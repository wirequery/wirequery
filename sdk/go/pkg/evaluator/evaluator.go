package evaluator

import (
	"encoding/json"
	"errors"
	"github.com/google/cel-go/cel"
	proto "github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
	"google.golang.org/protobuf/types/known/structpb"
	"reflect"
	"regexp"
	"strconv"
	"strings"
)

type CompiledQuery struct {
	QueryId            string
	AppName            string
	Method             string
	Path               string
	StatusCode         string
	Functions          []Function
	AggregatorFunction *Function
}

type Context struct {
	Method          string              `json:"method"`
	Path            string              `json:"path"`
	StatusCode      int                 `json:"statusCode"`
	QueryParameters map[string][]string `json:"queryParameters"`
	RequestBody     any                 `json:"requestBody"`
	ResponseBody    any                 `json:"responseBody"`
	RequestHeaders  map[string][]string `json:"requestHeaders"`
	ResponseHeaders map[string][]string `json:"responseHeaders"`
	Extensions      map[string]any      `json:"extensions"`
	TraceId         string              `json:"traceId"`
	StartTime       int64               `json:"startTime"`
	EndTime         int64               `json:"endTime"`
}

type Function struct {
	Type    string
	Program cel.Program
}

func CreateCelEnv() (*cel.Env, error) {
	return cel.NewEnv(
		cel.Variable("context", cel.AnyType),
		cel.Variable("it", cel.AnyType),
	)
}

func Eval(queries *[]CompiledQuery, inputContext Context) ([]*proto.QueryReport, error) {
	if !anyQueryMatchesResponse(queries, inputContext) {
		return nil, nil
	}
	var queryReports []*proto.QueryReport
	for _, query := range *queries {
		context := createContextObject(query, inputContext)
		if !queryMatchesResponse(query, inputContext) {
			continue
		}
		if len(query.Functions) == 0 {
			reports := createResultReport(query, context, inputContext)
			queryReports = append(queryReports, &reports)
		} else if results, err := executeFunctions(&query, context); err != nil {
			errorReport := createErrorReport(query, err)
			queryReports = append(queryReports, &errorReport)
		} else {
			for _, result := range results {
				reports := createResultReport(query, result, inputContext)
				queryReports = append(queryReports, &reports)
			}
		}
	}
	return queryReports, nil
}

func createContextObject(query CompiledQuery, context Context) map[string]interface{} {
	contextMap := map[string]interface{}{
		"method":          context.Method,
		"path":            query.Path,
		"queryParameters": context.QueryParameters,
		"statusCode":      context.StatusCode,
		"pathVariables":   matchPathVariables(query.Path, context.Path),
		"requestBody":     context.RequestBody,
		"requestHeaders":  context.RequestHeaders,
		"responseBody":    context.ResponseBody,
		"responseHeaders": context.ResponseHeaders,
		"extensions":      context.Extensions,
		"took":            context.EndTime - context.StartTime,
		"traceId":         context.TraceId,
	}
	return contextMap
}

func anyQueryMatchesResponse(queries *[]CompiledQuery, context Context) bool {
	anyQueryMatchesResponse := false
	for _, query := range *queries {
		if queryMatchesResponse(query, context) {
			anyQueryMatchesResponse = true
			break
		}
	}
	return anyQueryMatchesResponse
}

func executeFunctions(query *CompiledQuery, context map[string]interface{}) ([]any, error) {
	var previousResults = []any{nil}
	functionContext := map[string]interface{}{
		"context": context,
	}
	for i, function := range query.Functions {
		var nextPreviousResults []any
		for _, previousResult := range previousResults {
			if i == 0 {
				functionContext["it"] = context
			} else {
				functionContext["it"] = previousResult
			}
			result, err := executeFunction(function, functionContext)
			for _, subResult := range result {
				nextPreviousResults = append(nextPreviousResults, subResult)
			}
			if err != nil {
				return nil, err
			}
		}
		previousResults = nextPreviousResults
	}
	return previousResults, nil
}

// TODO test flatMap
func executeFunction(function Function, functionContext map[string]interface{}) ([]any, error) {
	if function.Type == "map" {
		result, _, err := function.Program.Eval(functionContext)
		if err != nil {
			return nil, err
		}
		nativeResult, err := result.ConvertToNative(reflect.TypeOf(&structpb.Value{}))
		if err != nil {
			return nil, err
		}
		return []any{nativeResult}, nil
	}
	if function.Type == "flatMap" {
		result, _, err := function.Program.Eval(functionContext)
		if err != nil {
			return nil, err
		}
		nativeResult, err := result.ConvertToNative(reflect.TypeOf(&structpb.Value{}))
		if err != nil {
			return nil, err
		}
		if v, ok := nativeResult.(*structpb.Value); ok {
			return v.GetListValue().AsSlice(), nil
		} else {
			println(result.Type().TypeName())
			return nil, errors.New("unable to flatMap a non-array")
		}
	}
	if function.Type == "filter" {
		result, _, err := function.Program.Eval(functionContext)
		if err != nil {
			return nil, err
		}
		// The result is always true or false, so need for conversion.
		if result.Value() == true {
			return []any{functionContext["it"]}, nil
		}
		return nil, nil
	}
	return nil, errors.New("unknown function: " + function.Type)
}

func createErrorReport(query CompiledQuery, err error) proto.QueryReport {
	return proto.QueryReport{
		QueryId: query.QueryId,
		Message: "{\"error\":\"" + err.Error() + "\"}",
	}
}

func createResultReport(query CompiledQuery, value any, inputContext Context) proto.QueryReport {
	if result, err := json.Marshal(value); err != nil {
		return createErrorReport(query, err)
	} else {
		return proto.QueryReport{
			QueryId:   query.QueryId,
			Message:   "{\"result\":" + string(result) + "}",
			TraceId:   inputContext.TraceId,
			StartTime: inputContext.StartTime,
			EndTime:   inputContext.EndTime,
		}
	}
}

func queryMatchesResponse(query CompiledQuery, context Context) bool {
	return statusCodeMatches(query.StatusCode, context.StatusCode) &&
		methodMatches(query.Method, context.Method) &&
		pathMatches(query.Path, context.Path)
}

func statusCodeMatches(codeMatcher string, actualCode int) bool {
	codeMatcherRegex := strings.ReplaceAll(codeMatcher, "x", "[0-9]")
	matched, _ := regexp.MatchString(codeMatcherRegex, strconv.Itoa(actualCode))
	return codeMatcher == "" || matched
}

func methodMatches(methodMatcher string, actualMethod string) bool {
	return methodMatcher == "" || strings.ToUpper(methodMatcher) == strings.ToUpper(actualMethod)
}

func pathMatches(pathMatcher string, actualPath string) bool {
	if pathMatcher == "" {
		return true
	}
	matchParts := strings.Split(pathMatcher, "/")
	actualParts := strings.Split(actualPath, "/")
	if len(matchParts) != len(actualParts) {
		return false
	}
	for i, matchPart := range matchParts {
		actualPart := actualParts[i]
		if strings.HasPrefix(matchPart, "{") && strings.HasSuffix(matchPart, "}") {
			continue
		}
		if actualPart != matchPart {
			return false
		}
	}
	return true
}

func matchPathVariables(pathMatcher string, actualPath string) interface{} {
	result := make(map[string]string)
	if pathMatcher == "" {
		return nil
	}
	matchParts := strings.Split(pathMatcher, "/")
	actualParts := strings.Split(actualPath, "/")
	if len(matchParts) != len(actualParts) {
		return nil
	}
	for i, matchPart := range matchParts {
		actualPart := actualParts[i]
		if strings.HasPrefix(matchPart, "{") && strings.HasSuffix(matchPart, "}") {
			result[strings.TrimRight(strings.TrimLeft(matchPart, "{"), "}")] = actualPart
			continue
		}
		if actualPart != matchPart {
			return nil
		}
	}
	return result
}

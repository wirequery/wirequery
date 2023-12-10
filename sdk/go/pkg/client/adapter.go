package client

import (
	"github.com/google/cel-go/cel"
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
	proto "github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
)

func CompileQuery(addQuery *proto.Query, env *cel.Env) (*evaluator.CompiledQuery, error) {
	var aggregatorFunction *evaluator.Function
	var functions []evaluator.Function
	if addQuery.AggregatorOperation != nil {
		function, err := toFunction(env, addQuery.AggregatorOperation.Name, addQuery.AggregatorOperation.CelExpression)
		if err != nil {
			return &evaluator.CompiledQuery{
				QueryId: addQuery.QueryId,
			}, err
		}
		aggregatorFunction = &function
	}
	for _, streamOperation := range addQuery.GetStreamOperations() {
		function, err := toFunction(env, streamOperation.Name, streamOperation.CelExpression)
		if err != nil {
			return &evaluator.CompiledQuery{
				QueryId: addQuery.QueryId,
			}, err
		}
		functions = append(functions, function)
	}
	compiledQuery := evaluator.CompiledQuery{
		QueryId:            addQuery.QueryId,
		AppName:            addQuery.QueryHead.AppName,
		Method:             addQuery.QueryHead.Method,
		Path:               addQuery.QueryHead.Path,
		StatusCode:         addQuery.QueryHead.StatusCode,
		Functions:          functions,
		AggregatorFunction: aggregatorFunction,
	}
	return &compiledQuery, nil
}

func toFunction(env *cel.Env, functionType string, celExpression string) (evaluator.Function, error) {
	if celExpression == "" {
		return evaluator.Function{
			Type: functionType,
		}, nil
	}
	ast, err := env.Compile(celExpression)
	if err.Err() != nil {
		return evaluator.Function{}, err.Err()
	}
	program, err2 := env.Program(ast)
	return evaluator.Function{
		Type:    functionType,
		Program: program,
	}, err2
}

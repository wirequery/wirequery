package evaluator

import (
	"github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
	"testing"
)

func Test_eval(t *testing.T) {
	type args struct {
		queries []CompiledQuery
		context Context
	}
	tests := []struct {
		name    string
		args    args
		want    []*wirequerypb.QueryReport
		wantErr bool
	}{
		{
			name: "base case",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Functions: []Function{
						compileMapFunction("1"),
					},
				}},
				context: Context{},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":1}",
			}},
		}, {
			name: "status code is passed",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Functions: []Function{
						compileFilterFunction("it.statusCode == 200"),
						compileMapFunction("1"),
					},
				}},
				context: Context{
					StatusCode: 200,
				},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":1}",
			}},
		}, {
			name: "Path variables are passed and matched",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Path:    "/abc/{x}",
					Functions: []Function{
						compileFilterFunction("it.pathVariables.x == 'def'"),
						compileMapFunction("it.path"),
					},
				}},
				context: Context{
					Path: "/abc/def",
				},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":\"/abc/{x}\"}",
			}},
		}, {
			name: "status code is taken into account if matching",
			args: args{
				queries: []CompiledQuery{{
					QueryId:    "qid",
					AppName:    "app",
					StatusCode: "200",
					Functions: []Function{
						compileMapFunction("1"),
					},
				}},
				context: Context{
					StatusCode: 200,
				},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":1}",
			}},
		}, {
			name: "status code is taken into account if matching with wildcards",
			args: args{
				queries: []CompiledQuery{{
					QueryId:    "qid",
					AppName:    "app",
					StatusCode: "20x",
					Functions: []Function{
						compileMapFunction("1"),
					},
				}},
				context: Context{
					StatusCode: 200,
				},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":1}",
			}},
		}, {
			name: "status code is ignored if not matching",
			args: args{
				queries: []CompiledQuery{{
					QueryId:    "qid",
					AppName:    "app",
					StatusCode: "200",
					Functions: []Function{
						compileMapFunction("1"),
					},
				}},
				context: Context{
					StatusCode: 300,
				},
			},
			want: []*wirequerypb.QueryReport{},
		}, {
			name: "Method is taken into account if matching",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Method:  "GET",
					Functions: []Function{
						compileMapFunction("1"),
					},
				}},
				context: Context{
					Method: "GET",
				},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":1}",
			}},
		}, {
			name: "Method is taken into account if not matching",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Method:  "GET",
					Functions: []Function{
						compileMapFunction("1"),
					},
				}},
				context: Context{
					Method: "POST",
				},
			},
			want: []*wirequerypb.QueryReport{},
		}, {
			name: "Path is taken into account if matching",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Path:    "/abc",
					Functions: []Function{
						compileMapFunction("1"),
					},
				}},
				context: Context{
					Path: "/abc",
				},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":1}",
			}},
		}, {
			name: "Path is taken into account if not matching",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Path:    "/def",
					Functions: []Function{
						compileMapFunction("1"),
					},
				}},
				context: Context{
					Path: "/abc",
				},
			},
			want: []*wirequerypb.QueryReport{},
		}, {
			name: "Path is taken into account if matching with wildcard",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Path:    "/abc/{def}",
					Functions: []Function{
						compileMapFunction("1"),
					},
				}},
				context: Context{
					Path: "/abc/bcd",
				},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":1}",
			}},
		}, {
			name: "expressions are ignored if filter is false",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Functions: []Function{
						compileFilterFunction("false"),
						compileMapFunction("1"),
					},
				}},
				context: Context{},
			},
			want: []*wirequerypb.QueryReport{},
		}, {
			name: "request and response body are passed",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Functions: []Function{
						compileFilterFunction("it.requestBody == 'abc' && it.responseBody == 'def'"),
						compileMapFunction("1"),
					},
				}},
				context: Context{
					RequestBody:  "abc",
					ResponseBody: "def",
				},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":1}",
			}},
		}, {
			name: "request and response body are transformed to json if possible",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Functions: []Function{
						compileFilterFunction("it.requestBody.a == true && it.responseBody.b == true"),
						compileMapFunction("1"),
					},
				}},
				context: Context{
					RequestBody:  map[string]interface{}{"a": true},
					ResponseBody: map[string]interface{}{"b": true},
				},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":1}",
			}},
		}, {
			name: "request and response headers are passed",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Functions: []Function{
						compileFilterFunction("it.requestHeaders['abc'] == ['def'] && it.responseHeaders['def'] == ['abc']"),
						compileMapFunction("1"),
					},
				}},
				context: Context{
					RequestHeaders:  map[string][]string{"abc": {"def"}},
					ResponseHeaders: map[string][]string{"def": {"abc"}},
				},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":1}",
			}},
		}, {
			name: "context contains headers, trace id, took and bodies",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Functions: []Function{
						compileMapFunction("context"),
					},
				}},
				context: Context{
					RequestBody:     "def",
					RequestHeaders:  map[string][]string{"abc": {"def"}},
					ResponseBody:    "abc",
					ResponseHeaders: map[string][]string{"def": {"abc"}},
					TraceId:         "123",
					StartTime:       30,
					EndTime:         40,
				},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"result\":{\"extensions\":{},\"method\":\"\",\"path\":\"\",\"pathVariables\":null,\"queryParameters\":{},\"requestBody\":\"def\",\"requestHeaders\":{\"abc\":[\"def\"]},\"responseBody\":\"abc\",\"responseHeaders\":{\"def\":[\"abc\"]},\"statusCode\":0,\"took\":10,\"traceId\":\"123\"}}",
			}},
		}, {
			name: "filter errors are passed back",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Functions: []Function{
						compileFilterFunction("it.responseBody.a.b"),
						compileMapFunction("true"),
					},
				}},
				context: Context{},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"error\":\"no such key: a\"}",
			}},
		}, {
			name: "transform errors are passed back",
			args: args{
				queries: []CompiledQuery{{
					QueryId: "qid",
					AppName: "app",
					Functions: []Function{
						compileFilterFunction("true"),
						compileMapFunction("it.responseBody.a.b"),
					},
				}},
				context: Context{},
			},
			want: []*wirequerypb.QueryReport{{
				QueryId: "qid",
				Message: "{\"error\":\"no such key: a\"}",
			}},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := Eval(&tt.args.queries, tt.args.context)
			if (err != nil) != tt.wantErr {
				t.Errorf("eval() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if !equalQueryReports(got, tt.want) {
				t.Errorf("eval()\n  got = %v\n want = %v", got, tt.want)
			}
		})
	}
}

func equalQueryReports(got []*wirequerypb.QueryReport, want []*wirequerypb.QueryReport) bool {
	if len(got) != len(want) {
		return false
	}
	for i := range got {
		if got[i].QueryId != want[i].QueryId {
			return false
		}
		if got[i].Message != want[i].Message {
			return false
		}
	}
	return true
}

func compileMapFunction(e string) Function {
	env, _ := CreateCelEnv()
	ast, _ := env.Compile(e)
	result, _ := env.Program(ast)
	return Function{"map", result}
}

func compileFilterFunction(e string) Function {
	env, _ := CreateCelEnv()
	ast, _ := env.Compile(e)
	result, _ := env.Program(ast)
	return Function{"filter", result}
}

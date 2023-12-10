package client

import (
	"github.com/google/cel-go/cel"
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
	proto "github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
	"reflect"
	"testing"
)

func TestCompileQuery(t *testing.T) {
	createdEnv, err := cel.NewEnv(
		cel.Variable("ctx", cel.AnyType),
		cel.Variable("it", cel.AnyType),
	)
	if err != nil {
		panic(err)
	}

	type args struct {
		addQuery *proto.Query
	}
	tests := []struct {
		name    string
		args    args
		want    *evaluator.CompiledQuery
		wantErr bool
	}{
		{
			name: "base case without functions",
			args: args{
				addQuery: &proto.Query{
					QueryId: "123",
					QueryHead: &proto.QueryHead{
						AppName:    "app",
						Method:     "GET",
						Path:       "/",
						StatusCode: "200",
					},
					StreamOperations:    []*proto.Operation{},
					AggregatorOperation: nil,
				},
			},
			want: &evaluator.CompiledQuery{
				QueryId:            "123",
				AppName:            "app",
				Method:             "GET",
				Path:               "/",
				StatusCode:         "200",
				Functions:          nil,
				AggregatorFunction: nil,
			},
		}, {
			name: "functions are transformed, no body",
			args: args{
				addQuery: &proto.Query{
					QueryId: "123",
					QueryHead: &proto.QueryHead{
						AppName:    "app",
						Method:     "GET",
						Path:       "/",
						StatusCode: "200",
					},
					StreamOperations: []*proto.Operation{
						{
							Name:          "map",
							CelExpression: "",
						},
					},
					AggregatorOperation: nil,
				},
			},
			want: &evaluator.CompiledQuery{
				QueryId:            "123",
				AppName:            "app",
				Method:             "GET",
				Path:               "/",
				StatusCode:         "200",
				Functions:          []evaluator.Function{{Type: "map"}},
				AggregatorFunction: nil,
			},
		}, {
			name: "stream functions are transformed, with body",
			args: args{
				addQuery: &proto.Query{
					QueryId: "123",
					QueryHead: &proto.QueryHead{
						AppName:    "app",
						Method:     "GET",
						Path:       "/",
						StatusCode: "200",
					},
					StreamOperations: []*proto.Operation{{
						Name:          "map",
						CelExpression: "it",
					}},
					AggregatorOperation: nil,
				},
			},
			want: &evaluator.CompiledQuery{
				QueryId:            "123",
				AppName:            "app",
				Method:             "GET",
				Path:               "/",
				StatusCode:         "200",
				Functions:          []evaluator.Function{{Type: "map", Program: toProgram(createdEnv, "it")}},
				AggregatorFunction: nil,
			},
		}, {
			name: "aggregator functions are transformed, with body",
			args: args{
				addQuery: &proto.Query{
					QueryId: "123",
					QueryHead: &proto.QueryHead{
						AppName:    "app",
						Method:     "GET",
						Path:       "/",
						StatusCode: "200",
					},
					StreamOperations: nil,
					AggregatorOperation: &proto.Operation{
						Name:          "map",
						CelExpression: "it",
					},
				},
			},
			want: &evaluator.CompiledQuery{
				QueryId:            "123",
				AppName:            "app",
				Method:             "GET",
				Path:               "/",
				StatusCode:         "200",
				Functions:          nil,
				AggregatorFunction: &evaluator.Function{Type: "map", Program: toProgram(createdEnv, "it")},
			},
		},
		// TODO add error edge cases...
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := CompileQuery(tt.args.addQuery, createdEnv)
			if (err != nil) != tt.wantErr {
				t.Errorf("CompileQuery() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if !reflect.DeepEqual(got, tt.want) {
				t.Errorf("CompileQuery() got = %v, want %v", got, tt.want)
			}
		})
	}
}

func toProgram(env *cel.Env, celExpression string) cel.Program {
	ast, err := env.Compile(celExpression)
	if err.Err() != nil {
		panic(err)
	}
	program, err2 := env.Program(ast)
	if err2 != nil {
		panic(err2)
	}
	return program
}

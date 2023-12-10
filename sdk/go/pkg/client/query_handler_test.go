package client

import (
	"github.com/stretchr/testify/assert"
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
	"github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
	"reflect"
	"testing"
	"time"
)

func Test_wireQueryClient_handleAddQuery(t *testing.T) {
	t.Run("it adds a query if there are no errors", func(t *testing.T) {
		client := mockWirequeryServiceClient{}
		w := WireQueryClient{client: &client}
		env, _ := evaluator.CreateCelEnv()

		uncompiledQuery, _ := &wirequerypb.Query{
			QueryId: "123",
			QueryHead: &wirequerypb.QueryHead{
				AppName:    "app",
				Method:     "GET",
				Path:       "/",
				StatusCode: "200",
			},
			StreamOperations:    []*wirequerypb.Operation{},
			AggregatorOperation: nil,
		}, env

		query, _ := CompileQuery(uncompiledQuery, env)

		w.handleAddQuery(env, &wirequerypb.QueryMutation_AddQuery{AddQuery: uncompiledQuery})

		got := w.queries
		want := []evaluator.CompiledQuery{*query}

		if !reflect.DeepEqual(got, want) {
			t.Errorf("CompileQuery() got = %v, want %v", got, want)
		}
		assert.Equal(t, 0, client.reportQueryResultsCnt)
	})

	t.Run("it does not add a query and reports error if there are errors", func(t *testing.T) {
		client := mockWirequeryServiceClient{}
		w := WireQueryClient{client: &client}
		env, _ := evaluator.CreateCelEnv()

		uncompiledQuery, _ := &wirequerypb.Query{
			QueryId: "123",
			QueryHead: &wirequerypb.QueryHead{
				AppName:    "app",
				Method:     "GET",
				Path:       "/",
				StatusCode: "200",
			},
			StreamOperations: []*wirequerypb.Operation{{
				Name:          "map",
				CelExpression: "this-is-an-invalid-expression",
			}},
			AggregatorOperation: nil,
		}, env

		_, _ = CompileQuery(uncompiledQuery, env)

		w.handleAddQuery(env, &wirequerypb.QueryMutation_AddQuery{AddQuery: uncompiledQuery})

		got := w.queries

		if got != nil {
			t.Errorf("CompileQuery() got = %v, want %v", got, nil)
		}
		assert.EventuallyWithT(t, func(c *assert.CollectT) {
			assert.Equal(t, 1, client.reportQueryResultsCnt)
		}, 1*time.Second, 10*time.Millisecond)
	})
}

func Test_wireQueryClient_handleRemoveQueryById(t *testing.T) {
	t.Run("it removes queries with the same query id", func(t *testing.T) {
		client := mockWirequeryServiceClient{}
		w := WireQueryClient{client: &client}
		env, _ := evaluator.CreateCelEnv()

		uncompiledQuery, _ := &wirequerypb.Query{
			QueryId: "123",
			QueryHead: &wirequerypb.QueryHead{
				AppName:    "app",
				Method:     "GET",
				Path:       "/",
				StatusCode: "200",
			},
			StreamOperations:    []*wirequerypb.Operation{},
			AggregatorOperation: nil,
		}, env

		query, _ := CompileQuery(uncompiledQuery, env)

		w.handleAddQuery(env, &wirequerypb.QueryMutation_AddQuery{AddQuery: uncompiledQuery})

		got := w.queries
		want := []evaluator.CompiledQuery{*query}

		if !reflect.DeepEqual(got, want) {
			t.Errorf("CompileQuery() got = %v, want %v", got, want)
		}
		assert.EventuallyWithT(t, func(c *assert.CollectT) {
			assert.Equal(t, 0, client.reportQueryResultsCnt)
		}, 1*time.Second, 10*time.Millisecond)
	})

	t.Run("it does not add a query and reports error if there are errors", func(t *testing.T) {
		client := mockWirequeryServiceClient{}
		w := WireQueryClient{client: &client}
		env, _ := evaluator.CreateCelEnv()

		uncompiledQuery, _ := &wirequerypb.Query{
			QueryId: "123",
			QueryHead: &wirequerypb.QueryHead{
				AppName:    "app",
				Method:     "GET",
				Path:       "/",
				StatusCode: "200",
			},
			StreamOperations: []*wirequerypb.Operation{{
				Name:          "map",
				CelExpression: "this-is-an-invalid-expression",
			}},
			AggregatorOperation: nil,
		}, env

		_, _ = CompileQuery(uncompiledQuery, env)

		w.handleAddQuery(env, &wirequerypb.QueryMutation_AddQuery{AddQuery: uncompiledQuery})

		got := w.queries

		if got != nil {
			t.Errorf("CompileQuery() got = %v, want %v", got, nil)
		}

		assert.EventuallyWithT(t, func(c *assert.CollectT) {
			assert.Equal(t, 1, client.reportQueryResultsCnt)
		}, 1*time.Second, 10*time.Millisecond)
	})
}

package client

import (
	"encoding/json"
	"github.com/stretchr/testify/assert"
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
	proto "github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
	"reflect"
	"testing"
	"time"
)

func Test_wireQueryClient_handleAddQuery(t *testing.T) {
	t.Run("it adds a query if there are no errors", func(t *testing.T) {
		client := mockWirequeryServiceClient{}
		w := WireQueryClient{client: &client}
		env, _ := evaluator.CreateCelEnv()

		uncompiledQuery, _ := &proto.Query{
			QueryId: "123",
			QueryHead: &proto.QueryHead{
				AppName:    "app",
				Method:     "GET",
				Path:       "/",
				StatusCode: "200",
			},
			StreamOperations:    []*proto.Operation{},
			AggregatorOperation: nil,
		}, env

		query, _ := CompileQuery(uncompiledQuery, env)

		w.handleAddQuery(env, &proto.QueryMutation_AddQuery{AddQuery: uncompiledQuery})

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

		uncompiledQuery, _ := &proto.Query{
			QueryId: "123",
			QueryHead: &proto.QueryHead{
				AppName:    "app",
				Method:     "GET",
				Path:       "/",
				StatusCode: "200",
			},
			StreamOperations: []*proto.Operation{{
				Name:          "map",
				CelExpression: "this-is-an-invalid-expression",
			}},
			AggregatorOperation: nil,
		}, env

		_, _ = CompileQuery(uncompiledQuery, env)

		w.handleAddQuery(env, &proto.QueryMutation_AddQuery{AddQuery: uncompiledQuery})

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

		uncompiledQuery, _ := &proto.Query{
			QueryId: "123",
			QueryHead: &proto.QueryHead{
				AppName:    "app",
				Method:     "GET",
				Path:       "/",
				StatusCode: "200",
			},
			StreamOperations:    []*proto.Operation{},
			AggregatorOperation: nil,
		}, env

		query, _ := CompileQuery(uncompiledQuery, env)

		w.handleAddQuery(env, &proto.QueryMutation_AddQuery{AddQuery: uncompiledQuery})

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

		uncompiledQuery, _ := &proto.Query{
			QueryId: "123",
			QueryHead: &proto.QueryHead{
				AppName:    "app",
				Method:     "GET",
				Path:       "/",
				StatusCode: "200",
			},
			StreamOperations: []*proto.Operation{{
				Name:          "map",
				CelExpression: "this-is-an-invalid-expression",
			}},
			AggregatorOperation: nil,
		}, env

		_, _ = CompileQuery(uncompiledQuery, env)

		w.handleAddQuery(env, &proto.QueryMutation_AddQuery{AddQuery: uncompiledQuery})

		got := w.queries

		if got != nil {
			t.Errorf("CompileQuery() got = %v, want %v", got, nil)
		}

		assert.EventuallyWithT(t, func(c *assert.CollectT) {
			assert.Equal(t, 1, client.reportQueryResultsCnt)
		}, 1*time.Second, 10*time.Millisecond)
	})
}

func Test_wireQueryClient_handleQueryOneTrace(t *testing.T) {
	t.Run("it does not send anything when trace is not in cache", func(t *testing.T) {
		client := mockWirequeryServiceClient{}
		w := WireQueryClient{client: &client}

		w.handleQueryOneTrace(&proto.QueryMutation_QueryOneTrace{
			QueryOneTrace: &proto.QueryOneTrace{
				QueryId: "456",
				TraceId: "789",
			},
		})

		time.Sleep(10 * time.Millisecond) // Sleep as the effect should happen in a goroutine
		assert.Equal(t, 0, client.reportQueryResultsCnt)
	})

	t.Run("it sends back the trace from cache", func(t *testing.T) {
		client := mockWirequeryServiceClient{}
		w := WireQueryClient{client: &client}

		context := evaluator.Context{StartTime: 10, EndTime: 20, TraceId: "123"}
		marshalled, _ := json.Marshal(&context)

		PutCache("123", &context)
		w.handleQueryOneTrace(&proto.QueryMutation_QueryOneTrace{
			QueryOneTrace: &proto.QueryOneTrace{
				QueryId: "456",
				TraceId: "123",
			},
		})
		assert.EventuallyWithT(t, func(c *assert.CollectT) {
			assert.Equal(t, 1, client.reportQueryResultsCnt)
		}, 1*time.Second, 10*time.Millisecond)

		want := proto.QueryReports{QueryReports: []*proto.QueryReport{{
			QueryId:   "456",
			Message:   "{\"result\":" + string(marshalled) + "}",
			StartTime: 10,
			EndTime:   20,
			TraceId:   "123",
		}}}
		got := client.reportQueryResultsIn[0]

		if !reflect.DeepEqual(got.QueryReports, want.QueryReports) {
			t.Errorf("CompileQuery() got = %v, want %v", got.QueryReports, want.QueryReports)
		}
	})
}

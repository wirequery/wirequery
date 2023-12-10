package client

import (
	"github.com/stretchr/testify/assert"
	proto "github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
	"reflect"
	"testing"
)

func Test_wireQueryClient_ReportError(t *testing.T) {
	t.Run("it reports errors wrapped in error JSON", func(t *testing.T) {
		w := WireQueryClient{appName: "someapp", apiKey: "123-456"}
		client := mockWirequeryServiceClient{}
		w.ReportError("123", "some error")
		assert.Equal(t, 1, len(client.reportQueryResultsIn))

		got := client.reportQueryResultsIn
		want := []*proto.QueryReports{{
			QueryReports: []*proto.QueryReport{{
				QueryId: "123",
				Message: "{\"error\":\"some error\"}",
			}},
			ApiKey:  "123-456",
			AppName: "someapp",
		}}

		if !reflect.DeepEqual(got, want) {
			t.Errorf("CompileQuery() got = %v, want %v", got, want)
		}
	})
}

func Test_wireQueryClient_ReportResult(t *testing.T) {
	t.Run("it reports results wrapped in result JSON", func(t *testing.T) {
		w := WireQueryClient{appName: "someapp", apiKey: "123-456"}
		client := mockWirequeryServiceClient{}
		w.ReportResult([]*proto.QueryReport{{
			QueryId: "123",
			Message: "{\"result\":\"\\\"xyz\\\"\"}",
		}})
		assert.Equal(t, 1, len(client.reportQueryResultsIn))

		got := client.reportQueryResultsIn
		want := []*proto.QueryReports{{
			QueryReports: []*proto.QueryReport{{
				QueryId: "123",
				Message: "{\"result\":\"\\\"xyz\\\"\"}",
			}},
			ApiKey:  "123-456",
			AppName: "someapp",
		}}

		if !reflect.DeepEqual(got, want) {
			t.Errorf("CompileQuery() got = %v, want %v", got, want)
		}
	})
}

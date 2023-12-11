package client

import (
	"context"
	"github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
	"google.golang.org/grpc"
)

type mockWirequeryServiceClient struct {
	listenForQueriesCnt   int
	reportQueryResultsCnt int

	listenForQueriesIn   []*wirequerypb.ListenForQueriesRequest
	reportQueryResultsIn []*wirequerypb.QueryReports
}

func (m *mockWirequeryServiceClient) ListenForQueries(ctx context.Context, in *wirequerypb.ListenForQueriesRequest, opts ...grpc.CallOption) (wirequerypb.WirequeryService_ListenForQueriesClient, error) {
	m.listenForQueriesCnt = m.listenForQueriesCnt + 1
	m.listenForQueriesIn = append(m.listenForQueriesIn, in)
	return nil, nil
}

func (m *mockWirequeryServiceClient) ReportQueryResults(ctx context.Context, in *wirequerypb.QueryReports, opts ...grpc.CallOption) (*wirequerypb.Empty, error) {
	m.reportQueryResultsCnt = m.reportQueryResultsCnt + 1
	m.reportQueryResultsIn = append(m.reportQueryResultsIn, in)
	return nil, nil
}

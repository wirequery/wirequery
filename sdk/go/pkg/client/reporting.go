package client

import (
	"encoding/json"
	proto "github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
)

type errorResult struct {
	Error string `json:"error"`
}

func (w *WireQueryClient) ReportError(queryId string, message string) {
	go func() {
		res, err := json.Marshal(errorResult{message})
		if err != nil {
			panic(err)
		}
		queryReports := proto.QueryReports{
			QueryReports: []*proto.QueryReport{{
				QueryId: queryId,
				Message: string(res),
			}},
			ApiKey:  w.apiKey,
			AppName: w.appName,
		}
		w.client.ReportQueryResults(w.ctx, &queryReports)
	}()
}

func (w *WireQueryClient) ReportResult(queryReportIn []*proto.QueryReport) {
	go func() {
		queryReports := proto.QueryReports{
			QueryReports: queryReportIn,
			ApiKey:       w.apiKey,
			AppName:      w.appName,
		}
		w.client.ReportQueryResults(w.ctx, &queryReports)
	}()
}

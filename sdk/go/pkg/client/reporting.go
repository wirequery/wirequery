package client

import (
	"encoding/json"
	. "github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
)

type errorResult struct {
	Error string `json:"error"`
}

type result struct {
	Result any `json:"result"`
}

func (w *WireQueryClient) ReportError(queryId string, message string) error {
	res, err := json.Marshal(errorResult{message})
	if err != nil {
		panic(err)
	}
	queryReports := QueryReports{
		QueryReports: []*QueryReport{{
			QueryId: queryId,
			Message: string(res),
		}},
		ApiKey:  w.apiKey,
		AppName: w.appName,
	}
	_, err = w.client.ReportQueryResults(w.ctx, &queryReports)
	return err
}

func (w *WireQueryClient) ReportResult(queryReportIn []*QueryReport) error {
	queryReports := QueryReports{
		QueryReports: queryReportIn,
		ApiKey:       w.apiKey,
		AppName:      w.appName,
	}
	_, err := w.client.ReportQueryResults(w.ctx, &queryReports)
	return err
}

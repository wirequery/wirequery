# Go

!!IMPORTANT: this SDK is highly experimental. There is no built-in masking!!

In order to integrate WireQuery with Go, you need to connect and listen to WireQuery Server in the background.
```
import (
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
)

wqsClient := client.Listen(host, appName, apiKey)
```
Then, in your server middleware, you need to intercept each request and process that data:
```
import (
	"github.com/wirequery/wirequery/sdk/go/pkg/client"
)

    // ... Process the request. Then:

    context := evaluator.Context{
            Method:          method,
            Path:            path,
            StatusCode:      statusCode,
            QueryParameters: queryParameters,
            RequestBody:     requestBody,
            ResponseBody:    responseBody,
            RequestHeaders:  requestHeaders,
            ResponseHeaders: responseHeaders,
            Extensions:      extensions,
            TraceId:         traceId,
            StartTime:       startTime,
            EndTime:         endTime,
    }

    // Cache the request/response for future Extended Tracing lookup.
    client.PutCache(body.TraceId, &context)

    // Run the queries against the context and report the results.
    queries := wqsClient.GetQueries()
    results, err := evaluator.Eval(&queries, context)
    wqsClient.ReportResult(results)

    // ...
```
Note that sensitive data needs to be masked before sending it to the WireQuery Server.

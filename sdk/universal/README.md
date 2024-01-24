# Universal

!!IMPORTANT: this SDK is highly experimental. There is no built-in masking!!

WireQuery Universal Daemon is a program that allows any application to easily connect to WireQuery.

## How It Works

WireQuery Universal Daemon connects to WireQuery to fetch the queries for the specified applications. Whenever queries are sent from
WireQuery to WireQuery Universal Daemon, the list of queries that run over every request is updated.

In the meanwhile, applications can send a so-called Context object to WireQuery Universal Daemon. Whenever a context object is received,
WireQuery Universal Daemon will run all queries against that object and send the results back to WireQuery.

## Installation

Make sure you have Go 1.19 or later installed and run:
```
go install github.com/wirequery/wirequery/sdk/go/cmd/universal
```

## Running WireQuery Universal Daemon

WireQuery Universal Daemon should be run on the same machine as your application.
```
wirequery --appName <appName> --apiKey <apiKey> --port <port> --host <host>
```
For example:
```
wirequery --appName dummy --apiKey 123-456-789 --port 8091 --host grpc.wirequery.io
```

## Usage

Applications wanting to connect to WireQuery should send the following information to WireQuery Universal Daemon with
each request, whereas the request's sensitive data is masked before sending the data to WireQuery Universal Daemon.
Masked data should be replaced by the term "MASKED".
```
POST api/v1/events

Content-Type: application/json

{
  "method": "<method>",
  "path": "<path>",
  "statusCode": <statusCode>,
  "queryParameters": <queryParameters>,
  "requestBody": <requestBody>,
  "responseBody": <responseBody>,
  "requestHeaders": <requestHeaders>,
  "responseHeaders": <responseHeaders>,
  "extensions": <extensions>,
  "startTime": <startTime>,
  "endTime": <endTime>,
  "traceId": <traceId>
}
```

Here:
- `<method>` is the method of the HTTP call, e.g. `POST`
- `<path>` is the path of the HTTP call, e.g. `/api/users/1`
- `<statusCode>` is the status code of the HTTP call, e.g. `200`
- `<queryParamters>` is an object of query parameters, e.g. `{ "searchTerms": [ "some", "term"], "something": "else"  }`
- `<requestBody>` is an object containing the request body, e.g. `{ "username": "wouter" }`
- `<responseBody>` is an object containing the response body, e.g. `{ "username": "wouter" }`
- `<extensions>` is an object containing custom extensions, e.g. `{ "upstreamCalls": [], "logs": [] }`
- `<startTime>` is the start timestamp of the request in milliseconds
- `<endTime>` is the end timestamp of the request in milliseconds
- `<traceId>` is the trace id of the request, which is needed for Extended Tracing


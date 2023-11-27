# Writing Queries

Queries consist of three parts:

- Query Head
- Stream Operations
- Aggregation Operations

## Query Head

A query always starts with the head. The head contains high-over information about what needs to be captured.

The query head first starts with the name of the application, followed by zero or more additional parts:

- *Method*: the method of the request, e.g. `GET`
- *Path*: the path of the request including path variables that can be extracted for later use, e.g. `/users/{id}`
- *Status Code*: the status code of the request with wild cards specified by an 'x', e.g. `2xx`

These parts can be in any order, e.g.:

- ```GET 2xx /users/{id}```
- ```302 POST```
- ```/users/{id}```

## Stream Operations

The result of the head can be piped using zero or more `|`-operators, and be picked up by "stream operators".

There are three stream operators:

- `map`: for each input, apply the provided transformation
- `flatMap`: for each input, apply the provided transformation and flatten the result
- `filter`: keep the input if and only if the result is true

Each stream operator has access to two variables:

- `context`: the initially provided input
- `it`: the result of the previous pipe operation (for the first operator, `context` and `it` are the same)

The `context` (and initially `it`) consists of the following variables:

- `method`
- `statusCode`
- `path`
- `queryParameters`
- `requestBody`
- `requestHeaders`
- `responseBody`
- `responseHeaders`
- `extensions`

All stream operations are followed by an expression. These expressions are based on Google CEL. More information about
how to write Google CEL expressions can be found [here](https://github.com/google/cel-spec).

For example:

```
order-service POST 2xx /orders | filter it.responseBody.totalAmount > 100 | map it.responseBody.totalAmount
```

## Aggregation Operations

Finally, queries may specify an aggregation operation as the last stream operation of a query. Currently, there is only
one aggregation operation:

- `distinct`: return values that have not been returned before

For example:

```
user-service GET 2xx /users | flatMap it.map(user, user.authorisations) | distinct
```

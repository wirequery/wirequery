Analyse your application's requests and responses using a powerful and easy-to-use query language. WireQuery provides
mechanisms to protect your data, and does not compromise on security or performance.

# SDKs

The SDKs for WireQuery can be found at:

| Technology      | Description                            |
|-----------------|----------------------------------------|
| [JVM](/sdk/jvm) | SDK for Java Core, Spring Boot 2 and 3 |

# Writing Queries

WireQuery utilizes a WQEL (WireQuery Expression Language) to write queries.

Queries consist of three parts:

- Head
- Stream Operations
- Aggregation Operations

## Head

A WQEL expression always starts with the head. The head contains high-over information about what needs to be captured.

It can consist of the following parts:

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

For example:

```
POST 2xx /orders | filter it.responseBody.totalAmount > 100 | map it.responseBody.totalAmount
```

## Aggregation Operations

Finally, queries may specify an aggregation operation as the last stream operation of a query. Currently, there is only
one aggregation operation:

- `distinct`: return values that have not been returned before

For example:

```
GET 2xx /users | flatMap it.map(user, user.authorisations) | distinct
```

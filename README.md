Analyse your application's requests and responses using a powerful and easy-to-use query language. WireQuery provides
mechanisms to protect your data, and does not compromise on security or performance.

# Getting Started

The fastest way to get started is to add a Spring Boot Starter to your Spring Boot project. Depending on your version of
Spring Boot, you can use one of two starters:

| Technology     | Group Id      | Artifact Id                     |
|----------------|---------------|---------------------------------|
| Spring Boot 2  | com.wirequery | wirequery-spring-boot-2-starter |
| Spring Boot 3  | com.wirequery | wirequery-spring-boot-3-starter |

This will automatically configure WireQuery for WebMVC, logging all requests using the logger mechanisms provided by
Spring Boot.

The next step is to define queries that you would like to monitor. For example, if you have an endpoint that retrieves
all users and you want to log information about them, you can use the `wirequery.queries` property for it:

```
wirequery:
  queries:
    - name: zero-order-amounts
      query: GET 2xx /orders | filter it.responseBody.amount == 0
```

If you run your program, however, you will notice that everything is masked. By default, every field in the headers,
request and response body are masked to ensure privacy. This may be controlled by applying `@Mask` and `@Unmask` onto
request / response objects and their fields. For example, one could define:

```
import com.wirequery.core.annotations.Mask
import com.wirequery.core.annotations.Unmask

@Mask // Not necessary, unless unmaskByDefault is set to true.
class User {
    @Unmask
    String username;
    String password;
}
```

Congratulations! You have successfully configured WireQuery to intercept requests and report them in a privacy aware
manner.

However, WireQuery can do much more than that. For instance, using `extensions`, you can attach outgoing requests,
timing information, information about the internal state, etc., to a query's context.

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

# Extending WireQuery

WireQuery can be extended to provide more information than only the incoming request and response, using extensions. In
the Spring variant of WireQuery, one can achieve this using the `RequestData` object, which can be injected into a bean.
The `RequestData` object, then, contains a method for setting an extension. For example:

```
requestData.putExtension("outgoingRequests", outgoingRequests);
```

It may be a good idea to apply the same masking techniques used within the rest of the application. Therefore, you can
inject or instantiate one of the following services to help you mask your objects:

- `HeadersMasker`: allows you to mask request and response headers
- `ObjectMasker`: allows you to mask objects

For example:

```
var maskedRequestHeaders = headersMasker.maskRequestHeaders(myRequestHeaders);
var maskedResponseHeaders = headersMasker.maskResponseHeaders(myResponseHeaders);
var maskedObject = objectMasker.mask(myObject);
```

# Configuration

The following settings can be used to configure WireQuery in your `application.yml` file:

| Property Name                          | Sub fields  | Default | Description                                                                                     |
|----------------------------------------|-------------|---------|-------------------------------------------------------------------------------------------------|
| wirequery.queries                      | name, query | []      | List of queries by name and query                                                               |
| wirequery.maskSettings.unmaskByDefault |             | false   | If set to true, unmask everything by default                                                    |
| wirequery.maskSettings.requestHeaders  |             | []      | List of request headers to be masked or unmasked depending on whether `unmaskByDefault` is set  |
| wirequery.maskSettings.responseHeaders |             | []      | List of response headers to be masked or unmasked depending on whether `unmaskByDefault` is set |

Example:

```
wirequery:
  queries:
    - name: zero-order-amounts
      query: GET 2xx /orders | filter it.responseBody.amount == 0
  maskSettings:
    unmaskByDefault: false
    requestHeaders:
      - Accept
    responseHeaders:
      - Content-Type
```

# Limitations

Current limitations include:

- If the request body is malformed, it cannot be parsed and therefore not intercepted.

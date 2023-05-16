# Getting Started

The fastest way to get started is to add a Spring Boot Starter to your Spring Boot project. Depending on your version of
Spring Boot, you can use one of two starters:

| Technology    | Group Id      | Artifact Id                     |
|---------------|---------------|---------------------------------|
| Spring Boot 2 | com.wirequery | wirequery-spring-boot-2-starter |
| Spring Boot 3 | com.wirequery | wirequery-spring-boot-3-starter |

This will automatically configure WireQuery for WebMVC, logging all requests using the logger mechanisms provided by
Spring Boot.

While adding the WireQuery SDK into your project, it may be useful to try out certain queries on your local machine
before connecting to WireQuery.

As such, the next step is to define queries that you would like to monitor. For example, if you have an endpoint that
retrieves all users and you want to log information about them, you can use the `wirequery.queries` property for it:

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
import Mask
import Unmask

@Mask // Not necessary, unless unmaskByDefault is set to true.
class User {
    @Unmask
    String username;
    String password;
}
```

Now that we have successfully implemented the WireQuery SDK, let's connect to WireQuery.

First, either remove the `queries` part from the `application.yml` configuration file or move it to
e.g. `application-dev.yml`, so you have a separate profile that still uses the manually defined queries.

Next, add the following properties under `wirequery:`:

```
  connection:
    secure: <secure>
    host: <host>
    appName: <appName>
    apiKey: <apiKey>
```

Here:

- `<secure>` (true by default) needs to be set to false when connecting to an instance of WireQuery over http (e.g. when
  WireQuery is running locally).
- `<host>` is the path to WireQuery
- `<appName>` is the identifying name of your application
- `<appName>` is the app's api key

That's it, you have now successfully connected your app to WireQuery!

# Additional Configuration

In some cases, additional configuration may be needed for WireQuery.

## Masking using application config

Besides `@Mask` and `@Unmask`, you can also mask and unmask fields using the `wirequery.maskSettings.classes` property.
This property takes a list of objects containing:

- `mask` - Whether or not to mask (unless overridden by field settings) all fields in this class
- `unmask` - Whether or not to unmask (unless overridden by field settings) all fields in this class
- `name` - The fully qualified name of the class
- `fields` - A list of fields to set masking rules
    - `mask` - Whether or not to mask this field
    - `unmask` - Whether or not to unmask this field
    - `name` - The name of the field

Here, `mask` and `unmask` cannot be used at the same time.

For example, the following example would unmask every field in the `Transaction` class, except for `description`:

```
  maskSettings:
    classes:
      - unmask: true
        name: com.transactions.transaction.Transaction
        fields:
          - name: description
            mask: true
```

## Limiting Access

Access to resources can be limited using either the `allowedResources` or `unallowedResources` properties. These
properties cannot be used at the same time. Both properties take a list of objects containing the path and a list of
methods (as strings). Paths can have wildcards using `{...}` (match until the next `/`) or `**` (match everything).

For example:

```
wirequery:
  ...
  allowedResources:
    - path: /transactions
      methods:
        - GET
    - path: /transactions/{transactionId}
      methods:
        - PUT
    - path: /transactions/**
      methods:
        - POST
```

## Extending WireQuery

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

# Configuration Properties

The following settings can be used to configure WireQuery in your `application.yml` file:

| Property Name                          | Sub fields                 | Default | Description                                                                                           |
|----------------------------------------|----------------------------|---------|-------------------------------------------------------------------------------------------------------|
| wirequery.queries                      | name, query                | []      | List of queries by name and query                                                                     |
| wirequery.maskSettings.unmaskByDefault |                            | false   | If set to true, unmask everything by default                                                          |
| wirequery.maskSettings.requestHeaders  |                            | []      | List of request headers to be masked or unmasked depending on whether `unmaskByDefault` is set        |
| wirequery.maskSettings.responseHeaders |                            | []      | List of response headers to be masked or unmasked depending on whether `unmaskByDefault` is set       |
| wirequery.maskSettings.classes         | mask, unmask, name, fields | []      | List of classes that also need to be masked. Can be used instead of annotations, or to override them. |
| wirequery.allowedResources             | path, methods              | null    | Determines which resources are allowed to be accessed.                                                |
| wirequery.unallowedResources           | path, methods              | null    | Determines which resources are allowed to be accessed.                                                |

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

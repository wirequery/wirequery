# Java Virtual Machine

## Adding Dependencies

In case you are using Spring Boot in your application, The easiest way to get started with WireQuery is to add a Spring
Boot Starter to your project. Depending on your version of Spring Boot, you can use one of two starters:

| Technology    | Group Id      | Artifact Id                     |
|---------------|---------------|---------------------------------|
| Spring Boot 2 | com.wirequery | wirequery-spring-boot-2-starter |
| Spring Boot 3 | com.wirequery | wirequery-spring-boot-3-starter |

(**Note:** If you're not using Spring Boot, you can depend on either the Spring 5 or Spring 6 dependencies, or the Java
Core dependency if you are not using Spring at all. Setting up WireQuery will take a bit more time, but the Spring Boot
starters may act as a source of inspiration on how to set it up in your project. The rest of the article will assume
that you are using one of the Spring Boot Starters.)

## Setting up the Connection

Next, add the following properties to your `application.yml`:

```
wirequery:
  connection:
    secure: <secure>
    host: <host>
    appName: <appName>
    apiKey: <apiKey>
```

Here:

- `<secure>` (`true` by default) needs to be set to `false` when connecting to an instance of WireQuery over http (e.g.
  when WireQuery is running locally).
- `<host>` is the path to WireQuery
- `<appName>` is the identifying name of your application
- `<apiKey>` is the app's API key

## Masking

If you run your program, however, you will notice that everything is masked. By default, every field in the headers,
request and response body are masked to ensure privacy. This may be controlled by applying `@Mask` and `@Unmask` onto
request / response objects and their fields. For example, one could define:

```
import com.wirequery.core.annotations.Mask;
import com.wirequery.core.annotations.Unmask;

@Unmask
public class User {
    String username;
    @Mask
    String password;
    
    // ...
}
```

## Additional Configuration

### Masking using Application Config

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

For example, the following example would unmask every field in the `Product` class, except for `description`:

```
wirequery:
  ...
  maskSettings:
    classes:
      - unmask: true
        name: com.products.product.Product
        fields:
          - name: description
            mask: true
```

### Limiting Access

Access to resources can be limited using either the `allowedResources` or `unallowedResources` properties. These
properties cannot be used at the same time. Both properties take a list of objects containing the path and a list of
methods (as strings). Paths can have wildcards using `{...}` (match until the next `/`) or `**` (match everything).

For example:

```
wirequery:
  ...
  allowedResources:
    - path: /products
      methods:
        - GET
    - path: /products/{productId}
      methods:
        - PUT
    - path: /products/**
      methods:
        - POST
```

### Extending WireQuery

WireQuery can be extended to provide more information than only the incoming request and response using extensions.
You can extend WireQuery by using the `RequestData` object, which can be injected into a bean. The `RequestData` object,
then, contains a method for setting an extension. For example:

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

## Configuration Properties

The following settings can be used to configure WireQuery in your `application.yml` file:

| Property Name                          | Sub fields                 | Default | Description                                                                                           |
|----------------------------------------|----------------------------|---------|-------------------------------------------------------------------------------------------------------|
| wirequery.maskSettings.unmaskByDefault |                            | false   | If set to true, unmask everything by default                                                          |
| wirequery.maskSettings.requestHeaders  |                            | []      | List of request headers to be masked or unmasked depending on whether `unmaskByDefault` is set        |
| wirequery.maskSettings.responseHeaders |                            | []      | List of response headers to be masked or unmasked depending on whether `unmaskByDefault` is set       |
| wirequery.maskSettings.classes         | mask, unmask, name, fields | []      | List of classes that also need to be masked. Can be used instead of annotations, or to override them. |
| wirequery.allowedResources             | path, methods              | null    | Determines which resources are allowed to be accessed.                                                |
| wirequery.unallowedResources           | path, methods              | null    | Determines which resources are allowed to be accessed.                                                |

## Limitations

Current limitations include:

- If the request body is malformed, it cannot be parsed and therefore not intercepted. As such, it will end up as `null` in the `context`.

## Examples

The following examples demonstrate how WireQuery can be used within a Spring Boot application:

- [Products Service](https://github.com/wirequery/wirequery/tree/main/sdk/jvm/examples/spring-boot/products) - simulates a product catalogue.
- [Basket Service](https://github.com/wirequery/wirequery/tree/main/sdk/jvm/examples/spring-boot/balance-calculator) - simulates an order basket. Connects to the products service.

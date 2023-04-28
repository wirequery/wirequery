Analyse your application's requests and responses without compromising on privacy, security or performance.

Using WireQuery, you can define powerful queries such as:
```
GET 2xx /users | map it.responseBody
```
And have the results reported back to you whenever this query is triggered.

# Getting Started
The fastest way to get started is to add a Spring Boot Starter to your Spring Boot project.

## Adding the dependency
Depending on your Spring Boot version, you can add the following dependency and set it to the latest version.

### Spring Boot 2 - Maven
```
<dependency>
  <groupId>com.wirequery</groupId>
  <artifactId>wirequery-spring-boot-2-starter</artifactId>
  <scope>runtime</scope>
  <version>...</version>
</dependency>
```

### Spring Boot 2 - Gradle
```
implementation 'com.wirequery:wirequery-spring-boot-2-starter:...'
```

### Spring Boot 2 - Gradle Kotlin
```
implementation("com.wirequery:wirequery-spring-boot-2-starter:...")
```

### Spring Boot 3 - Maven
```
<dependency>
  <groupId>com.wirequery</groupId>
  <artifactId>wirequery-spring-boot-2-starter</artifactId>
  <scope>runtime</scope>
  <version>...</version>
</dependency>
```

### Spring Boot 3 - Gradle
```
implementation 'com.wirequery:wirequery-spring-boot-3-starter:...'
```

### Spring Boot 3 - Gradle Kotlin
```
implementation("com.wirequery:wirequery-spring-boot-3-starter:...")
```

## Adding Queries
After adding the correct dependency, you can add WireQuery queries to your `application.yml` file. For example, if you have a `users` endpoint, you could write the following:
```
wirequery:
  queries:
    - name: basic
      query: GET 2xx /users | map it.responseBody
```
This query will log the response body every time the `/users` endpoint is hit with a 2xx status code and the GET method.

## Masking
By default, every field in the headers, request and response body are masked to ensure privacy. This may be controlled by applying `@Mask` and `@Unmask` onto request / response objects and their fields. For example, one could define:

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

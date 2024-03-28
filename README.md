[![Docker Images](https://img.shields.io/badge/docker-latest-blue)](https://github.com/orgs/wirequery/packages?repo_name=wirequery&ecosystem=container)
[![Maven Central](https://img.shields.io/maven-central/v/com.wirequery/wirequery-spring-boot-3-starter)](https://central.sonatype.com/search?q=com.wirequery)
[![NPM](https://img.shields.io/npm/v/@wirequery/wirequery-js-core)](https://www.npmjs.com/package/@wirequery/wirequery-js-core)

# WireQuery

WireQuery is world's first *full-stack* session replay and network exploration tool. Using WireQuery, you can see how a
user experiences an issue through a video-like frontend recording. Combined with an overview of the network calls to
the backend (including network calls further upstream and their actual payload), you get a holistic overview
of how an issue came into existence.

Some issues, however, can only be found on the backend, before users have reported them with a video-like
recording. Through a specialized “network query language” called WQL, you can easily start your investigation from the
backend as well. Like before, this includes all the network calls up- and downstream of your query result.

Since the video-like recordings and network calls may contain sensitive information, WireQuery’s SDKs are designed with
privacy in mind. In most cases, minimum effort is required to strip all sensitive data from your frontend and backend
systems.

⭐ If you like WireQuery, please consider giving it a star. Your support can help the project grow and deliver exciting
features.

Also, if you have any questions or feedback, feel free to raise an Issue.

<a href="https://youtu.be/lt-9KZOFffA?si=BiVf9Onhmeg_Za1w"><img src="screenshot_3_with_play_btn.png"></a>

## Getting Started

If you want to try out WireQuery on your local machine:

1. Make sure Docker is installed and run the following commands in your terminal:
    ```
    mkdir wirequery
    cd wirequery
    curl -O https://raw.githubusercontent.com/wirequery/wirequery/main/docker-compose.yml
    curl -O https://raw.githubusercontent.com/wirequery/wirequery/main/nginx.conf
    docker-compose up
    ```
2. Wait until both the backend and frontend are initialized and navigate to `localhost:8090`. Log in with `admin` / `admin` and update your password in the Settings.
3. Start using WireQuery by creating an application and connecting to WireQuery using one of the SDKs below.

If you wish to install WireQuery on a server, please follow the [Server Installation](https://www.wirequery.io/docs/introduction/server-installation) instructions.

## SDKs

In order to connect your application to WireQuery, you can use one of WireQuery's SDKs. WireQuery's SDKs are offered in the following variants:

| Technology                  | Description                                                       | Notes                                                        | Resources                                            |
|-----------------------------|-------------------------------------------------------------------|--------------------------------------------------------------|------------------------------------------------------|
| [JVM](/sdk/jvm)             | Library for vanilla Java, Spring Boot 2 and 3                     |                                                              | [Docs](https://www.wirequery.io/docs/sdks/jvm)       |
| [JS (Browser)](/sdk/js)     | Integration with Javascript in the Browser for frontend recording |                                                              | [Docs](https://www.wirequery.io/docs/sdks/js)        |
| [Go](/sdk/go)               | Library for Go.                                                   | Highly experimental and masking not built-in yet             | [Docs](https://www.wirequery.io/docs/sdks/go)        |
| [Universal](/sdk/universal) | Universal SDK for every other programming language.               | Highly experimental and masking should be done by the client | [Docs](https://www.wirequery.io/docs/sdks/universal) |

More SDKs will be added over time.

## Links

- [Official Website](https://www.wirequery.io)
- [Documentation](https://www.wirequery.io/docs)
- [Writing Queries](https://www.wirequery.io/docs/guides/writing-queries)
- [Join our Discord Channel](https://discord.gg/BfaMCtkZe2) for questions and support.
- [Quick Start Guide with Spring Boot](https://www.wirequery.io/blog/wirequery-spring-boot)
- [WireQuery Introduction Blog Post](https://www.wirequery.io/blog/unveiling-wirequery)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md)

## License

Unless otherwise specified, WireQuery is licensed under AGPLv3 and the SDK is licensed under MIT. For more information,
see [LICENSE.md](LICENSE.md).

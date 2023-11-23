# WireQuery

Capture, explore, and analyze data flowing in and out of your applications - without compromising privacy.

- **Fix production incidents** an order of magnitude faster by querying the exact state
  in which it occurs.
- **Speed up development** and gain technical/business insights by exploring production data.
- **Free and Open Source**, so that you can use WireQuery to your heart's content.

WireQuery SDKs capture HTTP requests and responses based on the provided query in the WireQuery UI. Masking is done in
the application itself, so that sensitive data is stripped before ever reaching the WireQuery server.

![Screenshot](screenshot_1.png)

## Feature Highlights

- **Explore**:
  After entering a query, the targeted production system will immediately start capturing data so that WireQuery can
  show it back to you.
- **(Stored) Queries**: Occassionally, you may want to run a query for a longer time, or even indefinitely. (Stored)
  Queries allow you to do so.
- **Extended Tracing**: Extended Tracing is similar to regular tracing, but adds request and response bodies and more
  where confidential information is redacted, so you can see what *actually* happens during a call.
- **Sessions**: Allow users such as Support Engineers to spin up a group of queries that are
  instantiated by applying a template, so developers have all the knowledge they need to tackle any incident prior to
  getting started.
- **Recording**: Record interactions on the frontend as if they were screen captures. WireQuery is the first tool in the
  world to combine that data with the data on the backend, so you can get an end-to-end overview of what happened when a
  user ended up in a certain state.
- **Masking**: Each application can specify masking and unmasking rules, so that confidential information is hidden.

## Get Started

You can follow the Getting Started guide [here](docs/getting-started.md).

## SDKs

WireQuery's SDKs are offered in the following variants:

| Technology              | Description                                                       |
|-------------------------|-------------------------------------------------------------------|
| [JVM](/sdk/jvm)         | Library for vanilla Java, Spring Boot 2 and 3                     |
| [JS (Browser)](/sdk/js) | Integration with Javascript in the Browser for frontend recording |

More SDKs will be added over time.

## Guides

- [Writing Queries](/docs/writing-queries.md)

## Community

- [Join our Discord Channel](https://discord.gg/ej7Rxwdd) for questions and support.

## WireQuery as a Service

WireQuery is also available as a service, including a Free Tier that's powerful enough for small businesses, so you
don't have to spend time on maintaining it.

Visit [wirequery.com](https://www.wirequery.com/) for more information.

## Contributing

The best way to improve the product is by providing us with feedback. Please feel free to provide feedback (i.e. ideas
for improvement, bugs, etc.) by opening an issue. Also, if you found a bug, you can create a PR to have it merged into
the code. Before it can be merged, however, you need to sign a Contributors License Agreement, to make sure that we can
serve our users with your code going forward.

Also, please feel free to discuss ideas and feedback on our [Discord Channel](https://discord.gg/ej7Rxwdd).

## License

Unless otherwise specified, WireQuery is licensed under AGPLv3 and the SDK is licensed under MIT. For more information,
see [LICENSE.md](LICENSE.md).

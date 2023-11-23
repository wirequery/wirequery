# WireQuery

Capture, explore, and analyze data flowing in and out of your applications - without compromising confidentiality.

- **Investigate and Fix Bugs Faster**: Investigate and fix bugs an order of magnitude faster by querying the exact state
  in which it occurs.
- **Speed Up Development**: Speed up development by observing and analyzing production data instead of relying on
  possibly outdated or missing documentation.
- **Free and Open Source**: WireQuery is licensed under AGPLv3 and the SDK is licensed under MIT, so you can use
  WireQuery to your heart's content.

Imagine what your production systems could tell you...

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

WireQuery is available as a Docker image. In order for it to operate, you need to have the following software installed:

- TimescaleDB
- Redis

The easiest way to install WireQuery on a cluster is using its Helm image.

## SDKs

WireQuery is offered in the following variants:

| Technology              | Description                                                       |
|-------------------------|-------------------------------------------------------------------|
| [JVM](/sdk/jvm)         | Library for vanilla Java, Spring Boot 2 and 3                     |
| [JS (Browser)](/sdk/js) | Integration with Javascript in the Browser for frontend recording |

## Guides

- [Writing Queries](/docs/writing-queries.md)

## Community

- [Join our Discord Channel](https://discord.gg/ej7Rxwdd) for questions and support.

## Contributing

The best way to improve the product is by providing us with feedback. Please feel free to provide feedback (i.e. ideas
for improvement, bugs, etc.) by opening an issue. Also, if you found a bug, you can create a PR to have it merged into
the code. Before it can be merged, however, you need to sign a Contributors License Agreement, to make sure that we can
serve our users with your code going forward.

Also, please feel free to discuss ideas and feedback on our [Discord Channel](https://discord.gg/ej7Rxwdd).

## License

Unless otherwise specified, WireQuery is licensed under AGPLv3 and the SDK is licensed under MIT. For more information,
see [LICENSE.md](LICENSE.md).

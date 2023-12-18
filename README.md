# WireQuery

Capture, explore, and analyze data flowing in and out of your applications - without compromising privacy.

- **Fix production incidents** an order of magnitude faster by querying the exact state
  in which it occurs.
- **Speed up development** and gain technical/business insights by exploring production data.
- **Free and Open Source**, so that you can use WireQuery to your heart's content.

WireQuery SDKs capture HTTP requests and responses based on the provided query in WireQuery. Masking is done in the
application itself, so that sensitive data is stripped before ever reaching the WireQuery server.

![Screenshot](screenshot_1.png)

## Feature Highlights

- **Explore**: Enter a query to let the targeted (production) system start capturing HTTP requests immediately.
- **(Stored) Queries**: Store queries so that you can run them in the background.
- **Extended Tracing**: Trace the results with their bodies, so you can see what happens in your entire stack. [Screenshot](screenshot_3.png)
- **Sessions**: Allow non-technical users to start multiple queries at the same time.
- **Full-Stack Recording**: Record the frontend like a video while capturing the related backend requests. [Screenshot](screenshot_2.png)
- **Masking**: Easily mask confidential information, so you can query with peace of mind.
 
## Get Started

- You can follow the Installation Guide [here](docs/installation.md).
- You can follow the Getting Started Guide [here](docs/getting-started.md).

## SDKs

WireQuery's SDKs are offered in the following variants:

| Technology                  | Description                                                       |
|-----------------------------|-------------------------------------------------------------------|
| [JVM](/sdk/jvm)             | Library for vanilla Java, Spring Boot 2 and 3                     |
| [JS (Browser)](/sdk/js)     | Integration with Javascript in the Browser for frontend recording |
| [Universal](/sdk/universal) | Universal SDK for every other programming language                |                                                    |

More SDKs will be added over time.

## Guides

- [Writing Queries](/docs/writing-queries.md)

## Community

- [Join our Discord Channel](https://discord.gg/BfaMCtkZe2) for questions and support.

## Contributing

The best way to improve the product is by providing us with feedback. Please feel free to provide feedback (i.e. ideas
for improvement, bugs, etc.) by opening an issue. Also, if you found a bug, you can create a PR to have it merged into
the code. Before it can be merged, however, you need to sign a Contributors License Agreement, to make sure that we can
serve our users with your code going forward.

Also, please feel free to discuss ideas and feedback on our [Discord Channel](https://discord.gg/BfaMCtkZe2).

## License

Unless otherwise specified, WireQuery is licensed under AGPLv3 and the SDK is licensed under MIT. For more information,
see [LICENSE.md](LICENSE.md).

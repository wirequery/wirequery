# WireQuery

Capture, explore, and analyze data flowing in and out of your applications - without compromising privacy.

- **Reduce MTTR**: Investigate and fix bugs an order of magnitude faster by querying the exact state in which it occurs.
- **Speed Up Development**: Speed up development by observing and analyzing production data instead of relying on possibly outdated or missing documentation.
- **Free and Open Source**: WireQuery is licensed under AGPLv3 and the SDK is licensed under MIT.

![Screenshot](screenshot_1.png)

## Highlights

- **Instant Query**:
After typing in a query, the targeted production system will immediately start capturing data to show it back to you.

- **Stored Queries**: Occassionally, you may want to run a query for a longer time, or even indefinitely. Stored Queries allows you to do so.

- **Sessions**: Allow non-technical users to spin up multiple queries at the same time by applying a template.

- **Recording**: Record user interactions on the frontend as if it were a screen capture. Combine this with the data on the backend to get an end-to-end overview of what's happening in your stack.

- **Masking**: Each application can specify masking and unmasking rules, so that confidential information is hidden.

## Get Started

WireQuery is available as a Docker image. In order for it to operate, you need to have the following software installed:

- TimescaleDB
- Redis

The easiest way to install WireQuery on a cluster is using its Helm image.

## SDKs

WireQuery is offered in the following variants:

| Technology              | Description                                   |
|-------------------------|-----------------------------------------------|
| [JVM](/sdk/jvm)         | Library for vanilla Java, Spring Boot 2 and 3 |
| [JS (Browser)](/sdk/js) | Integration with Javascript in the Browser    |

## Guides

- [Writing Queries](/docs/writing-queries.md)

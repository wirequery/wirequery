Capture, explore, and analyze data flowing in and out of your applications - without compromising privacy.

- **Reduce Mean-Time-To-Resolve**: Investigate and fix bugs an order of magnitude faster by querying the exact state in which it occurs.
- **Speed Up Development**: Speed up development by observing and analyzing production data instead of relying on possibly outdated or missing documentation.
- **Free and Open Source**: WireQuery is licensed under AGPLv3 and the SDK is licensed under MIT.

# Feature Highlights

- **Instant Query**:
After typing in a query, the targeted production system will immediately start capturing data to show it back to you.

- **Stored Queries**: Occassionally, you may want to run a query for a longer time, or even indefinitely. Stored Queries allows you to do so.

- **Sessions**: Allow non-technical users to spin up a session in which the data is collected from multiple Stored Queries at the same time.

- **Recording**: Record user interactions on the frontend as if it were a screen capture. Combine this with the data on the backend.

- **Masking**: Each application can specify masking and unmasking rules, so that confidential information is hidden.

# Getting Started

WireQuery is available as a Docker image. In order for it to operate, you also need:

- TimescaleDB
- Redis

# SDKs

WireQuery is offered in the following variants:

| Technology      | Description                                   |
|-----------------|-----------------------------------------------|
| [JVM](/sdk/jvm) | Library for vanilla Java, Spring Boot 2 and 3 |

# Guides

- [Writing Queries](/docs/writing-queries.md)

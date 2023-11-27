# Getting Started

There are different ways to set up WireQuery:

- Using Helm (depends on Kubernetes)
- Manually

## Helm

Coming Soon...

## Manual Set Up

### Prerequisites

WireQuery is available as two Docker images: one for the frontend and one for the backend. In order for it to operate,
you need to have the following software installed:

- Postgres 14 or higher with Timescaledb extension installed
- Redis 7 or higher

### User with Row Security Enabled

The Postgres database needs to have a user with row security enabled. In order to create such a user, you can use the
following query:

```sql
-- Don't forget to replace this password.
CREATE USER "wirequery-multitenant" WITH PASSWORD '...';

GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON ALL TABLES IN SCHEMA public TO "wirequery-multitenant";
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO "wirequery-multitenant";
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON TABLES TO "wirequery-multitenant";
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO "wirequery-multitenant";
```

### WireQuery Backend

Provide the following arguments to the `wirequery-backend` container:

- `SPRING_FLYWAY_USER`: username of a user with root privileges
- `SPRING_FLYWAY_PASSWORD`: password of a user with root privileges
- `SPRING_FLYWAY_URL`: JDBC url of TSDB database, e.g.: `jdbc:postgresql://wirequery-postgresql:5432/wirequery`
- `WIREQUERY_REDIS_HOSTNAME`: host name of Redis instance
- `WIREQUERY_REDIS_PORT`: port of Redis instance, e.g. 6379
- `WIREQUERY_DB_USER`: username of a user with row security enabled
- `WIREQUERY_DB_PASSWORD`: password of a user with row security enabled
- `WIREQUERY_DB_URL`: JDBC url of TSDB database, e.g.: `jdbc:postgresql://wirequery-postgresql:5432/wirequery`
- `WIREQUERY_ADMIN_ENABLED`: if set to true, the `/admin` endpoints are enabled. Make sure these are behind a firewall.
  The `/admin` endpoints are needed to set up WireQuery.
- `WIREQUERY_DB_URL`: JDBC url of TSDB database, e.g.: `jdbc:postgresql://wirequery-postgresql:5432/wirequery`
- `WIREQUERY_TENANT_ID`: id of the tenant. Probably `1`.

### WireQuery Frontend

The WireQuery frontend does not need additional configuration to function.

### NGINX

In order for the backend and frontend to work together correctly, frontend routes should be forwarded to the frontend
and backend routes to the backend:

- `/`: wirequery-frontend, port 3000
- `/graphql`: wirequery-backend, port 8080

Next, grpc calls need to be routed to port 9090, and when using NGINX, make sure it doesn't time out by applying the
following configuration:

```
grpc_read_timeout "86400s";
grpc_send_timeout "86400s";
client_body_timeout "86400s";
```

### Set up

- Make sure that the admin endpoints are enabled.
- Call `curl -X POST <...host name...>/api/internal/admin/new-env/default` to create a new tenant called `default`.
- You can now log in using `admin` as a username and `Administrator` as the password.

**MAKE SURE YOU UPDATE THIS PASSWORD IMMEDIATELY!**

CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    tenant_id  INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    username   VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    enabled    BOOLEAN      NOT NULL,
    roles      VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE (tenant_id, username),
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY users_tenant_isolation_policy ON users
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE users
    ENABLE ROW LEVEL SECURITY;

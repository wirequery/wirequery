CREATE TABLE statistics
(
    id         BIGSERIAL PRIMARY KEY,
    tenant_id  INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    moment     DATE         NOT NULL,
    hour       INT          NOT NULL,
    type       VARCHAR(255) NOT NULL,
    metadata   TEXT         NOT NULL,
    amount     INT          NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE (tenant_id, moment, hour, type, metadata),
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY statistics_tenant_isolation_policy ON statistics
    USING (tenant_id = current_setting('app.tenant_id')::INT);

CREATE TABLE groups
(
    id          BIGSERIAL PRIMARY KEY,
    tenant_id   INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    name        VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    UNIQUE (tenant_id, name),
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY groups_tenant_isolation_policy ON groups
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE groups
    ENABLE ROW LEVEL SECURITY;

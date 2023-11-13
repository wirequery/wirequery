CREATE TABLE quarantine_groups
(
    id          BIGSERIAL PRIMARY KEY,
    tenant_id   INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    enabled     BOOLEAN      NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY quarantine_groups_tenant_isolation_policy ON quarantine_groups
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE quarantine_groups
    ENABLE ROW LEVEL SECURITY;

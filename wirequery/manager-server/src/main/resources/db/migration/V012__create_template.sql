CREATE TABLE templates
(
    id                   BIGSERIAL PRIMARY KEY,
    tenant_id            INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    name                 VARCHAR(255) NOT NULL,
    description          TEXT         NOT NULL,
    name_template        TEXT         NOT NULL,
    description_template TEXT         NOT NULL,
    created_at           TIMESTAMP,
    updated_at           TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY templates_tenant_isolation_policy ON templates
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE templates
    ENABLE ROW LEVEL SECURITY;

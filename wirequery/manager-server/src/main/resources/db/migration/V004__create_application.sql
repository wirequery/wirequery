CREATE TABLE applications
(
    id                BIGSERIAL PRIMARY KEY,
    tenant_id         INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    name              VARCHAR(255) NOT NULL,
    description       TEXT         NOT NULL,
    api_key           VARCHAR(255) NOT NULL,
    in_Quarantine     BOOLEAN      NOT NULL,
    quarantine_rule   VARCHAR(255) NULL,
    quarantine_reason VARCHAR(255) NULL,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    UNIQUE (tenant_id, name),
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY applications_tenant_isolation_policy ON applications
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE applications
    ENABLE ROW LEVEL SECURITY;

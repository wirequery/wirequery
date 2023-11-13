CREATE TABLE quarantine_rules
(
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    quarantine_group_id INT          NOT NULL,
    name                VARCHAR(255) NOT NULL,
    description         VARCHAR(255) NOT NULL,
    rule                VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP,
    FOREIGN KEY (quarantine_group_id) REFERENCES quarantine_groups (id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY quarantine_rules_tenant_isolation_policy ON quarantine_rules
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE quarantine_rules
    ENABLE ROW LEVEL SECURITY;

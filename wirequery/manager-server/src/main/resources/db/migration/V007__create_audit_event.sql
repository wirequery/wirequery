CREATE TABLE audit_events
(
    id         BIGSERIAL PRIMARY KEY,
    tenant_id  INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    username   VARCHAR(255) NOT NULL,
    action     VARCHAR(255) NOT NULL,
    ref_id     VARCHAR(255) NOT NULL,
    details    VARCHAR(255) NOT NULL,
    summary    VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY audit_events_tenant_isolation_policy ON audit_events
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE audit_events
    ENABLE ROW LEVEL SECURITY;

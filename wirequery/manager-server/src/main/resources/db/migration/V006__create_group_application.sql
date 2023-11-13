CREATE TABLE group_applications
(
    id             BIGSERIAL PRIMARY KEY,
    tenant_id      INT NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    group_id       INT NOT NULL,
    application_id INT NOT NULL,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    UNIQUE (tenant_id, group_id, application_id),
    FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES applications (id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY group_applications_tenant_isolation_policy ON group_applications
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE group_applications
    ENABLE ROW LEVEL SECURITY;

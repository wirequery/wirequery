CREATE TABLE template_querys
(
    id             BIGSERIAL PRIMARY KEY,
    tenant_id      INT  NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    template_id    INT  NOT NULL,
    application_id INT  NOT NULL,
    name_template  TEXT NOT NULL,
    query_template TEXT NOT NULL,
    query_limit    INT  NOT NULL,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES templates (id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES applications (id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY template_querys_tenant_isolation_policy ON template_querys
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE template_querys
    ENABLE ROW LEVEL SECURITY;

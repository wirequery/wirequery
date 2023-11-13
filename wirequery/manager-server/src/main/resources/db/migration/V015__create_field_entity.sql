CREATE TABLE template_fields
(
    id          BIGSERIAL PRIMARY KEY,
    tenant_id   INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    template_id INT          NOT NULL,
    order_key   INT          NOT NULL,
    key         VARCHAR(255) NOT NULL,
    label       VARCHAR(255) NOT NULL,
    type        VARCHAR(255) NOT NULL,
    FOREIGN KEY (template_id) REFERENCES templates (id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY template_fields_tenant_isolation_policy ON template_fields
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE template_fields
    ENABLE ROW LEVEL SECURITY;

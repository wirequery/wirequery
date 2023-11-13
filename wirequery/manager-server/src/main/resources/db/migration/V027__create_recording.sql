CREATE TABLE recordings
(
    id             BIGSERIAL PRIMARY KEY,
    tenant_id      INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    session_id     INT          NOT NULL,
    template_id    INT          NOT NULL,
    args           TEXT         NOT NULL,
    secret         VARCHAR(255) NOT NULL,
    look_back_secs INT          NOT NULL,
    timeout_secs   INT          NOT NULL,
    recording      TEXT         NOT NULL,
    context        TEXT         NOT NULL,
    status         VARCHAR(255) NOT NULL,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES templates (id) ON DELETE SET NULL,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY recordings_tenant_isolation_policy ON recordings
    USING (tenant_id = current_setting('app.tenant_id')::INT);

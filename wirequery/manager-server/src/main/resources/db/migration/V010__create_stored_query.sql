CREATE TABLE stored_querys
(
    id             BIGSERIAL PRIMARY KEY,
    tenant_id      INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    application_id INT          NOT NULL,
    name           VARCHAR(255) NOT NULL,
    type           VARCHAR(255) NOT NULL,
    query          TEXT         NOT NULL,
    query_limit    INT          NULL,
    end_date       TIMESTAMP    NULL,
    disabled       BOOLEAN,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES applications (id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY stored_querys_tenant_isolation_policy ON stored_querys
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE stored_querys
    ENABLE ROW LEVEL SECURITY;

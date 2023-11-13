CREATE TABLE query_logs
(
    stored_query_id INT  NOT NULL,
    tenant_id       INT  NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    message         TEXT NOT NULL,
    created_at      TIMESTAMP,
    FOREIGN KEY (stored_query_id) REFERENCES stored_querys (id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

SELECT create_hypertable('query_logs', 'created_at');

CREATE INDEX ix_stored_query_id_created_at_asc ON query_logs (stored_query_id, created_at ASC);
CREATE INDEX ix_stored_query_id_created_at_desc ON query_logs (stored_query_id, created_at DESC);

CREATE POLICY query_logs_tenant_isolation_policy ON query_logs
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE query_logs
    ENABLE ROW LEVEL SECURITY;

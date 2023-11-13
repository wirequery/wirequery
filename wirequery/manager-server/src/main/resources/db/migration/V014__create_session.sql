CREATE TABLE sessions
(
    id          BIGSERIAL PRIMARY KEY,
    tenant_id   INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    name        VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

ALTER TABLE stored_querys
    ADD COLUMN session_id INT NULL;

ALTER TABLE stored_querys
    ADD CONSTRAINT stored_querys_session_id_fkey
        FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE SET NULL;

CREATE POLICY sessions_tenant_isolation_policy ON sessions
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE sessions
    ENABLE ROW LEVEL SECURITY;

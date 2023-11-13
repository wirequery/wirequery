CREATE TABLE group_users
(
    id         BIGSERIAL PRIMARY KEY,
    tenant_id  INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    group_id   INT          NOT NULL,
    user_id    INT          NOT NULL,
    role       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE (tenant_id, group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY group_users_tenant_isolation_policy ON group_users
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE group_users
    ENABLE ROW LEVEL SECURITY;

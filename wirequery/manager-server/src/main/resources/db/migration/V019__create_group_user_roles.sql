ALTER TABLE group_users
    DROP COLUMN role;

CREATE TABLE group_user_roles
(
    id            BIGSERIAL PRIMARY KEY,
    tenant_id     INT NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    group_user_id INT NOT NULL,
    group_role_id INT NOT NULL,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
    FOREIGN KEY (group_user_id) REFERENCES group_users (id) ON DELETE CASCADE,
    FOREIGN KEY (group_role_id) REFERENCES group_roles (id) ON DELETE CASCADE
);

CREATE POLICY group_user_roles_tenant_isolation_policy ON group_user_roles
    USING (tenant_id = current_setting('app.tenant_id')::INT);

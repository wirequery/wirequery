ALTER TABLE users
    DROP COLUMN roles;

CREATE TABLE user_roles
(
    id        BIGSERIAL PRIMARY KEY,
    tenant_id INT NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    user_id   INT NOT NULL,
    role_id   INT NOT NULL,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE POLICY user_roles_tenant_isolation_policy ON user_roles
    USING (tenant_id = current_setting('app.tenant_id')::INT);

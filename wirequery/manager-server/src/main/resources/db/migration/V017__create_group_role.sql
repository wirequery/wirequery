CREATE TABLE group_roles
(
    id         BIGSERIAL PRIMARY KEY,
    tenant_id  INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE TABLE group_role_authorisations
(
    id            BIGSERIAL PRIMARY KEY,
    tenant_id     INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    group_role_id INT          NOT NULL,
    name          VARCHAR(255) NOT NULL,
    FOREIGN KEY (group_role_id) REFERENCES group_roles (id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY group_roles_tenant_isolation_policy ON group_roles
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE group_roles
    ENABLE ROW LEVEL SECURITY;

CREATE POLICY group_role_authorisations_tenant_isolation_policy ON group_role_authorisations
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE group_role_authorisations
    ENABLE ROW LEVEL SECURITY;

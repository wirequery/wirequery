CREATE TABLE roles
(
    id         BIGSERIAL PRIMARY KEY,
    tenant_id  INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE TABLE role_authorisations
(
    id        BIGSERIAL PRIMARY KEY,
    tenant_id INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    role_id   INT          NOT NULL,
    name      VARCHAR(255) NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE POLICY roles_tenant_isolation_policy ON roles
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE roles
    ENABLE ROW LEVEL SECURITY;

CREATE POLICY role_authorisations_tenant_isolation_policy ON role_authorisations
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE role_authorisations
    ENABLE ROW LEVEL SECURITY;

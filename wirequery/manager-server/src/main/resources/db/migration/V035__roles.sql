ALTER TABLE roles
    DROP CONSTRAINT roles_name_key;

ALTER TABLE group_roles
    DROP CONSTRAINT group_roles_name_key;

ALTER TABLE roles
    ADD UNIQUE (tenant_id, name);

ALTER TABLE group_roles
    ADD UNIQUE (tenant_id, name);

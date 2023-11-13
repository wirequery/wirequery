ALTER TABLE roles
    ADD UNIQUE (name);

ALTER TABLE group_roles
    ADD UNIQUE (name);

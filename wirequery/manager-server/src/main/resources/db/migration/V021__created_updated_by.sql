ALTER TABLE applications
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE applications
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE groups
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE groups
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE group_applications
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE group_applications
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE group_roles
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE group_roles
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE group_users
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE group_users
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE quarantine_groups
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE quarantine_groups
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE quarantine_rules
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE quarantine_rules
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE roles
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE roles
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE sessions
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE sessions
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE stored_querys
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE stored_querys
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE templates
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE templates
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE template_querys
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE template_querys
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE users
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE users
    ADD COLUMN updated_by VARCHAR(255);

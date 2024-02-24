-- Create a default tenant, if and only if, no tenants exist.
INSERT INTO tenants (id, name, slug, plan, enabled, created_at, updated_at)
SELECT 1, 'Default', 'default', '', true, NOW(), NOW()
WHERE NOT EXISTS(SELECT NULL FROM tenants);

-- Create a default admin user, if and only if, no users exists yet.
-- set the password to 'UNSET' to indicate that the password needs to be changed.
INSERT INTO users (id, tenant_id, username, password, enabled, created_at, updated_at, created_by, updated_by)
SELECT 1, 1, 'admin', 'UNSET', true, NOW(), NOW(), '', ''
WHERE NOT EXISTS(SELECT NULL FROM users);

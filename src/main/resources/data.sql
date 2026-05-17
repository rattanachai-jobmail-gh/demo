INSERT INTO roles (role_name)
VALUES ('CEO'), ('Cashier')
ON CONFLICT (role_name) DO NOTHING;

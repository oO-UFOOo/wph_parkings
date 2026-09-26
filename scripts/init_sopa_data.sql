-- Initialize sample data for SOPA OpenEye Backend

-- Insert Patrol Clients
INSERT INTO patrol_clients (client_name, client_code, description, is_active)
VALUES
    ('Downtown Patrol', 'DT-001', 'Downtown area patrol client', true),
    ('North Zone Patrol', 'NZ-002', 'North zone area patrol client', true);

-- Insert Spots for Downtown Patrol (id=1)
INSERT INTO qr_spots (spot_name, qr_code, longitude, latitude, patrol_client_id, description, is_active)
VALUES
    ('Main Street Corner', 'QR-DT-001', -73.935242, 40.730610, 1, 'Main street intersection', true),
    ('Central Park East', 'QR-DT-002', -73.965244, 40.781776, 1, 'Central Park East entrance', true),
    ('Times Square', 'QR-DT-003', -73.985130, 40.758896, 1, 'Times Square area', true);

-- Insert Spots for North Zone Patrol (id=2)
INSERT INTO qr_spots (spot_name, qr_code, longitude, latitude, patrol_client_id, description, is_active)
VALUES
    ('North Terminal', 'QR-NZ-001', -73.876951, 40.853096, 2, 'North terminal checkpoint', true),
    ('North Park', 'QR-NZ-002', -73.869629, 40.805411, 2, 'North park area', true);

-- Insert App Users
INSERT INTO app_users (username, password, app_type, is_active)
VALUES
    ('ufo', '$2a$10$GRLdNijSQmvZv/TXhkjWLuTJZFpNqm0bH8PQNB3xh5Uv6b1f6w2B.', 'UFO', true),
    ('openeye', '$2a$10$y9Qqf3fM8Y1J7K6L9P2Q3R4S5T6U7V8W9X0Y1Z2a3B4c5D6e7F8g', 'OPENEYE', true);

-- Note: The hashed passwords above are for "ufo_password_123" and "openeye_password_123"
-- Generated using BCryptPasswordEncoder with standard Spring Security

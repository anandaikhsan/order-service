-- Insert sample events
INSERT INTO events (id, name, description, venue, event_date, created_at, updated_at, total_seats, available_seats, price) 
VALUES 
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Concert A', 'A great concert', 'Venue A', '2023-12-15 19:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 100, 100, 90.0),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Concert B', 'Another amazing concert', 'Venue B', '2023-12-20 20:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 80, 80, 120.0),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Sports Event', 'Championship game', 'Stadium C', '2023-12-25 15:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 200, 200, 110.0);

-- Insert sample seats for Concert A
INSERT INTO seats (id, seat_number, row, section, status, price, event_id, created_at, updated_at) 
VALUES 
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'A1', 'A', 'Main', 'AVAILABLE', 100.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'A2', 'A', 'Main', 'AVAILABLE', 100.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'B1', 'B', 'Main', 'AVAILABLE', 80.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', 'B2', 'B', 'Main', 'AVAILABLE', 80.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample seats for Concert B
INSERT INTO seats (id, seat_number, row, section, status, price, event_id, created_at, updated_at) 
VALUES 
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 'A1', 'A', 'VIP', 'AVAILABLE', 150.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a32', 'A2', 'A', 'VIP', 'AVAILABLE', 150.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'B1', 'B', 'Regular', 'AVAILABLE', 90.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a34', 'B2', 'B', 'Regular', 'AVAILABLE', 90.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample seats for Sports Event
INSERT INTO seats (id, seat_number, row, section, status, price, event_id, created_at, updated_at) 
VALUES 
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', '101', '1', 'North', 'AVAILABLE', 120.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', '102', '1', 'North', 'AVAILABLE', 120.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a43', '201', '2', 'South', 'AVAILABLE', 100.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', '202', '2', 'South', 'AVAILABLE', 100.0, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

PRAGMA foreign_keys = ON;

INSERT INTO products(name, category, quantity, ideal_quantity, unit_price, expiration_date, registration_date)
VALUES
('Pao frances', 'Paes', 80, 120, 0.80, date('now', '+2 day'), date('now')),
('Bolo de chocolate', 'Bolos', 4, 8, 39.90, date('now', '+5 day'), date('now')),
('Sonho creme', 'Doces', 10, 20, 6.50, date('now', '-1 day'), date('now')),
('Croissant', 'Salgados', 25, 25, 8.90, date('now', '+12 day'), date('now')),
('Farinha de trigo 25kg', 'Insumos', 3, 6, 95.00, date('now', '+45 day'), date('now'));

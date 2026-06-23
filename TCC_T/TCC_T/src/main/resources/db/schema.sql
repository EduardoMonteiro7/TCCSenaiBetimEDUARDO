PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE COLLATE NOCASE,
    password_hash TEXT NOT NULL,
    salt TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('MANAGER', 'USER')),
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_only_one_manager
ON users(role)
WHERE role = 'MANAGER';

CREATE TRIGGER IF NOT EXISTS prevent_manager_delete
BEFORE DELETE ON users
WHEN OLD.role = 'MANAGER'
BEGIN
    SELECT RAISE(ABORT, 'The manager account cannot be deleted.');
END;

CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 0),
    ideal_quantity INTEGER NOT NULL CHECK (ideal_quantity >= 0),
    unit_price REAL NOT NULL CHECK (unit_price >= 0),
    expiration_date TEXT NOT NULL,
    registration_date TEXT NOT NULL DEFAULT (date('now')),
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_products_name ON products(name COLLATE NOCASE);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category COLLATE NOCASE);
CREATE INDEX IF NOT EXISTS idx_products_expiration ON products(expiration_date);

CREATE TABLE IF NOT EXISTS inventory_movements (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER,
    user_id INTEGER,
    movement_type TEXT NOT NULL CHECK (movement_type IN ('CREATE', 'UPDATE', 'DELETE', 'STOCK_IN', 'STOCK_OUT')),
    quantity_change INTEGER NOT NULL DEFAULT 0,
    notes TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS system_settings (
    setting_key TEXT PRIMARY KEY,
    setting_value TEXT NOT NULL
);

INSERT OR IGNORE INTO system_settings(setting_key, setting_value)
VALUES ('expiration_warning_days', '7');

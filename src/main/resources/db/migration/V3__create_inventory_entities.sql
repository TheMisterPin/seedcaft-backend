CREATE TABLE IF NOT EXISTS warehouse (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(128),
    state VARCHAR(128),
    country VARCHAR(128),
    max_storage_units INTEGER NOT NULL CHECK (max_storage_units >= 0),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS storage_bin (
    id BIGSERIAL PRIMARY KEY,
    warehouse_id BIGINT NOT NULL REFERENCES warehouse(id),
    code VARCHAR(64) NOT NULL,
    max_storage_units INTEGER NOT NULL CHECK (max_storage_units >= 0),
    current_storage_units INTEGER NOT NULL CHECK (current_storage_units >= 0),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_storage_bin_warehouse_code UNIQUE (warehouse_id, code)
);

CREATE TABLE IF NOT EXISTS inventory_stock (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id),
    warehouse_id BIGINT NOT NULL REFERENCES warehouse(id),
    bin_id BIGINT NOT NULL REFERENCES storage_bin(id),
    quantity_on_hand INTEGER NOT NULL CHECK (quantity_on_hand >= 0),
    quantity_available INTEGER NOT NULL CHECK (quantity_available >= 0),
    quantity_reserved INTEGER NOT NULL CHECK (quantity_reserved >= 0),
    quantity_blocked INTEGER NOT NULL CHECK (quantity_blocked >= 0),
    reorder_point INTEGER NOT NULL CHECK (reorder_point >= 0),
    stock_status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT ck_inventory_stock_status CHECK (stock_status IN ('IN_STOCK', 'LOW_STOCK', 'OUT_OF_STOCK', 'OVERSTOCKED', 'BLOCKED')),
    CONSTRAINT uk_inventory_stock_product_wh_bin UNIQUE (product_id, warehouse_id, bin_id)
);

CREATE TABLE IF NOT EXISTS inventory_metric_snapshot (
    id BIGSERIAL PRIMARY KEY,
    snapshot_date DATE NOT NULL,
    scope_type VARCHAR(32) NOT NULL,
    scope_code VARCHAR(64) NOT NULL,
    scope_name VARCHAR(255) NOT NULL,
    total_units BIGINT NOT NULL CHECK (total_units >= 0),
    available_units BIGINT NOT NULL CHECK (available_units >= 0),
    reserved_units BIGINT NOT NULL CHECK (reserved_units >= 0),
    blocked_units BIGINT NOT NULL CHECK (blocked_units >= 0),
    low_stock_count INTEGER NOT NULL CHECK (low_stock_count >= 0),
    out_of_stock_count INTEGER NOT NULL CHECK (out_of_stock_count >= 0),
    total_storage_capacity INTEGER NOT NULL CHECK (total_storage_capacity >= 0),
    used_storage_units INTEGER NOT NULL CHECK (used_storage_units >= 0),
    fill_percentage NUMERIC(6,2) NOT NULL CHECK (fill_percentage >= 0),
    inventory_value NUMERIC(19,4) NOT NULL CHECK (inventory_value >= 0),
    created_at TIMESTAMP,
    CONSTRAINT ck_inventory_metric_scope_type CHECK (scope_type IN ('GLOBAL', 'WAREHOUSE', 'CATEGORY', 'PRODUCT'))
);

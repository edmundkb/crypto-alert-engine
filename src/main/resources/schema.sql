CREATE TABLE IF NOT EXISTS crypto_prices (
    id BIGSERIAL PRIMARY KEY,
    ticker VARCHAR(10) NOT NULL,
    price NUMERIC(18, 2) NOT NULL,
    volume NUMERIC(18, 2),
    timestamp TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ticker_timestamp ON crypto_prices (ticker, timestamp DESC);
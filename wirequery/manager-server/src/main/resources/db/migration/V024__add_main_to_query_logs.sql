ALTER TABLE query_logs ADD COLUMN main BOOLEAN NOT NULL DEFAULT true;

CREATE INDEX ix_query_logs_trace_id ON query_logs (trace_id);

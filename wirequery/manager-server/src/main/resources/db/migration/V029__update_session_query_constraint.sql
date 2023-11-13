ALTER TABLE stored_querys
    DROP CONSTRAINT stored_querys_session_id_fkey;

ALTER TABLE stored_querys
    ADD CONSTRAINT stored_querys_session_id_fkey
        FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE CASCADE;

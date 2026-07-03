ALTER TABLE attachments
    ADD COLUMN size          BIGINT       NULL AFTER url,
    ADD COLUMN original_name VARCHAR(255) NULL AFTER size;

ALTER TABLE document.avsender_mottaker
    ALTER COLUMN part_id_value DROP NOT NULL,
    ALTER COLUMN part_id_type_id DROP NOT NULL;
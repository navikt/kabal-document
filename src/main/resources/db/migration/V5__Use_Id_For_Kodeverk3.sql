ALTER TABLE document.brevmottaker
    ALTER COLUMN part_id_type DROP NOT NULL;

ALTER TABLE document.journalfoeringdata
    ALTER COLUMN saken_gjelder_type DROP NOT NULL;
ALTER TABLE document.journalfoeringdata
    ALTER COLUMN tema DROP NOT NULL;
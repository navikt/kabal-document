ALTER TABLE document.brevmottaker
    ADD COLUMN part_id_type_id TEXT;

ALTER TABLE document.journalfoeringdata
    ADD COLUMN saken_gjelder_type_id TEXT,
    ADD COLUMN tema_id                     TEXT,
    ADD COLUMN sak_fagsystem_id            TEXT;

ALTER TABLE document.journalfoeringdata
    ADD COLUMN sak_sakstype_id TEXT NOT NULL DEFAULT '1';

ALTER TABLE document.journalfoeringdata
    ALTER COLUMN sak_fagsak_id DROP NOT NULL;

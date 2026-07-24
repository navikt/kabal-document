ALTER TABLE document.trygderetten_metadata
    ALTER COLUMN paaanket_vedtaksdato DROP NOT NULL;

ALTER TABLE document.trygderetten_metadata
    ALTER COLUMN forsterket_rett DROP NOT NULL;

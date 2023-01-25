ALTER TABLE document.journalfoeringdata
    ADD COLUMN IF NOT EXISTS journalposttype TEXT;

ALTER TABLE document.dokumentenhet
DROP
COLUMN IF EXISTS should_be_distributed;
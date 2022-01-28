ALTER TABLE document.dokumentenhet
    ADD COLUMN dokument_type TEXT,
    ADD COLUMN ekstern_referanse TEXT;

ALTER TABLE document.opplastetdokument
    ADD COLUMN dokument_type TEXT;

ALTER TABLE document.journalfoert_vedlegg
    ADD COLUMN index INT DEFAULT 0;

ALTER TABLE document.opplastetdokument
    ADD COLUMN index INT DEFAULT 0;

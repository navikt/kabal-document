ALTER TABLE document.journalfoeringdata
    ADD COLUMN inngaende_kanal TEXT;

CREATE TABLE document.dokument_info_reference
(
    id                    UUID PRIMARY KEY,
    journalpost_id        TEXT NOT NULL,
    dokument_info_id      TEXT NOT NULL,
    opplastet_dokument_id UUID NOT NULL,
    CONSTRAINT fk_dokument_info_reference_opplastet_dok
        FOREIGN KEY (opplastet_dokument_id)
            REFERENCES document.opplastetdokument (id)
);

CREATE INDEX dokument_info_reference_dk_idx ON document.dokument_info_reference (opplastet_dokument_id);

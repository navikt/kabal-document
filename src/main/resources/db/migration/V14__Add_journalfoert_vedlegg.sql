CREATE TABLE document.journalfoert_vedlegg
(
    id                   UUID PRIMARY KEY,
    kilde_journalpost_id TEXT NOT NULL,
    dokument_info_id     TEXT NOT NULL,
    dokumentenhet_id     UUID NOT NULL,
    CONSTRAINT fk_journalfoert_vedlegg_doke
        FOREIGN KEY (dokumentenhet_id)
            REFERENCES document.dokumentenhet (id)
);

CREATE INDEX journalfoert_vedlegg_fk_idx ON document.journalfoert_vedlegg (dokumentenhet_id);

CREATE TABLE document.journalfoert_vedlegg_id
(
    id                      UUID PRIMARY KEY,
    journalfoert_vedlegg_id UUID NOT NULL,
    brevmottakerdist_id     UUID NOT NULL,
    CONSTRAINT fk_journalfoert_vedlegg_id_brevmottakerdist
        FOREIGN KEY (brevmottakerdist_id)
            REFERENCES document.brevmottakerdist (id)
);

CREATE INDEX journalfoert_vedlegg_id_fk_idx ON document.journalfoert_vedlegg_id (brevmottakerdist_id);
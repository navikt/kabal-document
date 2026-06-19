CREATE TABLE document.trygderetten_metadata
(
    id                                  UUID PRIMARY KEY,
    dokumentenhet_id                    UUID    NOT NULL,
    kravfremsettelsesdato               DATE,
    paaanket_vedtaksdato                DATE    NOT NULL,
    tidligere_i_tr_og_opphevet_henvist  BOOLEAN,
    gjenopptak                          BOOLEAN,
    forsterket_rett                     BOOLEAN NOT NULL,
    ettersendelse                       BOOLEAN NOT NULL,
    representant_part_id_type           TEXT,
    representant_part_id_value          TEXT,
    representant_navn                   TEXT,
    representant_adresse_adressetype    TEXT,
    representant_adresse_adresselinje_1 TEXT,
    representant_adresse_adresselinje_2 TEXT,
    representant_adresse_adresselinje_3 TEXT,
    representant_adresse_postnummer     TEXT,
    representant_adresse_poststed       TEXT,
    representant_adresse_land           TEXT,
    CONSTRAINT fk_trygderetten_metadata_doke
        FOREIGN KEY (dokumentenhet_id)
            REFERENCES document.dokumentenhet (id)
);

CREATE INDEX trygderetten_metadata_fk_idx ON document.trygderetten_metadata (dokumentenhet_id);

CREATE TABLE document.trygderetten_metadata_lovhenvisning
(
    trygderetten_metadata_id UUID NOT NULL,
    lovhenvisning            TEXT NOT NULL,
    CONSTRAINT fk_trygderetten_metadata_lovhenvisning
        FOREIGN KEY (trygderetten_metadata_id)
            REFERENCES document.trygderetten_metadata (id)
);

CREATE INDEX trygderetten_metadata_lovhenvisning_fk_idx
    ON document.trygderetten_metadata_lovhenvisning (trygderetten_metadata_id);

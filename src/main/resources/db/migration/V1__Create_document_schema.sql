DO
$$
    BEGIN
        IF EXISTS
            (SELECT 1 from pg_roles where rolname = 'cloudsqliamuser')
        THEN
            GRANT USAGE ON SCHEMA public TO cloudsqliamuser;
            GRANT USAGE ON SCHEMA document TO cloudsqliamuser;
            GRANT SELECT ON ALL TABLES IN SCHEMA public TO cloudsqliamuser;
            GRANT SELECT ON ALL TABLES IN SCHEMA document TO cloudsqliamuser;
            ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO cloudsqliamuser;
            ALTER DEFAULT PRIVILEGES IN SCHEMA document GRANT SELECT ON TABLES TO cloudsqliamuser;
        END IF;
    END
$$;

CREATE TABLE document.dokumentenhet
(
    id                         UUID PRIMARY KEY,
    eier                       TEXT NOT NULL,
    avsluttet_av_saksbehandler TIMESTAMP,
    avsluttet                  TIMESTAMP,
    modified                   TIMESTAMP
);

CREATE TABLE document.brevmottakerdist
(
    id                    UUID PRIMARY KEY,
    brev_mottaker_id      UUID NOT NULL,
    opplastet_dokument_id UUID NOT NULL,
    journalpost_id        TEXT NOT NULL,
    ferdigstilt_i_joark   TIMESTAMP,
    dokdist_referanse     UUID,
    dokumentenhet_id      UUID NOT NULL,
    CONSTRAINT fk_brevmottakerdist_doke
        FOREIGN KEY (dokumentenhet_id)
            REFERENCES document.dokumentenhet (id)
);

CREATE TABLE document.opplastetdokument
(
    id               UUID PRIMARY KEY,
    type             TEXT      NOT NULL,
    mellomlager_id   TEXT      NOT NULL,
    opplastet        TIMESTAMP NOT NULL,
    size             BIGINT    NOT NULL,
    name             TEXT      NOT NULL,
    dokumentenhet_id UUID      NOT NULL,
    CONSTRAINT fk_opplastetdokument_doke
        FOREIGN KEY (dokumentenhet_id)
            REFERENCES document.dokumentenhet (id)
);

CREATE TABLE document.brevmottaker
(
    id               UUID PRIMARY KEY,
    part_id_type     TEXT NOT NULL,
    part_id_value    TEXT NOT NULL,
    navn             TEXT NOT NULL,
    rolle            TEXT NOT NULL,
    dokumentenhet_id UUID NOT NULL,
    CONSTRAINT fk_brevmottaker_doke
        FOREIGN KEY (dokumentenhet_id)
            REFERENCES document.dokumentenhet (id)
);

CREATE TABLE document.journalfoeringdata
(
    id                  UUID PRIMARY KEY,
    saken_gjelder_type  TEXT NOT NULL,
    saken_gjelder_value TEXT NOT NULL,
    tema                TEXT NOT NULL,
    sak_fagsak_id       TEXT,
    sak_fagsystem       TEXT,
    kilde_referanse     TEXT NOT NULL,
    enhet               TEXT NOT NULL,
    dokumentenhet_id    UUID NOT NULL,
    CONSTRAINT fk_journalfoeringdata_doke
        FOREIGN KEY (dokumentenhet_id)
            REFERENCES document.dokumentenhet (id)
);

CREATE INDEX journalfoeringdata_fk_idx ON document.journalfoeringdata (dokumentenhet_id);
CREATE INDEX brevmottaker_fk_idx ON document.brevmottaker (dokumentenhet_id);
CREATE INDEX opplastetdokument_fk_idx ON document.opplastetdokument (dokumentenhet_id);
CREATE INDEX brevmottakerdist_fk_idx ON document.brevmottakerdist (dokumentenhet_id);
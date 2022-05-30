CREATE TABLE document.dokument_info
(
    id               UUID PRIMARY KEY,
    dokument_info_id TEXT NOT NULL,
    dokumentenhet_id UUID NOT NULL,
    CONSTRAINT fk_dokumentinfoid_doke
        FOREIGN KEY (dokumentenhet_id)
            REFERENCES document.dokumentenhet (id)
);
ALTER TABLE document.dokumentenhet
    ADD COLUMN journalfoering_id UUID;

ALTER TABLE document.dokumentenhet
    ADD CONSTRAINT fk_journalfoering_id_journalfoeringdata
        FOREIGN KEY (journalfoering_id)
            REFERENCES document.journalfoeringdata (id);

ALTER table document.journalfoeringdata
    DROP CONSTRAINT fk_journalfoeringdata_doke;

ALTER table document.journalfoeringdata
    DROP COLUMN dokumentenhet_id;
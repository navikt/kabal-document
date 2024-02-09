ALTER TABLE document.brevmottaker
    ADD COLUMN tving_sentral_print BOOLEAN,
    ADD COLUMN adresse_adressetype TEXT,
    ADD COLUMN adresse_adresselinje_1 TEXT,
    ADD COLUMN adresse_adresselinje_2 TEXT,
    ADD COLUMN adresse_adresselinje_3 TEXT,
    ADD COLUMN adresse_postnummer TEXT,
    ADD COLUMN adresse_poststed TEXT,
    ADD COLUMN adresse_land TEXT;
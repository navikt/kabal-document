ALTER TABLE document.dokumentenhet
    ADD COLUMN IF NOT EXISTS journalfoeringdata_id UUID REFERENCES document.journalfoeringdata (id);

UPDATE document.dokumentenhet d
SET journalfoeringdata_id = (SELECT j.id FROM document.journalfoeringdata j WHERE j.dokumentenhet_id = d.id);

ALTER TABLE document.journalfoeringdata
    ALTER COLUMN dokumentenhet_id DROP NOT NULL;

ALTER TABLE document.journalfoeringdata
DROP
COLUMN dokumentenhet_id;

-- Hoveddokument
ALTER TABLE document.dokumentenhet
    ADD COLUMN IF NOT EXISTS hoveddokument_id UUID REFERENCES document.opplastetdokument (id);

UPDATE document.dokumentenhet d
SET hoveddokument_id = (SELECT o.id
                        FROM document.opplastetdokument o
                        WHERE o.dokumentenhet_id = d.id
                          AND o.type = 'HOVEDDOKUMENT');

ALTER TABLE document.opplastetdokument
    ALTER COLUMN dokumentenhet_id DROP NOT NULL;

ALTER TABLE document.dokumentenhet
    ADD COLUMN IF NOT EXISTS should_be_distributed BOOLEAN default TRUE;

ALTER TABLE document.brevmottakerdist
    ALTER COLUMN journalpost_id DROP NOT NULL;

--Replaced by sak_fagsystem_id
ALTER TABLE document.journalfoeringdata
DROP
COLUMN sak_fagsystem;

    --Deprecated
ALTER TABLE document.dokumentenhet
DROP
COLUMN eier;
    --Deprecated
ALTER TABLE document.brevmottaker
DROP
COLUMN rolle;

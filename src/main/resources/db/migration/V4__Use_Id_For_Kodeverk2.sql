UPDATE document.brevmottaker
SET part_id_type_id = 'PERSON'
WHERE part_id_type = 'PERSON';

UPDATE document.brevmottaker
SET part_id_type_id = 'VIRKSOMHET'
WHERE part_id_type = 'VIRKSOMHET';

UPDATE document.journalfoeringdata
SET saken_gjelder_type_id = 'PERSON'
WHERE saken_gjelder_type = 'PERSON';

UPDATE document.journalfoeringdata
SET saken_gjelder_type_id = 'VIRKSOMHET'
WHERE saken_gjelder_type = 'VIRKSOMHET';

UPDATE document.journalfoeringdata
SET tema_id = '27'
WHERE tema = 'OMS';

--TODO: Hvilke trenger vi her?
UPDATE document.journalfoeringdata
SET sak_fagsystem_id = '1'
WHERE sak_fagsystem = 'FS36';

UPDATE document.journalfoeringdata
SET sak_fagsystem_id = '2'
WHERE sak_fagsystem = 'FS39';

UPDATE document.journalfoeringdata
SET sak_fagsystem_id = '8'
WHERE sak_fagsystem = 'K9';

UPDATE document.journalfoeringdata
SET sak_fagsystem_id = '17'
WHERE sak_fagsystem = 'OMSORGSPENGER';

UPDATE document.journalfoeringdata
SET sak_fagsystem_id = '18'
WHERE sak_fagsystem = 'MANUELL';

ALTER TABLE document.brevmottaker
    ALTER COLUMN part_id_type_id SET NOT NULL;

ALTER TABLE document.journalfoeringdata
    ALTER COLUMN saken_gjelder_type_id SET NOT NULL;
ALTER TABLE document.journalfoeringdata
    ALTER COLUMN tema_id SET NOT NULL;
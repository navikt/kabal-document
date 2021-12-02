ALTER TABLE document.brevmottaker
DROP
COLUMN part_id_type;

ALTER TABLE document.journalfoeringdata
DROP
COLUMN saken_gjelder_type;
ALTER TABLE document.journalfoeringdata
DROP
COLUMN tema;
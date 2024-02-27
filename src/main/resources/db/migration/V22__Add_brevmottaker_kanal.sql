ALTER TABLE document.brevmottaker
    ADD COLUMN IF NOT EXISTS kanal TEXT;

ALTER TABLE document.brevmottaker
    RENAME TO avsender_mottaker;

ALTER TABLE document.brevmottakerdist
    RENAME COLUMN brev_mottaker_id TO avsender_mottaker_id;

ALTER TABLE document.brevmottakerdist
    RENAME TO avsender_mottaker_dist;

ALTER TABLE document.journalfoert_vedlegg_id
    RENAME COLUMN brevmottakerdist_id TO avsender_mottaker_dist_id;
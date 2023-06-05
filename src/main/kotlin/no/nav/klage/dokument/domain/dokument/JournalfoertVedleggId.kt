package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "journalfoert_vedlegg_id", schema = "document")
class JournalfoertVedleggId (
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "journalfoert_vedlegg_id")
    val journalfoertVedleggId: UUID,
)
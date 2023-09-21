package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "journalfoert_vedlegg", schema = "document")
class JournalfoertVedlegg(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "kilde_journalpost_id")
    val kildeJournalpostId: String,
    @Column(name = "dokument_info_id")
    val dokumentInfoId: String,
    @Column(name = "index")
    val index: Int,
)
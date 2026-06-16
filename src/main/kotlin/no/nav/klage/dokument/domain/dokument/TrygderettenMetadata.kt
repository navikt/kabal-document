package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "trygderetten_metadata", schema = "document")
class TrygderettenMetadata(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "dokumentenhet_id")
    val dokumentEnhetId: UUID,
    @Column(name = "kravfremsettelsesdato")
    val kravfremsettelsesdato: LocalDate? = null,
    @Column(name = "paaanket_vedtaksdato")
    val paaanketVedtaksdato: LocalDate,
    @Column(name = "tidligere_i_tr_og_opphevet_henvist")
    val tidligereITROgOpphevetHenvist: Boolean? = null,
    @Column(name = "gjenopptak")
    val gjenopptak: Boolean? = null,
    @Column(name = "forsterket_rett")
    val forsterketRett: Boolean,
    @Column(name = "ettersendelse")
    val ettersendelse: Boolean,
    @Column(name = "lovhenvisning")
    val lovhenvisning: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrygderettenMetadata

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

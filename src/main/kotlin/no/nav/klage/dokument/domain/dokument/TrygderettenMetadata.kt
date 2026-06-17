package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.*
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
    val kravfremsettelsesdato: LocalDate?,
    @Column(name = "paaanket_vedtaksdato")
    val paaanketVedtaksdato: LocalDate,
    @Column(name = "tidligere_i_tr_og_opphevet_henvist")
    val tidligereITROgOpphevetHenvist: Boolean?,
    @Column(name = "gjenopptak")
    val gjenopptak: Boolean?,
    @Column(name = "forsterket_rett")
    val forsterketRett: Boolean,
    @Column(name = "ettersendelse")
    val ettersendelse: Boolean,
    @Column(name = "lovhenvisning")
    val lovhenvisning: String,
    @Embedded
    @AttributeOverrides(
        value = [
            AttributeOverride(name = "partId.type", column = Column(name = "representant_part_id_type")),
            AttributeOverride(name = "partId.value", column = Column(name = "representant_part_id_value")),
            AttributeOverride(name = "navn", column = Column(name = "representant_navn")),
            AttributeOverride(name = "adresse.adressetype", column = Column(name = "representant_adresse_adressetype")),
            AttributeOverride(name = "adresse.adresselinje1", column = Column(name = "representant_adresse_adresselinje_1")),
            AttributeOverride(name = "adresse.adresselinje2", column = Column(name = "representant_adresse_adresselinje_2")),
            AttributeOverride(name = "adresse.adresselinje3", column = Column(name = "representant_adresse_adresselinje_3")),
            AttributeOverride(name = "adresse.postnummer", column = Column(name = "representant_adresse_postnummer")),
            AttributeOverride(name = "adresse.poststed", column = Column(name = "representant_adresse_poststed")),
            AttributeOverride(name = "adresse.land", column = Column(name = "representant_adresse_land")),
        ]
    )
    val representant: Representant?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrygderettenMetadata

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.*
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.Tema
import java.util.*

@Entity
@Table(name = "journalfoeringdata", schema = "document")
data class JournalfoeringData(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Embedded
    @AttributeOverrides(
        value = [
            AttributeOverride(name = "type", column = Column(name = "saken_gjelder_type_id")),
            AttributeOverride(name = "value", column = Column(name = "saken_gjelder_value"))
        ]
    )
    val sakenGjelder: PartId,
    @Column(name = "tema_id")
    @Convert(converter = TemaConverter::class)
    val tema: Tema,
    @Column(name = "sak_fagsak_id")
    val sakFagsakId: String?,
    @Column(name = "sak_fagsystem_id")
    @Convert(converter = FagsystemConverter::class)
    val sakFagsystem: Fagsystem?,
    @Column(name = "kilde_referanse")
    val kildeReferanse: String,
    @Column(name = "enhet")
    val enhet: String,
    @Column(name = "behandlingstema")
    val behandlingstema: String,
    @Column(name = "tittel")
    val tittel: String,
    @Column(name = "brevkode")
    val brevKode: String,
    @Embedded
    @AttributeOverrides(
        value = [
            AttributeOverride(name = "key", column = Column(name = "tilleggsopplysning_key")),
            AttributeOverride(name = "value", column = Column(name = "tilleggsopplysning_value"))
        ]
    )
    val tilleggsopplysning: Tilleggsopplysning?,
)


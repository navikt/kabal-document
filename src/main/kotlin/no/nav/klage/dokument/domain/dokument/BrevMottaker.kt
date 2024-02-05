package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "brevmottaker", schema = "document")
class BrevMottaker(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Embedded
    @AttributeOverrides(
        value = [
            AttributeOverride(name = "type", column = Column(name = "part_id_type_id")),
            AttributeOverride(name = "value", column = Column(name = "part_id_value"))
        ]
    )
    val partId: PartId,
    @Column(name = "navn")
    val navn: String?,
    @Column(name = "tving_sentral_print")
    val tvingSentralPrint: Boolean,
    @Embedded
    @AttributeOverrides(
        value = [
            AttributeOverride(name = "adressetype", column = Column(name = "adresse_adressetype")),
            AttributeOverride(name = "adresselinje1", column = Column(name = "adresse_adresselinje_1")),
            AttributeOverride(name = "adresselinje2", column = Column(name = "adresse_adresselinje_2")),
            AttributeOverride(name = "adresselinje3", column = Column(name = "adresse_adresselinje_3")),
            AttributeOverride(name = "postnummer", column = Column(name = "adresse_postnummer")),
            AttributeOverride(name = "poststed", column = Column(name = "adresse_poststed")),
            AttributeOverride(name = "land", column = Column(name = "adresse_land")),
        ]
    )
    val adresse: Adresse?,
    @Column(name = "local_print")
    val localPrint: Boolean,
)
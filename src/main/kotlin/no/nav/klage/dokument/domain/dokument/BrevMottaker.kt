package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "brevmottaker", schema = "document")
data class BrevMottaker(
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
)
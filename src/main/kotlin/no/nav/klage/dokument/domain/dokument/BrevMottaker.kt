package no.nav.klage.dokument.domain.dokument

import no.nav.klage.dokument.domain.kodeverk.Rolle
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "brevmottaker", schema = "document")
open class BrevMottaker(
    @Id
    open val id: UUID = UUID.randomUUID(),
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
    @Column(name = "Rolle")
    @Enumerated(EnumType.STRING)
    val rolle: Rolle,
)
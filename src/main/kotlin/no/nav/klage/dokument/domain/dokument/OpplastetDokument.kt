package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.util.*

@Entity
@Table(name = "opplastetdokument", schema = "document")
@DiscriminatorColumn(name = "type")
abstract class OpplastetDokument(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "mellomlager_id")
    val mellomlagerId: String,
    @Column(name = "name")
    val name: String,
    @Column(name = "type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    val type: OpplastetDokumentType,
    @Column(name = "index")
    val index: Int,
    @Column(name = "source_reference")
    val sourceReference: UUID?,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "opplastet_dokument_id", referencedColumnName = "id", nullable = false)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    val dokumentInfoReferenceList: MutableList<DokumentInfoReference> = mutableListOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OpplastetDokument

        if (id != other.id) return false
        if (mellomlagerId != other.mellomlagerId) return false
        return name == other.name
    }

    override fun hashCode(): Int = id.hashCode()
}

enum class OpplastetDokumentType {
    HOVEDDOKUMENT,
    VEDLEGG
}
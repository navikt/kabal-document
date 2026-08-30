package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.domain.dokument.TrygderettenMetadata
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TrygderettenMetadataRepository : JpaRepository<TrygderettenMetadata, UUID> {
    fun findByDokumentEnhetId(dokumentEnhetId: UUID): TrygderettenMetadata?
}

package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DokumentEnhetRepository : JpaRepository<DokumentEnhet, UUID>
package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.domain.dokument.AvsenderMottakerDistribusjon
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AvsenderMottakerDistribusjonRepository : JpaRepository<AvsenderMottakerDistribusjon, UUID>
package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.domain.dokument.BrevMottakerDistribusjon
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BrevMottakerDistribusjonRepository : JpaRepository<BrevMottakerDistribusjon, UUID>
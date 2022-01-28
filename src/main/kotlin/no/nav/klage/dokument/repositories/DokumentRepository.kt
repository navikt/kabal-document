package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.domain.dokument2.Dokument
import no.nav.klage.dokument.domain.dokument2.HovedDokument
import no.nav.klage.dokument.domain.dokument2.Vedlegg
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import java.util.*


@NoRepositoryBean
interface DokumentRepository<T : Dokument> : JpaRepository<T, UUID> {
    @Query("select d from #{#entityName} as d from dokument where d.ekstern_referanse = eksternReferanse")
    fun findByEksternReferanse(eksternReferanse: String): T
}

interface HovedDokumentRepository : DokumentRepository<HovedDokument>

interface VedleggRepository : DokumentRepository<Vedlegg>
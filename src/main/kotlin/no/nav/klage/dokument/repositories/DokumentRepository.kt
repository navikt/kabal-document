package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.domain.dokument2.Dokument
import no.nav.klage.dokument.domain.dokument2.HovedDokument
import no.nav.klage.dokument.domain.dokument2.Vedlegg
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


@NoRepositoryBean
interface BaseDokumentRepository<T : Dokument> : Repository<T, UUID> {
    //@Query("select d from #{#entityName} as d from dokument where d.ekstern_referanse = eksternReferanse")

    fun findById(id: UUID): Optional<T>
    fun existsById(id: UUID): Boolean
    fun findAll(): Iterable<T>
}


@Transactional
interface DokumentRepository : BaseDokumentRepository<Dokument> {
    fun findOne(id: UUID): Dokument? = findById(id).orElse(null)
}

@Transactional
interface HovedDokumentRepository : BaseDokumentRepository<HovedDokument>, JpaRepository<HovedDokument, UUID> {
    fun findByEksternReferanse(eksternReferanse: String): List<HovedDokument>
}

@Transactional
interface VedleggRepository : BaseDokumentRepository<Vedlegg>, JpaRepository<Vedlegg, UUID>
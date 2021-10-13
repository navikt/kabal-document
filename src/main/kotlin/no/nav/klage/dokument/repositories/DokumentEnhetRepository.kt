package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.domain.kodeverk.Fagsystem
import no.nav.klage.dokument.domain.kodeverk.PartIdType
import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.domain.kodeverk.Tema
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class DokumentEnhetRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun findDokumentEnheterForDistribusjon(): List<UUID> {
        return jdbcTemplate.query("SELECT id FROM dokumentenhet WHERE avsluttet_av_saksbehandler IS NOT NULL AND avsluttet IS NULL")
        { rs: ResultSet, _: Int ->
            rs.getObject("id", UUID::class.java)
        }
    }

    fun findById(dokumentEnhetId: UUID): DokumentEnhet? {
        jdbcTemplate.query("SELECT id FROM dokumentenhet")
        { rs: ResultSet, _: Int ->
            rs.getObject("id", UUID::class.java)
        }.firstOrNull() ?: return null

        return try {
            val journalfoeringData = getJournalfoeringData(dokumentEnhetId)
            val brevMottakere = getBrevMottakere(dokumentEnhetId)
            val hovedDokument = getHovedDokument(dokumentEnhetId)
            val vedlegg = getVedlegg(dokumentEnhetId)
            val brevMottakerDistribusjoner = getBrevMottakerDistribusjoner(dokumentEnhetId)

            getDokumentEnhet(
                dokumentEnhetId,
                journalfoeringData,
                brevMottakere,
                hovedDokument,
                vedlegg,
                brevMottakerDistribusjoner
            )
        } catch (nsee: NoSuchElementException) {
            null
        }
    }

    private fun getDokumentEnhet(
        dokumentEnhetId: UUID,
        journalfoeringData: JournalfoeringData,
        brevMottakere: List<BrevMottaker>,
        hovedDokument: OpplastetDokument?,
        vedlegg: List<OpplastetDokument>,
        brevMottakerDistribusjoner: List<BrevMottakerDistribusjon>
    ): DokumentEnhet? {
        return jdbcTemplate.queryForObject(
            "SELECT id FROM dokumentenhet WHERE dokumentenhet_id = ?",
            { rs: ResultSet, _: Int ->
                DokumentEnhet(
                    id = rs.getObject("id", UUID::class.java),
                    eier = SaksbehandlerIdent(navIdent = rs.getString("eier")),
                    journalfoeringData = journalfoeringData,
                    brevMottakere = brevMottakere,
                    hovedDokument = hovedDokument,
                    vedlegg = vedlegg,
                    brevMottakerDistribusjoner = brevMottakerDistribusjoner,
                    avsluttetAvSaksbehandler = rs.getObject("avsluttet_av_saksbehandler", LocalDateTime::class.java),
                    avsluttet = rs.getObject("avsluttet", LocalDateTime::class.java),
                    modified = rs.getObject("modified", LocalDateTime::class.java),
                )
            }, dokumentEnhetId
        )
    }

    private fun getBrevMottakerDistribusjoner(dokumentEnhetId: UUID): List<BrevMottakerDistribusjon> {
        return jdbcTemplate.query(
            "SELECT id FROM brevmottakerdist WHERE dokumentenhet_id = ?",
            { rs: ResultSet, _: Int ->
                BrevMottakerDistribusjon(
                    id = rs.getObject("id", UUID::class.java),
                    brevMottakerId = rs.getObject("brev_mottaker_id", UUID::class.java),
                    opplastetDokumentId = rs.getObject("opplastet_dokument_id", UUID::class.java),
                    journalpostId = JournalpostId(value = rs.getString("journalpost_id")),
                    ferdigstiltIJoark = rs.getObject("ferdigstilt_i_joark", LocalDateTime::class.java),
                    dokdistReferanse = rs.getObject("dokdist_referanse", UUID::class.java)
                )
            }, dokumentEnhetId
        )
    }

    private fun getHovedDokument(dokumentEnhetId: UUID): OpplastetDokument? {
        return jdbcTemplate.queryForObject(
            "SELECT id FROM hoveddokument WHERE dokumentenhet_id = ?",
            { rs: ResultSet, _: Int ->
                OpplastetDokument(
                    id = rs.getObject("id", UUID::class.java),
                    mellomlagerId = rs.getString("mellomlager_id"),
                    opplastet = rs.getObject("opplastet", LocalDateTime::class.java),
                    size = rs.getLong("size"),
                    name = rs.getString("name"),
                )
            }, dokumentEnhetId
        )
    }

    private fun getVedlegg(dokumentEnhetId: UUID): List<OpplastetDokument> {
        return jdbcTemplate.query(
            "SELECT id FROM vedlegg WHERE dokumentenhet_id = ?",
            { rs: ResultSet, _: Int ->
                OpplastetDokument(
                    id = rs.getObject("id", UUID::class.java),
                    mellomlagerId = rs.getString("mellomlager_id"),
                    opplastet = rs.getObject("opplastet", LocalDateTime::class.java),
                    size = rs.getLong("size"),
                    name = rs.getString("name"),
                )
            }, dokumentEnhetId
        )
    }

    private fun getJournalfoeringData(dokumentEnhetId: UUID): JournalfoeringData {
        return jdbcTemplate.queryForObject(
            "SELECT id FROM journalfoeringdata WHERE dokumentenhet_id = ?",
            { rs: ResultSet, _: Int ->
                JournalfoeringData(
                    id = rs.getObject("id", UUID::class.java),
                    sakenGjelder = PartId(
                        type = PartIdType.valueOf(rs.getString("saken_gjelder_type")),
                        value = rs.getString("saken_gjelder_value")
                    ),
                    tema = Tema.valueOf(rs.getString("tema")),
                    sakFagsakId = rs.getString("sak_fagsak_id"),
                    sakFagsystem = Fagsystem.valueOf(rs.getString("sak_fagsystem")),
                    kildeReferanse = rs.getString("kilde_referanse"),
                    enhet = rs.getString("enhet")
                )
            }, dokumentEnhetId
        )
    }

    private fun getBrevMottakere(dokumentEnhetId: UUID): List<BrevMottaker> {
        return jdbcTemplate.query(
            "SELECT id FROM brevmottaker WHERE dokumentenhet_id = ?",
            { rs: ResultSet, _: Int ->
                BrevMottaker(
                    id = rs.getObject("id", UUID::class.java),
                    partId = PartId(
                        type = PartIdType.valueOf(rs.getString("part_id_type")),
                        value = rs.getString("part_id_value")
                    ),
                    navn = rs.getString("navn"),
                    rolle = Rolle.valueOf(rs.getString("rolle"))
                )
            }, dokumentEnhetId
        )
    }

    fun save(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        insertDokumentEnhet(dokumentEnhet)
        insertJournalfoeringData(dokumentEnhet.journalfoeringData, dokumentEnhet.id)
        dokumentEnhet.brevMottakere.forEach {
            insertBrevMottaker(it, dokumentEnhet.id)
        }
        dokumentEnhet.hovedDokument?.let { insertOpplastetDokument(it, dokumentEnhet.id, "hoveddokument") }
        dokumentEnhet.vedlegg.forEach {
            insertOpplastetDokument(it, dokumentEnhet.id, "vedlegg")
        }
        dokumentEnhet.brevMottakerDistribusjoner.forEach {
            insertBrevMottakerDistribusjon(it, dokumentEnhet.id)
        }
        return dokumentEnhet
    }

    private fun insertDokumentEnhet(dokumentEnhet: DokumentEnhet) {
        SimpleJdbcInsert(jdbcTemplate).withTableName("dokumentenhet").apply {
            execute(
                mapOf(
                    "id" to dokumentEnhet.id,
                    "eier" to dokumentEnhet.eier.navIdent,
                    "avsluttet_av_saksbehandler" to dokumentEnhet.avsluttetAvSaksbehandler,
                    "avsluttet" to dokumentEnhet.avsluttet,
                    "modified" to dokumentEnhet.modified
                )
            )
        }
    }

    private fun insertBrevMottakerDistribusjon(
        brevMottakerDistribusjon: BrevMottakerDistribusjon,
        dokumentEnhetId: UUID
    ) {
        SimpleJdbcInsert(jdbcTemplate).withTableName("brevmottakerdist").apply {
            execute(
                mapOf(
                    "id" to brevMottakerDistribusjon.id,
                    "brev_mottaker_id" to brevMottakerDistribusjon.brevMottakerId,
                    "opplastet_dokument_id" to brevMottakerDistribusjon.opplastetDokumentId,
                    "journalpost_id" to brevMottakerDistribusjon.journalpostId,
                    "ferdigstilt_i_joark" to brevMottakerDistribusjon.ferdigstiltIJoark,
                    "dokdist_referanse" to brevMottakerDistribusjon.dokdistReferanse,
                    "dokumentenhet_id" to dokumentEnhetId
                )
            )
        }
    }

    private fun insertOpplastetDokument(
        opplastetDokument: OpplastetDokument,
        dokumentEnhetId: UUID,
        tableName: String
    ) {
        SimpleJdbcInsert(jdbcTemplate).withTableName(tableName).apply {
            execute(
                mapOf(
                    "id" to opplastetDokument.id,
                    "mellomlager_id" to opplastetDokument.mellomlagerId,
                    "opplastet" to opplastetDokument.opplastet,
                    "size" to opplastetDokument.size,
                    "name" to opplastetDokument.name,
                    "dokumentenhet_id" to dokumentEnhetId
                )
            )
        }
    }

    private fun insertBrevMottaker(brevMottaker: BrevMottaker, dokumentEnhetId: UUID) {
        SimpleJdbcInsert(jdbcTemplate).withTableName("brevMottaker").apply {
            execute(
                mapOf(
                    "id" to brevMottaker.id,
                    "part_id_type" to brevMottaker.partId.type,
                    "part_id_value" to brevMottaker.partId.value,
                    "navn" to brevMottaker.navn,
                    "rolle" to brevMottaker.rolle.name,
                    "dokumentenhet_id" to dokumentEnhetId
                )
            )
        }
    }

    private fun insertJournalfoeringData(journalfoeringData: JournalfoeringData, dokumentEnhetId: UUID) {
        SimpleJdbcInsert(jdbcTemplate).withTableName("journalfoeringdata").apply {
            execute(
                mapOf(
                    "id" to journalfoeringData.id,
                    "saken_gjelder_type" to journalfoeringData.sakenGjelder.type,
                    "saken_gjelder_value" to journalfoeringData.sakenGjelder.value,
                    "tema" to journalfoeringData.tema.name,
                    "sak_fagsak_id" to journalfoeringData.sakFagsakId,
                    "sak_fagsystem" to journalfoeringData.sakFagsystem?.name,
                    "kilde_referanse" to journalfoeringData.kildeReferanse,
                    "enhet" to journalfoeringData.enhet,
                    "dokumentenhet_id" to dokumentEnhetId
                )
            )
        }
    }

    fun saveOrUpdate(dokumentEnhet: DokumentEnhet) {
        delete(dokumentEnhet.id)
        save(dokumentEnhet)
    }

    fun delete(dokumentEnhetId: UUID) {
        jdbcTemplate.update("DELETE FROM opplastetdokument WHERE dokumentenhet_id = ?", dokumentEnhetId)
        jdbcTemplate.update("DELETE FROM brevmottakerdist WHERE dokumentenhet_id = ?", dokumentEnhetId)
        jdbcTemplate.update("DELETE FROM vedlegg WHERE dokumentenhet_id = ?", dokumentEnhetId)
        jdbcTemplate.update("DELETE FROM hoveddokument WHERE dokumentenhet_id = ?", dokumentEnhetId)
        jdbcTemplate.update("DELETE FROM brevmottakere WHERE dokumentenhet_id = ?", dokumentEnhetId)
        jdbcTemplate.update("DELETE FROM journalfoeringdata WHERE dokumentenhet_id = ?", dokumentEnhetId)
        jdbcTemplate.update("DELETE FROM dokumentenhet WHERE id = ?", dokumentEnhetId)
    }
}
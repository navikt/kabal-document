package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class DokumentEnhetRepository(private val jdbcTemplate: JdbcTemplate) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun findById(dokumentEnhetId: UUID): DokumentEnhet? {
        if (!exists(dokumentEnhetId)) return null

        val journalfoeringData = getJournalfoeringData(dokumentEnhetId) ?: return null
        val brevMottakere = getBrevMottakere(dokumentEnhetId)
        val hovedDokument = getHovedDokument(dokumentEnhetId)
        val vedlegg = getVedlegg(dokumentEnhetId)
        val brevMottakerDistribusjoner = getBrevMottakerDistribusjoner(dokumentEnhetId)

        return getDokumentEnhet(
            dokumentEnhetId,
            journalfoeringData,
            brevMottakere,
            hovedDokument,
            vedlegg,
            brevMottakerDistribusjoner,
        )
    }

    fun saveOrUpdate(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        delete(dokumentEnhet.id)
        save(dokumentEnhet)
        return dokumentEnhet
    }

    fun save(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        insertDokumentEnhet(dokumentEnhet)
        insertJournalfoeringData(dokumentEnhet.journalfoeringData, dokumentEnhet.id)
        dokumentEnhet.brevMottakere.forEach {
            insertBrevMottaker(it, dokumentEnhet.id)
        }
        dokumentEnhet.hovedDokument?.let { insertOpplastetDokument(it, dokumentEnhet.id, "HOVEDDOKUMENT") }
        dokumentEnhet.vedlegg.forEach {
            insertOpplastetDokument(it, dokumentEnhet.id, "VEDLEGG")
        }
        dokumentEnhet.brevMottakerDistribusjoner.forEach {
            insertBrevMottakerDistribusjon(it, dokumentEnhet.id)
        }
        return dokumentEnhet
    }

    fun delete(dokumentEnhetId: UUID) {
        jdbcTemplate.update(
            "DELETE FROM document.opplastetdokument WHERE dokumentenhet_id = ?",
            dokumentEnhetId
        )
        jdbcTemplate.update("DELETE FROM document.brevmottakerdist WHERE dokumentenhet_id = ?", dokumentEnhetId)
        jdbcTemplate.update("DELETE FROM document.brevmottaker WHERE dokumentenhet_id = ?", dokumentEnhetId)
        jdbcTemplate.update(
            "DELETE FROM document.journalfoeringdata WHERE dokumentenhet_id = ?",
            dokumentEnhetId
        )
        jdbcTemplate.update("DELETE FROM document.dokumentenhet WHERE id = ?", dokumentEnhetId)
    }

    private fun exists(dokumentEnhetId: UUID): Boolean =
        jdbcTemplate.query(
            "SELECT id FROM document.dokumentenhet WHERE id = ?",
            { rs: ResultSet, _: Int ->
                rs.getObject("id", UUID::class.java)
            },
            dokumentEnhetId
        ).firstOrNull() != null

    private fun getDokumentEnhet(
        dokumentEnhetId: UUID,
        journalfoeringData: JournalfoeringData,
        brevMottakere: List<BrevMottaker>,
        hovedDokument: OpplastetDokument?,
        vedlegg: List<OpplastetDokument>,
        brevMottakerDistribusjoner: List<BrevMottakerDistribusjon>
    ): DokumentEnhet? {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM document.dokumentenhet WHERE id = ?",
            { rs: ResultSet, _: Int ->
                DokumentEnhet(
                    id = rs.getObject("id", UUID::class.java),
                    eier = SaksbehandlerIdent(navIdent = rs.getString("eier")),
                    journalfoeringData = journalfoeringData,
                    brevMottakere = brevMottakere,
                    hovedDokument = hovedDokument,
                    vedlegg = vedlegg,
                    dokumentType = DokumentType.of(rs.getString("dokument_type_id")),
                    brevMottakerDistribusjoner = brevMottakerDistribusjoner,
                    avsluttet = rs.getObject("avsluttet", LocalDateTime::class.java),
                    modified = rs.getObject("modified", LocalDateTime::class.java),
                )
            }, dokumentEnhetId
        )
    }

    private fun getBrevMottakerDistribusjoner(dokumentEnhetId: UUID): List<BrevMottakerDistribusjon> {
        return jdbcTemplate.query(
            "SELECT * FROM document.brevmottakerdist WHERE dokumentenhet_id = ?",
            { rs: ResultSet, _: Int ->
                BrevMottakerDistribusjon(
                    id = rs.getObject("id", UUID::class.java),
                    brevMottakerId = rs.getObject("brev_mottaker_id", UUID::class.java),
                    opplastetDokumentId = rs.getObject("opplastet_dokument_id", UUID::class.java),
                    journalpostId = JournalpostId(value = rs.getString("journalpost_id")),
                    ferdigstiltIJoark = rs.getObject("ferdigstilt_i_joark", LocalDateTime::class.java),
                    dokdistReferanse = rs.getObject("dokdist_referanse", UUID::class.java),
                )
            }, dokumentEnhetId
        )
    }

    fun getDokumentEnhetDokumentTypeFromBrevMottakerDistribusjonId(brevMottakerDistribusjonId: UUID): String? {
        return jdbcTemplate.query(
            "SELECT de.dokument_type_id FROM document.dokumentenhet AS de " +
                    "INNER JOIN document.brevmottakerdist AS bmd " +
                    "ON bmd.dokumentenhet_id = de.id " +
                    "WHERE bmd.id = ? ",
            { rs: ResultSet, _: Int ->
                rs.getString("dokument_type_id")

            }, brevMottakerDistribusjonId
        ).firstOrNull()
    }

    private fun getHovedDokument(dokumentEnhetId: UUID): OpplastetDokument? {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM document.opplastetdokument WHERE type = 'HOVEDDOKUMENT' AND  dokumentenhet_id = ?",
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
        } catch (t: Throwable) {
            logger.info("Feil i getHovedDokument", t)
            return null
        }
    }

    private fun getVedlegg(dokumentEnhetId: UUID): List<OpplastetDokument> {
        return jdbcTemplate.query(
            "SELECT * FROM document.opplastetdokument WHERE type = 'VEDLEGG' AND dokumentenhet_id = ?",
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

    private fun getJournalfoeringData(dokumentEnhetId: UUID): JournalfoeringData? {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM document.journalfoeringdata WHERE dokumentenhet_id = ?",
                { rs: ResultSet, _: Int ->
                    JournalfoeringData(
                        id = rs.getObject("id", UUID::class.java),
                        sakenGjelder = PartId(
                            type = PartIdType.of(rs.getString("saken_gjelder_type_id")),
                            value = rs.getString("saken_gjelder_value")
                        ),
                        tema = Tema.of(rs.getString("tema_id")),
                        sakFagsakId = rs.getString("sak_fagsak_id"),
                        sakFagsystem = rs.getString("sak_fagsystem_id")?.let {
                            Fagsystem.of(it)
                        },
                        kildeReferanse = rs.getString("kilde_referanse"),
                        enhet = rs.getString("enhet"),
                        behandlingstema = rs.getString("behandlingstema"),
                        tittel = rs.getString("tittel"),
                        brevKode = rs.getString("brevKode"),
                        tilleggsopplysning = nullSafeTilleggsopplysning(
                            rs.getString("tilleggsopplysning_key"),
                            rs.getString("tilleggsopplysning_value")
                        ),
                    )
                }, dokumentEnhetId
            )
        } catch (t: Throwable) {
            logger.info("Feil i getJournalfoeringData", t)
            return null
        }
    }

    private fun getBrevMottakere(dokumentEnhetId: UUID): List<BrevMottaker> {
        return jdbcTemplate.query(
            "SELECT * FROM document.brevmottaker WHERE dokumentenhet_id = ?",
            { rs: ResultSet, _: Int ->
                BrevMottaker(
                    id = rs.getObject("id", UUID::class.java),
                    partId = PartId(
                        type = PartIdType.of(rs.getString("part_id_type_id")),
                        value = rs.getString("part_id_value")
                    ),
                    navn = rs.getString("navn"),
                    rolle = Rolle.valueOf(rs.getString("rolle")),
                )
            }, dokumentEnhetId
        )
    }

    private fun insertDokumentEnhet(dokumentEnhet: DokumentEnhet) {
        SimpleJdbcInsert(jdbcTemplate).withSchemaName("document").withTableName("dokumentenhet").apply {
            execute(
                mapOf(
                    "id" to dokumentEnhet.id,
                    "eier" to dokumentEnhet.eier.navIdent,
                    "avsluttet" to dokumentEnhet.avsluttet,
                    "modified" to dokumentEnhet.modified,
                    "dokument_type_id" to dokumentEnhet.dokumentType.id,
                )
            )
        }
    }

    private fun insertBrevMottakerDistribusjon(
        brevMottakerDistribusjon: BrevMottakerDistribusjon,
        dokumentEnhetId: UUID
    ) {
        SimpleJdbcInsert(jdbcTemplate).withSchemaName("document").withTableName("brevmottakerdist").apply {
            execute(
                mapOf(
                    "id" to brevMottakerDistribusjon.id,
                    "brev_mottaker_id" to brevMottakerDistribusjon.brevMottakerId,
                    "opplastet_dokument_id" to brevMottakerDistribusjon.opplastetDokumentId,
                    "journalpost_id" to brevMottakerDistribusjon.journalpostId.value,
                    "ferdigstilt_i_joark" to brevMottakerDistribusjon.ferdigstiltIJoark,
                    "dokdist_referanse" to brevMottakerDistribusjon.dokdistReferanse,
                    "dokumentenhet_id" to dokumentEnhetId,
                )
            )
        }
    }

    private fun insertOpplastetDokument(
        opplastetDokument: OpplastetDokument,
        dokumentEnhetId: UUID,
        type: String
    ) {
        SimpleJdbcInsert(jdbcTemplate).withSchemaName("document").withTableName("opplastetdokument").apply {
            execute(
                mapOf(
                    "id" to opplastetDokument.id,
                    "mellomlager_id" to opplastetDokument.mellomlagerId,
                    "opplastet" to opplastetDokument.opplastet,
                    "size" to opplastetDokument.size,
                    "name" to opplastetDokument.name,
                    "type" to type,
                    "dokumentenhet_id" to dokumentEnhetId,
                )
            )
        }
    }

    private fun insertBrevMottaker(brevMottaker: BrevMottaker, dokumentEnhetId: UUID) {
        SimpleJdbcInsert(jdbcTemplate).withSchemaName("document").withTableName("brevMottaker").apply {
            execute(
                mapOf(
                    "id" to brevMottaker.id,
                    "part_id_type_id" to brevMottaker.partId.type.id,
                    "part_id_value" to brevMottaker.partId.value,
                    "navn" to brevMottaker.navn,
                    "rolle" to brevMottaker.rolle.name,
                    "dokumentenhet_id" to dokumentEnhetId,
                )
            )
        }
    }

    private fun insertJournalfoeringData(
        journalfoeringData: JournalfoeringData,
        dokumentEnhetId: UUID
    ) {
        SimpleJdbcInsert(jdbcTemplate)
            .withSchemaName("document")
            .withTableName("journalfoeringdata")
            .apply {
                execute(
                    mapOf(
                        "id" to journalfoeringData.id,
                        "saken_gjelder_type_id" to journalfoeringData.sakenGjelder.type.id,
                        "saken_gjelder_value" to journalfoeringData.sakenGjelder.value,
                        "tema_id" to journalfoeringData.tema.id,
                        "sak_fagsak_id" to journalfoeringData.sakFagsakId,
                        "sak_fagsystem_id" to journalfoeringData.sakFagsystem?.id,
                        "kilde_referanse" to journalfoeringData.kildeReferanse,
                        "enhet" to journalfoeringData.enhet,
                        "behandlingstema" to journalfoeringData.behandlingstema,
                        "tittel" to journalfoeringData.tittel,
                        "brevKode" to journalfoeringData.brevKode,
                        "tilleggsopplysning_key" to journalfoeringData.tilleggsopplysning?.key,
                        "tilleggsopplysning_value" to journalfoeringData.tilleggsopplysning?.value,
                        "dokumentenhet_id" to dokumentEnhetId,
                    )
                )
            }
    }


    private fun nullSafeTilleggsopplysning(key: String?, value: String?): Tilleggsopplysning? =
        if (key != null && value != null) {
            Tilleggsopplysning(key, value)
        } else null
}
package no.nav.klage.dokument.db

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@ActiveProfiles("local")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FlywayMigrationTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    data class Utfall(val id: Long, val navn: String)

    @Test
    fun flyway_should_run() {
        /*
        val saksdokumenter: List<Saksdokument> = jdbcTemplate.query(
            "SELECT * FROM klage.saksdokument"
        ) { rs: ResultSet, _: Int ->
            Saksdokument(
                journalpostId = rs.getString("journalpost_id"),
                dokumentInfoId = rs.getString("dokument_info_id")
            )
        }

        assertThat(saksdokumenter).hasSize(0)
        */
    }

}

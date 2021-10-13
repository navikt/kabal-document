package no.nav.klage.dokument.db

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.ResultSet
import java.util.*


@ActiveProfiles("local")
@Testcontainers
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FlywayMigrationTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun flyway_should_run() {
        val result: UUID? = jdbcTemplate.query("SELECT id FROM document.dokumentenhet")
        { rs: ResultSet, _: Int ->
            rs.getObject("id", UUID::class.java)
        }.firstOrNull()

        assertThat(result).isNull()
    }

}

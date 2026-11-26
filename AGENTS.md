# Agentinstrukser for kabal-document

## Prosjektet

Kotlin/Spring Boot med Gradle Kotlin DSL og Java 25.
Hovedkode ligger i `src/main/kotlin/no/nav/klage/dokument/`.
Databaseendringer ligger i `src/main/resources/db/migration/`.
Nais-oppsett ligger i `nais/`.

## Før du endrer kode

Les relevant kode og tester først.
Gjenbruk eksisterende mønstre for klienter og integrasjoner.
Hold endringen avgrenset. Ikke overskriv andres lokale endringer.

## Kodestil

Koden følger `ktlint_official` med maks linjelengde 140 (se `.editorconfig`).
Kjør `./gradlew ktlintFormat` etter hver kodeendring, og deretter `./gradlew ktlintCheck`.

Detekt krever navngitte argumenter når et kall har mer enn ett argument
(`NamedArguments`). Det gjelder også konstruktørkall i tester.

## Database

Legg skjemaendringer i nye Flyway-migrasjoner med neste ledige versjonsnummer.
Ikke endre migrasjoner som allerede er kjørt i delte miljøer.
Vurder eksisterende data og kompatibilitet ved skjemaendringer.

## Personvern og sikkerhet

Bruk syntetiske testdata.
Ikke legg personopplysninger, dokumentinnhold eller secrets i
prompter, logger eller nye testdata.
Alle beskyttede endepunkter valideres med Azure (Entra ID), se `SecurityConfiguration.kt`.
Avklar endringer i autentisering, autorisasjon og behandling
av personopplysninger før implementering.

## Verifisering

Kjør relevante tester for endringen først.
Integrasjonstester bruker Testcontainers og krever Docker.

CI bruker:

- `./gradlew ktlintCheck`
- `./gradlew detektMain detektTest`
- `./gradlew test build`

Kjør `ktlintCheck` og `detektMain detektTest` lokalt før du sier at en endring er ferdig.
Ikke svekk tester eller statiske kontroller for å få grønt bygg.
Rapporter hva som er endret, og hva som ikke kunne verifiseres.

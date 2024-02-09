package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Adresse(
    @Column(name = "adressetype")
    val adressetype: String,
    @Column(name = "adresselinje_1")
    val adresselinje1: String?,
    @Column(name = "adresselinje_2")
    val adresselinje2: String?,
    @Column(name = "adresselinje_3")
    val adresselinje3: String?,
    @Column(name = "postnummer")
    val postnummer: String?,
    @Column(name = "poststed")
    val poststed: String?,
    @Column(name = "land")
    val land: String,
)

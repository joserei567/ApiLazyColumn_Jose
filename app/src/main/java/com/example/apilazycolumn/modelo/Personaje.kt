package com.example.apilazycolumn.modelo

data class Personaje(
    val id: Int,
    val name: String,
    val status: String,
    val species: String
)

data class RespuestaPersonajes(
    val results: List<Personaje>
)

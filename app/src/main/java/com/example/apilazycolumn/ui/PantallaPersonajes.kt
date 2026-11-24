package com.example.apilazycolumn.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.apilazycolumn.R
import com.example.apilazycolumn.api.RetrofitClient
import com.example.apilazycolumn.modelo.Personaje
import java.io.IOException
import retrofit2.HttpException

@Composable
fun PantallaPersonajes() {
    var personajes by remember { mutableStateOf(listOf<Personaje>()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var busqueda by remember { mutableStateOf("") }
    var consulta by remember { mutableStateOf("") }
    var recarga by remember { mutableIntStateOf(0) }
    val teclado = LocalSoftwareKeyboardController.current

    LaunchedEffect(recarga) {
        cargando = true
        error = ""
        personajes = emptyList()
        try {
            val nombre = consulta.ifBlank { null }
            personajes = RetrofitClient.api.obtenerPersonajes(nombre).results
        } catch (e: IOException) {
            error = "No se ha podido conectar. Comprueba Internet y pulsa Actualizar."
        } catch (e: HttpException) {
            if (e.code() != 404 || consulta.isBlank()) {
                error = "La API no ha podido responder. Inténtalo de nuevo."
            }
        } finally {
            cargando = false
        }
    }

    Scaffold { margen ->
        Column(
            modifier = Modifier.fillMaxSize().padding(margen).padding(16.dp)
        ) {
            Text("Personajes de Rick y Morty", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar por nombre") },
                singleLine = true,
                enabled = !cargando,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        teclado?.hide()
                        consulta = busqueda.trim()
                        recarga++
                    },
                    enabled = !cargando
                ) {
                    Text("Buscar")
                }
                OutlinedButton(
                    onClick = {
                        teclado?.hide()
                        recarga++
                    },
                    enabled = !cargando
                ) {
                    Text("Actualizar")
                }
            }

            when {
                cargando -> {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error.isNotEmpty() -> {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
                personajes.isEmpty() -> {
                    Text("No se han encontrado personajes.")
                }
                else -> {
                    Text("Resultados: ${personajes.size}", style = MaterialTheme.typography.labelLarge)
                    Text("Se muestra la primera página de la API.", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(personajes, key = { it.id }) { personaje ->
                            TarjetaPersonaje(personaje)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaPersonaje(personaje: Personaje) {
    val estado = when (personaje.status) {
        "Alive" -> "Vivo"
        "Dead" -> "Muerto"
        else -> "Desconocido"
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_personaje),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(personaje.name, style = MaterialTheme.typography.titleMedium)
                Text("Especie: ${personaje.species}")
                Text("Estado: $estado")
            }
        }
    }
}


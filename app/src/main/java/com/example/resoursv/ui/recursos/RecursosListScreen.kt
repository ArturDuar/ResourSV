package com.example.resoursv.ui.recursos

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resoursv.data.model.Recurso
import com.example.resoursv.ui.components.RecursoCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecursosListScreen(onAdd: () -> Unit) {
    var recursos by remember { mutableStateOf<List<Recurso>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
// Aquí normalmente llamarías al controlador para cargar recursos
        loading = true
// Simular carga
        recursos = listOf(
            Recurso(id = "1", titulo = "Kotlin Basics", descripcion = "Introducción a Kotlin", tipo = "video", enlace = "https://example.com", imagen = "")
        )
        loading = false
    }


    Scaffold(topBar = { MediumTopAppBar(title = { Text("Recursos") }) }, floatingActionButton = {
        FloatingActionButton(onClick = onAdd) { Text("+") }
    }) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    items(recursos) { r -> RecursoCard(recurso = r) }
                }
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}
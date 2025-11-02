package com.example.resoursv.ui.recursos

import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.resoursv.data.model.Recurso

@Composable
fun RecursoFormScreen(onSaved: () -> Unit, recursoInicial: Recurso? = null) {
    var titulo by remember { mutableStateOf(recursoInicial?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(recursoInicial?.descripcion ?: "") }
    var tipo by remember { mutableStateOf(recursoInicial?.tipo ?: "") }
    var enlace by remember { mutableStateOf(recursoInicial?.enlace ?: "") }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Tipo") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = enlace, onValueChange = { enlace = it }, label = { Text("Enlace") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onSaved()
        }) { Text("Guardar") }
    }
}
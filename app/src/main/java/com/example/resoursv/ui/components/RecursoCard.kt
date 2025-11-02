package com.example.resoursv.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.resoursv.data.model.Recurso

@Composable
fun RecursoCard(recurso: Recurso) {
    val ctx = LocalContext.current
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = recurso.titulo)
            Text(text = recurso.descripcion, modifier = Modifier.padding(top = 6.dp))
            Text(text = "Tipo: ${recurso.tipo}", modifier = Modifier.padding(top = 6.dp))
            Text(text = "Abrir recurso", modifier = Modifier
                .padding(top = 8.dp)
                .clickable {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(recurso.enlace))
                    ctx.startActivity(i)
                })
        }
    }
}
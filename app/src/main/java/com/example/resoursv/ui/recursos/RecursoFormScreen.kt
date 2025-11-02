package com.example.resoursv.ui.recursos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resoursv.data.model.Recurso
import com.example.resoursv.viewmodel.RecursoViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecursoFormScreen(
    recursoId: String? = null,
    onSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RecursoViewModel = viewModel()
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var enlace by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var loadingData by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isEditMode = recursoId != null

    // Cargar datos si es modo edición
    LaunchedEffect(recursoId) {
        if (recursoId != null) {
            loadingData = true
            viewModel.getRecursoById(
                recursoId,
                onSuccess = { recurso ->
                    titulo = recurso.titulo
                    descripcion = recurso.descripcion
                    tipo = recurso.tipo
                    enlace = recurso.enlace
                    imagen = recurso.imagen
                    loadingData = false
                },
                onError = { error ->
                    scope.launch {
                        snackbarHostState.showSnackbar(error)
                    }
                    loadingData = false
                }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Recurso" else "Nuevo Recurso") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, enabled = !loading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (loadingData) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    enabled = !loading,
                    supportingText = { Text("Nombre descriptivo del recurso") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    enabled = !loading,
                    supportingText = { Text("Describe brevemente el contenido del recurso") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tipo,
                    onValueChange = { tipo = it },
                    label = { Text("Tipo *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    enabled = !loading,
                    supportingText = { Text("Ej: video, artículo, curso, tutorial") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = enlace,
                    onValueChange = { enlace = it },
                    label = { Text("Enlace (URL) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next
                    ),
                    enabled = !loading,
                    supportingText = { Text("URL completa del recurso") },
                    isError = enlace.isNotBlank() &&
                            !enlace.startsWith("http://") &&
                            !enlace.startsWith("https://")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = imagen,
                    onValueChange = { imagen = it },
                    label = { Text("URL de la imagen (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    enabled = !loading,
                    supportingText = { Text("URL de una imagen representativa") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        when {
                            titulo.isBlank() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("El título es requerido")
                                }
                            }
                            descripcion.isBlank() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("La descripción es requerida")
                                }
                            }
                            tipo.isBlank() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("El tipo es requerido")
                                }
                            }
                            enlace.isBlank() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("El enlace es requerido")
                                }
                            }
                            !enlace.startsWith("http://") && !enlace.startsWith("https://") -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("El enlace debe comenzar con http:// o https://")
                                }
                            }
                            imagen.isNotBlank() && !imagen.startsWith("http://") && !imagen.startsWith("https://") -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("La URL de la imagen debe comenzar con http:// o https://")
                                }
                            }
                            else -> {
                                loading = true
                                val recurso = Recurso(
                                    id = recursoId ?: UUID.randomUUID().toString(),
                                    titulo = titulo.trim(),
                                    descripcion = descripcion.trim(),
                                    tipo = tipo.trim(),
                                    enlace = enlace.trim(),
                                    imagen = imagen.trim()
                                )

                                if (isEditMode) {
                                    viewModel.updateRecurso(
                                        recursoId!!,
                                        recurso,
                                        onSuccess = {
                                            loading = false
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Recurso actualizado exitosamente")
                                            }
                                            onSaved()
                                        },
                                        onError = { errorMsg ->
                                            loading = false
                                            scope.launch {
                                                snackbarHostState.showSnackbar(errorMsg)
                                            }
                                        }
                                    )
                                } else {
                                    viewModel.addRecurso(
                                        recurso,
                                        onSuccess = {
                                            loading = false
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Recurso agregado exitosamente")
                                            }
                                            onSaved()
                                        },
                                        onError = { errorMsg ->
                                            loading = false
                                            scope.launch {
                                                snackbarHostState.showSnackbar(errorMsg)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (isEditMode) "Actualizar" else "Guardar")
                    }
                }
            }
        }
    }
}
package com.example.resoursv.ui.recursos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resoursv.ui.components.RecursoCard
import com.example.resoursv.viewmodel.RecursoUiState
import com.example.resoursv.viewmodel.RecursoViewModel
import com.example.resoursv.viewmodel.SortOption
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecursosListScreen(
    onAdd: () -> Unit,
    onEdit: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: RecursoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTipo by viewModel.selectedTipo.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showFilterDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var searchExpanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (!searchExpanded) {
                            Column {
                                Text("Recursos")
                                currentUser?.email?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        if (searchExpanded) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.searchRecursos(it) },
                                placeholder = { Text("Buscar...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                            IconButton(onClick = {
                                searchExpanded = false
                                viewModel.searchRecursos("")
                            }) {
                                Icon(Icons.Default.Close, "Cerrar búsqueda", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        } else {
                            IconButton(onClick = { searchExpanded = true }) {
                                Icon(Icons.Default.Search, "Buscar", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                            IconButton(onClick = { showFilterDialog = true }) {
                                Icon(
                                    Icons.Default.FilterList,
                                    "Filtrar",
                                    tint = if (selectedTipo != null) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            IconButton(onClick = { showSortDialog = true }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, "Ordenar", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                            IconButton(onClick = { viewModel.loadRecursos() }) {
                                Icon(Icons.Default.Refresh, "Actualizar", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                            IconButton(onClick = onLogout) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar sesión", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                // Chips de filtros activos
                if (selectedTipo != null || searchQuery.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (searchQuery.isNotEmpty()) {
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.searchRecursos("") },
                                label = { Text("Buscar: $searchQuery") },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                        selectedTipo?.let { tipo ->
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.filterByTipo(null) },
                                label = { Text("Tipo: $tipo") },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdd,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Agregar recurso")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is RecursoUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is RecursoUiState.Success -> {
                    if (state.recursos.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (searchQuery.isNotEmpty() || selectedTipo != null)
                                    "No se encontraron recursos" else "No hay recursos disponibles",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (searchQuery.isEmpty() && selectedTipo == null)
                                    "Presiona + para agregar uno" else "Intenta con otros filtros",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(state.recursos, key = { it.id }) { recurso ->
                                RecursoCard(
                                    recurso = recurso,
                                    onEdit = { onEdit(recurso.id) },
                                    onDelete = {
                                        viewModel.deleteRecurso(
                                            recurso.id,
                                            onSuccess = {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Recurso eliminado")
                                                }
                                            },
                                            onError = { error ->
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(error)
                                                }
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                is RecursoUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error al cargar recursos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = state.message, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadRecursos() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }

    // Diálogo de filtros
    if (showFilterDialog) {
        val tipos = viewModel.getTiposUnicos()
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filtrar por tipo") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            viewModel.filterByTipo(null)
                            showFilterDialog = false
                        }
                    ) {
                        Text("Todos")
                    }
                    tipos.forEach { tipo ->
                        TextButton(
                            onClick = {
                                viewModel.filterByTipo(tipo)
                                showFilterDialog = false
                            }
                        ) {
                            Text(tipo)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Diálogo de ordenamiento
    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = { Text("Ordenar por") },
            text = {
                Column {
                    SortOptionItem("Más recientes", SortOption.RECIENTE, sortOption) {
                        viewModel.setSortOption(it)
                        showSortDialog = false
                    }
                    SortOptionItem("Título A-Z", SortOption.TITULO_ASC, sortOption) {
                        viewModel.setSortOption(it)
                        showSortDialog = false
                    }
                    SortOptionItem("Título Z-A", SortOption.TITULO_DESC, sortOption) {
                        viewModel.setSortOption(it)
                        showSortDialog = false
                    }
                    SortOptionItem("Tipo A-Z", SortOption.TIPO_ASC, sortOption) {
                        viewModel.setSortOption(it)
                        showSortDialog = false
                    }
                    SortOptionItem("Tipo Z-A", SortOption.TIPO_DESC, sortOption) {
                        viewModel.setSortOption(it)
                        showSortDialog = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun SortOptionItem(
    label: String,
    option: SortOption,
    currentOption: SortOption,
    onClick: (SortOption) -> Unit
) {
    TextButton(
        onClick = { onClick(option) },
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (option == currentOption) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            label,
            style = if (option == currentOption) MaterialTheme.typography.titleMedium
            else MaterialTheme.typography.bodyLarge
        )
    }
}
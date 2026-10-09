package com.sm.iwanttowatch.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil3.compose.AsyncImage
import com.sm.iwanttowatch.data.local.MovieEntity
import org.koin.androidx.compose.koinViewModel

private const val IMAGE = "https://image.tmdb.org/t/p/w500"
private val tabs = listOf("Início", "Buscar", "Minha lista", "Sortear")
@Composable
fun WantToWatchApp(vm: AppViewModel = koinViewModel()) {
    val nav = rememberNavController()
    val state by vm.state.collectAsState()
    val route by nav.currentBackStackEntryAsState()
    val current = route?.destination?.route
    Scaffold(bottomBar = {
        if (current in tabs) NavigationBar { tabs.forEachIndexed { i, label -> NavigationBarItem(selected = current == label, onClick = { nav.navigate(label) { launchSingleTop = true; restoreState = true } }, icon = { Text(listOf("⌂", "⌕", "▤", "↻")[i]) }, label = { Text(label) }) } }
    }) { padding ->
        NavHost(navController = nav, startDestination = "Início", modifier = Modifier.padding(padding)) {
            composable("Início") { HomeScreen(state, onOpen = { nav.navigate("details/${it.id}") }, onSettings = { nav.navigate("settings") }, onRefresh = vm::refresh) }
            composable("Buscar") { SearchScreen(vm, onOpen = { nav.navigate("details/${it.id}") }) }
            composable("Minha lista") { WatchlistScreen(state.watchlist, onOpen = { nav.navigate("details/${it.id}") }, onWatched = vm::watched) }
            composable("Sortear") { RandomScreen(state.watchlist, onOpen = { nav.navigate("details/${it.id}") }) }
            composable("settings") { SimplePage("Configurações", "Tema: seguir o sistema\nIdioma: Português\nRegião: Brasil\n\nWantToWatch • versão 1.0") }
            composable("details/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
                val id = entry.arguments?.getString("id") ?: ""
                val savedItem = state.watchlist.firstOrNull { it.id == id }
                val item = savedItem ?: vm.findById(id)
                if (item != null) DetailsScreen(item, saved = savedItem != null, onSave = { vm.toggle(item) }, onWatched = { vm.watched(item, !item.watched) }, onBack = { nav.popBackStack() })
                else SimplePage("Detalhes", "Adicione este título à sua lista para salvar e acompanhar seu status.", onBack = { nav.popBackStack() })
            }
        }
    }
}

@Composable private fun HomeScreen(state: HomeState, onOpen: (MovieEntity) -> Unit, onSettings: () -> Unit, onRefresh: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Column { Text("WANT TO WATCH", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary); Text("Seu próximo favorito", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }; TextButton(onClick = onSettings) { Text("⚙") } } }
        item { SectionTitle("Minha lista", "${state.watchlist.size} títulos") }
        if (state.watchlist.isEmpty()) item { EmptyCard("Sua lista começa aqui", "Busque filmes e séries para guardar o que quer assistir.") }
        else item { LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(state.watchlist.take(8)) { PosterCard(it, onOpen) } } }
        item { SectionTitle("Bem avaliados", null) }
        if (state.topRated.isEmpty()) item { ApiPrompt(state, onRefresh) }
        else item { LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(state.topRated.take(12)) { PosterCard(it, onOpen) } } }
        item { SectionTitle("Em alta", null) }
        if (state.trending.isEmpty()) item { if (state.topRated.isNotEmpty()) EmptyCard("Catálogo em alta", "Não há conteúdo disponível no momento.") }
        else item { LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(state.trending.take(12)) { PosterCard(it, onOpen) } } }
    }
}
@Composable private fun ApiPrompt(state: HomeState, retry: () -> Unit) {
    EmptyCard(if (state.error != null) state.error else "Conecte o catálogo", if (state.error != null) "Toque para tentar novamente." else "Configure TMDB_API_KEY em gradle.properties para descobrir títulos.", retry)
}
@Composable private fun SectionTitle(title: String, trailing: String?) { Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); trailing?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } } }
@Composable private fun PosterCard(item: MovieEntity, onOpen: (MovieEntity) -> Unit) { Column(Modifier.width(132.dp).clickable { onOpen(item) }) { Poster(item.posterPath, Modifier.fillMaxWidth().height(190.dp)); Spacer(Modifier.height(8.dp)); Text(item.title, maxLines = 1, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold); Text("★ ${"%.1f".format(item.rating)}  •  ${item.releaseDate.take(4)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
@Composable private fun Poster(path: String?, modifier: Modifier = Modifier) { Box(modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) { if (path != null) AsyncImage(IMAGE + path, contentDescription = "Poster", modifier = Modifier.fillMaxSize()) else Text("▶", color = MaterialTheme.colorScheme.primary) } }
@Composable private fun EmptyCard(title: String, body: String, action: (() -> Unit)? = null) { Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.surfaceVariant).clickable(enabled = action != null) { action?.invoke() }.padding(20.dp)) { Text(title, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(4.dp)); Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium) } }
@Composable private fun SearchScreen(vm: AppViewModel, onOpen: (MovieEntity) -> Unit) { var query by remember { mutableStateOf("") }; val results by vm.results.collectAsState(); Column(Modifier.fillMaxSize().padding(20.dp)) { Text("Buscar", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(16.dp)); OutlinedTextField(query, { query = it; vm.search(it) }, Modifier.fillMaxWidth(), placeholder = { Text("Filmes e séries") }, singleLine = true); Spacer(Modifier.height(12.dp)); if (results.isEmpty()) Text("Pesquise no catálogo TMDB para encontrar seu próximo título.", color = MaterialTheme.colorScheme.onSurfaceVariant); LazyColumn { items(results) { MediaRow(it, onOpen = onOpen) } } } }
@Composable private fun WatchlistScreen(items: List<MovieEntity>, onOpen: (MovieEntity) -> Unit, onWatched: (MovieEntity, Boolean) -> Unit) { var type by remember { mutableStateOf("Todos") }; var status by remember { mutableStateOf("Todos") }; Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) { Text("Minha lista", Modifier.padding(top = 20.dp), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf("Todos", "Filmes", "Séries").forEach { FilterChip(type == it, { type = it }, label = { Text(it) }) } }; Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf("Todos", "Não assistidos", "Assistidos").forEach { FilterChip(status == it, { status = it }, label = { Text(it) }) } }; val visible = items.filter { (type == "Todos" || (type == "Filmes") == (it.type == "movie")) && (status == "Todos" || (status == "Assistidos") == it.watched) }; LazyColumn { items(visible) { MediaRow(it, onOpen, onWatched) } } } }
@Composable private fun MediaRow(item: MovieEntity, onOpen: (MovieEntity) -> Unit, onWatched: ((MovieEntity, Boolean) -> Unit)? = null) { Row(Modifier.fillMaxWidth().clickable { onOpen(item) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { Poster(item.posterPath, Modifier.size(72.dp, 104.dp)); Column(Modifier.weight(1f).padding(start = 14.dp)) { Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold); Text("${item.releaseDate.take(4)}  •  ★ ${"%.1f".format(item.rating)}", style = MaterialTheme.typography.bodySmall); Text(if (item.type == "movie") "Filme" else "Série", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary); if (onWatched != null) TextButton(onClick = { onWatched(item, !item.watched) }) { Text(if (item.watched) "✓ Assistido" else "Marcar assistido") } } } }
@Composable private fun RandomScreen(items: List<MovieEntity>, onOpen: (MovieEntity) -> Unit) { var selected by remember { mutableStateOf<MovieEntity?>(null) }; Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text("O que assistir?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(24.dp)); selected?.let { Poster(it.posterPath, Modifier.size(220.dp, 320.dp)); Text(it.title, Modifier.padding(top = 16.dp), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("★ ${"%.1f".format(it.rating)}  •  ${it.releaseDate.take(4)}  •  ${if (it.type == "movie") "Filme" else "Série"}"); TextButton(onClick = { onOpen(it) }) { Text("Ver detalhes") } } ?: EmptyCard("Sua próxima sessão", "Adicione títulos à lista e deixe a sorte escolher."); Spacer(Modifier.height(18.dp)); Button(onClick = { selected = items.randomOrNull() }) { Text(if (selected == null) "Sortear" else "Sortear novamente") } } }
@Composable private fun DetailsScreen(item: MovieEntity, saved: Boolean, onSave: () -> Unit, onWatched: () -> Unit, onBack: () -> Unit) { LazyColumn(Modifier.fillMaxSize()) { item { TextButton(onClick = onBack) { Text("← Voltar") }; Poster(item.posterPath, Modifier.fillMaxWidth().height(430.dp)); Column(Modifier.padding(22.dp)) { Text(item.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text(item.originalTitle.ifBlank { item.title }, color = MaterialTheme.colorScheme.onSurfaceVariant); Text("${item.releaseDate.take(4)}  •  ★ ${"%.1f".format(item.rating)}  •  ${if (item.type == "movie") "Filme" else "Série"}", Modifier.padding(vertical = 10.dp)); Text(item.overview.ifBlank { "Sinopse indisponível." }); Spacer(Modifier.height(20.dp)); Button(onClick = onSave, Modifier.fillMaxWidth()) { Text(if (saved) "✓ Na minha lista" else "Adicionar à minha lista") }; if (saved) OutlinedButton(onClick = onWatched, Modifier.fillMaxWidth()) { Text(if (item.watched) "Marcar como não assistido" else "Marcar como assistido") } } } } }
@Composable private fun SimplePage(title: String, body: String, onBack: (() -> Unit)? = null) { Column(Modifier.fillMaxSize().padding(24.dp)) { onBack?.let { TextButton(onClick = it) { Text("← Voltar") } }; Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(16.dp)); Text(body) } }

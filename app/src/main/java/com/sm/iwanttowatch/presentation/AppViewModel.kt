package com.sm.iwanttowatch.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.iwanttowatch.data.MovieRepository
import com.sm.iwanttowatch.data.local.MovieEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(val watchlist: List<MovieEntity> = emptyList(), val trending: List<MovieEntity> = emptyList(), val topRated: List<MovieEntity> = emptyList(), val loading: Boolean = false, val error: String? = null)
class AppViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    private val _results = MutableStateFlow<List<MovieEntity>>(emptyList())
    val results: StateFlow<List<MovieEntity>> = _results.asStateFlow()
    private val catalog = mutableMapOf<String, MovieEntity>()
    fun findById(id: String): MovieEntity? = catalog[id]
    init {
        viewModelScope.launch { repository.watchlist.collect { items -> _state.update { it.copy(watchlist = items) } } }
        refresh()
    }
    fun refresh() = viewModelScope.launch {
        _state.update { it.copy(loading = true, error = null) }
        runCatching { repository.trending() to repository.topRated() }
            .onSuccess { (trending, top) -> catalog.putAll((trending + top).associateBy { it.id }); _state.update { it.copy(trending = trending, topRated = top, loading = false) } }
            .onFailure { _state.update { it.copy(loading = false, error = "Não foi possível carregar o catálogo.") } }
    }
    fun search(query: String) = viewModelScope.launch { _results.value = runCatching { repository.search(query) }.getOrDefault(emptyList()).also { catalog.putAll(it.associateBy { movie -> movie.id }) } }
    fun toggle(item: MovieEntity) = viewModelScope.launch { repository.toggle(item) }
    fun watched(item: MovieEntity, watched: Boolean) = viewModelScope.launch { repository.setWatched(item.id, watched) }
}

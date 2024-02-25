package com.urbn.android.flickster.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.urbn.android.flickster.R
import com.urbn.android.flickster.data.Character
import com.urbn.android.flickster.domain.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {
    private var dataFetched = false

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    var CHARACTERSLIST: MutableList<Character?> = ArrayList()

    private val _currentSortingMethod = MutableLiveData<Int>()
    var currentSortingMethod: MutableLiveData<Int> = _currentSortingMethod

    val getAllCharacters = repository.getAllCharactersStream()

    fun getSnackbar(view: View): Snackbar {
        val snackbar = Snackbar.make(view, "Network not detected", Snackbar.LENGTH_INDEFINITE)
            .setAction("Retry") {
                dataFetched = false
                retryFetchingData()
                _errorMessage.postValue(null)
            }
        return snackbar
    }

    fun updateSortingMethod(sortingMethod: Int) {
        _currentSortingMethod.postValue(sortingMethod)
    }

    fun updateSortedCharacters(
        sortingMethod: Int,
    ): List<Character?> {
        return when (sortingMethod) {
            R.id.sort_alphabetically -> CHARACTERSLIST.sortedBy { it?.name }
            R.id.sort_reverse -> CHARACTERSLIST.sortedByDescending { it?.name }
            R.id.sort_favorite -> CHARACTERSLIST.filter { it?.isFavorite ?: false }
            else -> CHARACTERSLIST
        }
    }

    suspend fun saveCharacters(characters: List<Character>) {
        repository.insertAll(characters)
    }

    fun updateCharacterFavoriteStatus(character: Character) {
        viewModelScope.launch {
            repository.updateCharacterFavoriteStatus(character)
        }
    }

    suspend fun isDatabaseEmpty(): Boolean {
        return repository.isDatabaseEmpty()
    }

    suspend fun fetchCharacterData() {
        if (!dataFetched) {
            dataFetched = true
            viewModelScope.launch {
                try {
                    val response = repository.getCharacters()
                    if (response.isSuccessful) {
                        if (isDatabaseEmpty()) {
                            response.body()?.characters?.let { characters ->
                                saveCharacters(characters)
                            }
                        }
                        _currentSortingMethod.postValue(R.id.sort_alphabetically)
                    } else {
                        _errorMessage.postValue("Error fetching data")
                    }
                } catch (e: Exception) {
                    _errorMessage.postValue(e.message)
                }
            }
        }
    }

    private fun retryFetchingData() {
        viewModelScope.launch {
            fetchCharacterData()
        }
    }
}
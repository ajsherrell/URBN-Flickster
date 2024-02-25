package com.urbn.android.flickster.domain

import androidx.lifecycle.LiveData
import com.urbn.android.flickster.data.Character
import com.urbn.android.flickster.data.FlicksterResponse
import retrofit2.Response

interface CharacterRepository {

    suspend fun getCharacters() : Response<FlicksterResponse>

    fun getAllCharactersStream(): LiveData<List<Character>>

    suspend fun insertAll(characters: List<Character>)

    suspend fun update(character: Character)

    suspend fun delete(character: Character)

    suspend fun isDatabaseEmpty(): Boolean

    suspend fun updateCharacterFavoriteStatus(character: Character)

    fun getFavoriteCharactersStream(): LiveData<List<Character>>
}
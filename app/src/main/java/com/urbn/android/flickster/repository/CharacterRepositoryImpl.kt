package com.urbn.android.flickster.repository

import androidx.lifecycle.LiveData
import com.urbn.android.flickster.data.Character
import com.urbn.android.flickster.data.api.DuckApi
import com.urbn.android.flickster.domain.CharacterRepository
import com.urbn.android.flickster.data.FlicksterResponse
import com.urbn.android.flickster.data.room.CharacterDao
import retrofit2.Response
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: DuckApi,
    private val characterDao: CharacterDao
): CharacterRepository {

    override suspend fun getCharacters(): Response<FlicksterResponse> {
        return api.getCharacters()
    }

    override fun getAllCharactersStream(): LiveData<List<Character>> =
        characterDao.getAllCharacters()

    override suspend fun insertAll(characters: List<Character>) = characterDao.insertAll(characters)

    override suspend fun update(character: Character) = characterDao.update(character)

    override suspend fun delete(character: Character) = characterDao.delete(character)

    override suspend fun isDatabaseEmpty(): Boolean {
        return characterDao.getCharacterCount() == 0
    }

    override suspend fun updateCharacterFavoriteStatus(character: Character) {
        characterDao.updateCharacterFavoriteStatus(character.id, character.isFavorite)
    }

    override fun getFavoriteCharactersStream(): LiveData<List<Character>> =
        characterDao.getFavoriteCharacters()
}
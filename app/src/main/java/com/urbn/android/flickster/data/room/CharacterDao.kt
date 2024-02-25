package com.urbn.android.flickster.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.urbn.android.flickster.data.Character

/*
* These unused methods are kept for future use.
* For example, if the app is extended to include a feature to add new characters.
* They are not used in the current implementation.
* */

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<Character>)

    @Update
    suspend fun update(character: Character)

    @Delete
    suspend fun delete(character: Character)

    @Query("SELECT * FROM characters")
    fun getAllCharacters(): LiveData<List<Character>>

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun getCharacterCount(): Int

    @Query("UPDATE characters SET isFavorite = :isFavorite WHERE id = :characterId")
    suspend fun updateCharacterFavoriteStatus(characterId: Int, isFavorite: Boolean)

    @Query("SELECT * FROM characters WHERE isFavorite = 1")
    fun getFavoriteCharacters(): LiveData<List<Character>>
}
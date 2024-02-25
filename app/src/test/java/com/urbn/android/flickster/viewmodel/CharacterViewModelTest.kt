package com.urbn.android.flickster.viewmodel

import org.junit.Assert.*
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.urbn.android.flickster.data.Character
import com.urbn.android.flickster.data.FlicksterResponse
import com.urbn.android.flickster.domain.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CharacterViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: CharacterRepository

    private lateinit var viewModel: CharacterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = CharacterViewModel(repository)
    }

    @Test
    fun `fetchCharacterData success`() = runBlocking {
        // Arrange
        val charactersResponse = mock(FlicksterResponse::class.java)
        `when`(repository.getCharacters()).thenReturn(Response.success(charactersResponse))

        // Act
        viewModel.fetchCharacterData()
        delay(100)

        // Assert
        assertNotNull(viewModel.CHARACTERSLIST)
    }

    @Test
    fun `fetchCharacterData failure`() = runBlocking {
        // Arrange
        `when`(repository.getCharacters()).thenReturn(
            Response.error(
                404,
                ResponseBody.create("application/json".toMediaTypeOrNull(), "")
            )
        )

        // Act
        viewModel.fetchCharacterData()

        // Assert
        assertEquals("Error fetching data", viewModel.errorMessage.value)
    }

    @Test
    fun retryFetchingData() = runBlocking {
        // Arrange
        val charactersResponse = mock(FlicksterResponse::class.java)
        `when`(repository.getCharacters()).thenReturn(Response.success(charactersResponse))

        // Act
        viewModel.retryFetchingData()
        delay(100)

        // Assert
        assertNotNull(viewModel.CHARACTERSLIST)
    }

    @Test
    fun updateSortingMethod() {
        // Arrange
        val sortingMethod = 1

        // Act
        viewModel.updateSortingMethod(sortingMethod)

        // Assert
        assertEquals(sortingMethod, viewModel.currentSortingMethod.value)
    }

    @Test
    fun saveCharacters() = runBlocking {
        // Arrange
        val characters = listOf(mock(Character::class.java))

        // Act
        viewModel.saveCharacters(characters)

        // Assert
        verify(repository).insertAll(characters)
    }

    @Test
    fun updateCharacterFavoriteStatus() = runBlocking {
        // Arrange
        val character = mock(Character::class.java)

        // Act
        viewModel.updateCharacterFavoriteStatus(character)

        // Assert
        verify(repository).updateCharacterFavoriteStatus(character)
    }
}

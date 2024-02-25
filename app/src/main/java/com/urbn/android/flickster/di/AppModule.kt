package com.urbn.android.flickster.di

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.urbn.android.flickster.data.REQUEST_URL
import com.urbn.android.flickster.data.api.DuckApi
import com.urbn.android.flickster.data.room.AppDatabase
import com.urbn.android.flickster.data.room.CharacterDao
import com.urbn.android.flickster.domain.CharacterRepository
import com.urbn.android.flickster.repository.CharacterRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDuckApi(): DuckApi {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .baseUrl(REQUEST_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(DuckApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return AppDatabase.getInstance(app)
    }

    @Provides
    @Singleton
    fun provideCharacterDao(db: AppDatabase): CharacterDao {
        return db.characterDao()
    }

    @Provides
    @Singleton
    fun provideCharacterRepository(api: DuckApi, dao: CharacterDao): CharacterRepository {
        return CharacterRepositoryImpl(api, dao)
    }
}
package com.urbn.android.flickster.data.api

import com.urbn.android.flickster.data.FlicksterResponse
import retrofit2.Response
import retrofit2.http.GET

interface DuckApi {

    @GET("/?q=the+wire+characters&format=json")
    suspend fun getCharacters() : Response<FlicksterResponse>
}
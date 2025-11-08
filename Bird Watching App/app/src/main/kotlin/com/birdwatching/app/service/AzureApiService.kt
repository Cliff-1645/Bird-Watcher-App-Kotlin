package com.birdwatching.app.service

import com.birdwatching.app.data.entity.Bird
import com.birdwatching.app.data.entity.Trip
import retrofit2.http.GET
import retrofit2.http.Query

interface AzureApiService {
    @GET("api/trips")
    suspend fun getTrips(): List<Trip>

    @GET("api/birds")
    suspend fun getBirds(@Query("species") species: String? = null): List<Bird>

    @GET("api/birds/search")
    suspend fun searchBirds(@Query("query") query: String): List<Bird>
}

// This is a placeholder for REST API integration
// You can implement this if you prefer REST API over direct blob storage
object AzureApiClient {
    // TODO: Implement Retrofit client with your Azure Function App URL
    // const val BASE_URL = "https://your-function-app.azurewebsites.net/"
}


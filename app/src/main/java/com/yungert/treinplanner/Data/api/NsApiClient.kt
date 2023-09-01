package com.yungert.treinplanner.presentation.Data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NSApiClient {
    private const val BASE_URL = "https://gateway.apiportal.ns.nl/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: NSApiService by lazy {
        retrofit.create(NSApiService::class.java)
    }
}

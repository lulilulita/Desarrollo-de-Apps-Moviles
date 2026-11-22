package com.example.desarrolloappmoviles.repository

import com.example.desarrolloappmoviles.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: XanoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.XANO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XanoApiService::class.java)
    }
}

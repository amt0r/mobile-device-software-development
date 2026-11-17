package com.example.emergencypower.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var tokenProvider: (() -> String?)? = null

    fun setTokenProvider(provider: () -> String?) {
        tokenProvider = provider
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor { tokenProvider?.invoke() })
            .addInterceptor(MockScadaInterceptor())
            .build()
    }

    val apiService: EmergencyApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://scada.local/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EmergencyApiService::class.java)
    }
}

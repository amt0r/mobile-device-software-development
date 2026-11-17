package com.example.emergencypower.data.remote

import com.example.emergencypower.data.remote.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface EmergencyApiService {

    @GET("api/v1/grid/status")
    suspend fun getGridStatus(
        @Query("force_error") forceError: Int? = null
    ): Response<GridStatusDto>

    @GET("api/v1/telemetry/forecast")
    suspend fun getLoadForecast(
        @Query("hours") hours: Int = 6,
        @Query("force_error") forceError: Int? = null
    ): Response<LoadForecastResponseDto>

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>

    @POST("api/v1/generator/control")
    suspend fun executeGeneratorControl(
        @Body request: GeneratorControlDto
    ): Response<ApiResponseDto>
}

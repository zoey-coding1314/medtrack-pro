package com.elizabeth.s36639095.medtrack.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("drug/label.json")
    suspend fun getDrugInfo(@Query("search") search: String, @Query("limit") limit: Int = 1): DrugResponse

    companion object {
        var BASE_URL = "https://api.fda.gov/"

        fun create(): APIService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }
    }
}
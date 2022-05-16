package com.c22_pc383.wacayang.network

import com.c22_pc383.wacayang.BuildConfig
import com.c22_pc383.wacayang.data.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("wayangs")
    //@Headers("Authorization: token ${BuildConfig.BASE_TOKEN}")
    suspend fun getWayangs(@Query("page") page: String): Response<ListWayangResponse>

    @GET("search")
    //@Headers("Authorization: token ${BuildConfig.BASE_TOKEN}")
    suspend fun findWayang(@Query("name") query: String): Response<SearchWayangResponse>

    @GET("wayangs/{id}")
    //@Headers("Authorization: token ${BuildConfig.BASE_TOKEN}")
    suspend fun getWayangDetail(@Path("id") query: String): Response<DetailWayangResponse>
}
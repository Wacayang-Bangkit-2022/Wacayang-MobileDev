package com.c22_pc383.wacayang.repository

import com.c22_pc383.wacayang.network.ApiConfig
import com.c22_pc383.wacayang.network.ApiService
import okhttp3.MultipartBody

class WayangRepository(private val apiService: ApiService) {
    suspend fun getWayangs(token: String, page: String) = apiService.getWayangs(token, page)
    suspend fun findWayang(token: String, query: String) = apiService.findWayang(token, query)
    suspend fun getWayangDetail(token: String, query: String) = apiService.getWayangDetail(token, query)
    suspend fun predictWayang(token: String, file: MultipartBody.Part) = apiService.predictWayang(token, file)

    suspend fun addFavorite(token: String, itemId: Int) = apiService.addFavorite(token, itemId)
    suspend fun delFavorite(token: String, itemId: Int) = apiService.delFavorite(token, itemId)
    suspend fun getFavorites(token: String, query: String?) = apiService.getFavorites(token, query)

    companion object {
        @Volatile
        private var INSTANCE: WayangRepository? = null

        @Volatile
        private var AI_INSTANCE: WayangRepository? = null

        @JvmStatic
        fun getDefaultRepository(): WayangRepository {
            if (INSTANCE == null) INSTANCE = WayangRepository(ApiConfig.getApiService())
            return INSTANCE as WayangRepository
        }

        @JvmStatic
        fun getAIRepository(): WayangRepository {
            if (AI_INSTANCE == null) AI_INSTANCE = WayangRepository(ApiConfig.getAIApiService())
            return AI_INSTANCE as WayangRepository
        }
    }
}
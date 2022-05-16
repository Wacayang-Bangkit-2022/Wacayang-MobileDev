package com.c22_pc383.wacayang.repository

import com.c22_pc383.wacayang.data.FavoriteDatabase
import com.c22_pc383.wacayang.network.ApiConfig
import com.c22_pc383.wacayang.network.ApiService

class WayangRepository(private val apiService: ApiService) {
    suspend fun getWayangs(page: String) = apiService.getWayangs(page)
    suspend fun findWayang(query: String) = apiService.findWayang(query)
    suspend fun getWayangDetail(query: String) = apiService.getWayangDetail(query)

    companion object {
        @Volatile
        private var INSTANCE: WayangRepository? = null

        @JvmStatic
        fun getRepository(): WayangRepository {
            if (INSTANCE == null) {
                synchronized(FavoriteDatabase::class.java) {
                    INSTANCE = WayangRepository(ApiConfig.getApiService())
                }
            }
            return INSTANCE as WayangRepository
        }
    }
}
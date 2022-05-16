package com.c22_pc383.wacayang.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SupportSQLiteQuery
import com.c22_pc383.wacayang.data.FavoriteDao
import com.c22_pc383.wacayang.data.FavoriteDatabase
import com.c22_pc383.wacayang.data.Wayang
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository(application: Application) {
    private val _favDao: FavoriteDao = FavoriteDatabase.getDatabase(application).favoriteDao()
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    fun insertFavorite(item: Wayang) = executorService.execute { _favDao.insertFavorite(item) }
    fun deleteFavorite(item: Wayang) = executorService.execute { _favDao.deleteFavorite(item) }

    fun getFavorites(query: SupportSQLiteQuery): LiveData<List<Wayang>> = _favDao.getFavorites(query)
    fun isAFavorite(id: Int): LiveData<Int> = _favDao.isAFavorite(id)
}
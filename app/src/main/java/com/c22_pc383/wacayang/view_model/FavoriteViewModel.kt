package com.c22_pc383.wacayang.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.c22_pc383.wacayang.data.Wayang
import com.c22_pc383.wacayang.repository.FavoriteRepository

class FavoriteViewModel(app: Application): ViewModel() {
    private val _favRepo = FavoriteRepository(app)

    fun insertFavorite(item: Wayang) = _favRepo.insertFavorite(item)
    fun deleteFavorite(item: Wayang) = _favRepo.deleteFavorite(item)

    fun getFavorites(query: SupportSQLiteQuery): LiveData<List<Wayang>> = _favRepo.getFavorites(query)
    fun isAFavorite(id: Int): LiveData<Int> = _favRepo.isAFavorite(id)

    companion object {
        fun getSortQuery(keyword: String, sortType: SortType): SimpleSQLiteQuery {
            val query = StringBuilder().append("SELECT * FROM ${Wayang.FAV_TABLE}")
            if (keyword.isNotEmpty()) query.append(" WHERE name LIKE '%$keyword%'")
            when (sortType) {
                SortType.DEFAULT -> {}
                SortType.ASCENDING -> query.append(" ORDER BY name ASC")
                SortType.DESCENDING -> query.append(" ORDER BY name DESC")
            }

            return SimpleSQLiteQuery(query.toString())
        }

        enum class SortType {
            DEFAULT,
            ASCENDING,
            DESCENDING
        }
    }
}
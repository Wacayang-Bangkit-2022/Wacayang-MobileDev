package com.c22_pc383.wacayang.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavorite(item: Wayang)

    @Delete
    fun deleteFavorite(item: Wayang)

    @Query("SELECT COUNT (id) from ${Wayang.FAV_TABLE} WHERE id = :id")
    fun isAFavorite(id: Int): LiveData<Int>

    @RawQuery(observedEntities = [Wayang::class])
    fun getFavorites(query: SupportSQLiteQuery): LiveData<List<Wayang>>
}
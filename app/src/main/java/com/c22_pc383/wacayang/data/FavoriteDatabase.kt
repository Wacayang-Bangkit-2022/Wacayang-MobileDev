package com.c22_pc383.wacayang.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Wayang::class], version = 1, exportSchema = false)
abstract class FavoriteDatabase: RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteDatabase? = null
        private const val DB_NAME = "favorite_db"

        @JvmStatic
        fun getDatabase(context: Context): FavoriteDatabase {
            if (INSTANCE == null) {
                synchronized(FavoriteDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        FavoriteDatabase::class.java, DB_NAME).build()
                }
            }
            return INSTANCE as FavoriteDatabase
        }
    }
}
package com.c22_pc383.wacayang.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c22_pc383.wacayang.view_model.FavoriteViewModel

class FavoriteViewModelFactory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java))
            return FavoriteViewModel(app) as T
        throw IllegalArgumentException("${modelClass.name} is not supported by this factory")
    }
}
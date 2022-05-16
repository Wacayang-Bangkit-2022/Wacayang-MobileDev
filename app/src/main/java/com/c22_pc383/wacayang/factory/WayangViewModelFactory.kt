package com.c22_pc383.wacayang.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c22_pc383.wacayang.repository.WayangRepository
import com.c22_pc383.wacayang.view_model.WayangViewModel

class WayangViewModelFactory(private val repository: WayangRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WayangViewModel::class.java))
            return WayangViewModel(repository) as T
        throw IllegalArgumentException("${modelClass.name} is not supported by this factory")
    }
}
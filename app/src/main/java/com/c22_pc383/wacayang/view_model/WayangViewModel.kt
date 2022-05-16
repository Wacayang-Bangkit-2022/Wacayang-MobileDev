package com.c22_pc383.wacayang.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c22_pc383.wacayang.data.UserDetailsResponse
import com.c22_pc383.wacayang.data.UserResponse
import com.c22_pc383.wacayang.data.Wayang
import com.c22_pc383.wacayang.repository.WayangRepository
import kotlinx.coroutines.launch

class WayangViewModel(private val repository: WayangRepository): ViewModel() {
    // region Get Wayang List
    private var _listWayang = MutableLiveData<List<Wayang>>()
    val listWayang: LiveData<List<Wayang>> = _listWayang

    private var _isGetWayangError = MutableLiveData<Boolean>()
    val isGetWayangError: LiveData<Boolean> = _isGetWayangError
    // endregion

    // region Find Wayang
    private var _foundWayang = MutableLiveData<List<Wayang>>()
    val foundWayang: LiveData<List<Wayang>> = _foundWayang

    private var _isFindWayangError = MutableLiveData<Boolean>()
    val isFindWayangError: LiveData<Boolean> = _isFindWayangError
    // endregion

    // region Get Wayang Detail
    private var _detailWayang = MutableLiveData<Wayang>()
    val detailWayang: LiveData<Wayang> = _detailWayang

    private var _isGetDetailWayangError = MutableLiveData<Boolean>()
    val isGetDetailWayangError: LiveData<Boolean> = _isGetDetailWayangError
    // endregion

    fun getWayangs(page: Int) = viewModelScope.launch {
        try {
            val response = repository.getWayangs(page.toString())
            if (response.isSuccessful) {
                _listWayang.postValue(response.body()!!.listWayang)
            }
            _isGetWayangError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isGetWayangError.postValue(true)
            e.printStackTrace()
        }
    }

    fun findWayang(query: String, viewAll: Boolean = false) = viewModelScope.launch {
        try {
            val temp = if (viewAll) {
                val response = repository.getWayangs("0")
                if (response.isSuccessful) {
                    _foundWayang.postValue(response.body()!!.listWayang)
                }
                response.isSuccessful
            } else {
                val response = repository.findWayang(query)
                if (response.isSuccessful) {
                    _foundWayang.postValue(response.body()!!.wayangFound)
                }
                response.isSuccessful
            }

            _isFindWayangError.postValue(!temp)
        } catch (e: Exception) {
            _isFindWayangError.postValue(true)
            e.printStackTrace()
        }
    }

    fun getWayangDetail(query: String) = viewModelScope.launch {
        try {
            val response = repository.getWayangDetail(query)
            if (response.isSuccessful) {
                _detailWayang.postValue(response.body()!!.wayang)
            }
            _isGetDetailWayangError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isGetDetailWayangError.postValue(true)
            e.printStackTrace()
        }
    }

    init { getWayangs(0) }
}
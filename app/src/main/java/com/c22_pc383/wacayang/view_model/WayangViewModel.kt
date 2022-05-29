package com.c22_pc383.wacayang.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c22_pc383.wacayang.data.Comment
import com.c22_pc383.wacayang.data.Wayang
import com.c22_pc383.wacayang.data.WayangPredictResponse
import com.c22_pc383.wacayang.helper.Utils
import com.c22_pc383.wacayang.repository.WayangRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class WayangViewModel(private val repository: WayangRepository): ViewModel() {
    // region Sign User
    private var _isSignUserError = MutableLiveData<Boolean>()
    val isSignUserError: LiveData<Boolean> = _isSignUserError
    // endregion

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

    // region Predict Wayang
    private var _predictedWayang = MutableLiveData<WayangPredictResponse>()
    val predictedWayang: LiveData<WayangPredictResponse> = _predictedWayang

    private var _isPredictWayangError = MutableLiveData<Boolean>()
    val isPredictWayangError: LiveData<Boolean> = _isPredictWayangError
    // endregion

    // region Favorites
    private var _listFav = MutableLiveData<List<Wayang>>()
    val listFav: LiveData<List<Wayang>> = _listFav

    private var _isGetFavError = MutableLiveData<Boolean>()
    val isGetFavError: LiveData<Boolean> = _isGetFavError

    private var _isAddFavError = MutableLiveData<Boolean>()
    val isAddFavError: LiveData<Boolean> = _isAddFavError

    private var _isDelFavError = MutableLiveData<Boolean>()
    val isDelFavError: LiveData<Boolean> = _isDelFavError
    // endregion

    // region Comments
    private var _listComment = MutableLiveData<List<Comment>>()
    val listComment: LiveData<List<Comment>> = _listComment

    private var _isGetCommentError = MutableLiveData<Boolean>()
    val isGetCommentError: LiveData<Boolean> = _isGetCommentError

    private var _isAddCommentError = MutableLiveData<Boolean>()
    val isAddCommentError: LiveData<Boolean> = _isAddCommentError

    private var _isDelCommentError = MutableLiveData<Boolean>()
    val isDelCommentError: LiveData<Boolean> = _isDelCommentError
    // endregion

    fun signUser(token: String) = viewModelScope.launch {
        try {
            val response = repository.signUser(Utils.formatBearerToken(token))
            _isSignUserError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isSignUserError.postValue(true)
            e.printStackTrace()
        }
    }

    fun getWayangs(token: String, page: Int) = viewModelScope.launch {
        try {
            val response = repository.getWayangs(Utils.formatBearerToken(token), page.toString())
            if (response.isSuccessful) {
                _listWayang.postValue(response.body()!!.listWayang)
            }
            _isGetWayangError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isGetWayangError.postValue(true)
            e.printStackTrace()
        }
    }

    fun findWayang(token: String, query: String, viewAll: Boolean = false) = viewModelScope.launch {
        try {
            val temp = if (viewAll) {
                val response = repository.getWayangs(Utils.formatBearerToken(token), "0")
                if (response.isSuccessful) {
                    _foundWayang.postValue(response.body()!!.listWayang)
                }
                response.isSuccessful
            } else {
                val response = repository.findWayang(Utils.formatBearerToken(token), query)
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

    fun getWayangDetail(token: String, query: String) = viewModelScope.launch {
        try {
            val response = repository.getWayangDetail(Utils.formatBearerToken(token), query)
            if (response.isSuccessful) {
                _detailWayang.postValue(response.body()!!.wayang)
            }
            _isGetDetailWayangError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isGetDetailWayangError.postValue(true)
            e.printStackTrace()
        }
    }

    fun predictWayang(token: String, file: MultipartBody.Part) = viewModelScope.launch {
        try {
            val response = repository.predictWayang(Utils.formatBearerToken(token), file)
            if (response.isSuccessful) {
                _predictedWayang.postValue(response.body()!!)
            }
            _isPredictWayangError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isPredictWayangError.postValue(true)
            e.printStackTrace()
        }
    }

    fun getFavWayangs(token: String, query: String? = null) = viewModelScope.launch {
        try {
            val response = repository.getFavorites(Utils.formatBearerToken(token), query)
            if (response.isSuccessful) {
                _listFav.postValue(response.body()!!.favorite)
            }
            _isGetFavError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isGetFavError.postValue(true)
            e.printStackTrace()
        }
    }

    fun addFavorite(token: String, itemId: Int) = viewModelScope.launch {
        try {
            val response = repository.addFavorite(Utils.formatBearerToken(token), itemId)
            _isAddFavError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isAddFavError.postValue(true)
            e.printStackTrace()
        }
    }

    fun delFavorite(token: String, itemId: Int) = viewModelScope.launch {
        try {
            val response = repository.delFavorite(Utils.formatBearerToken(token), itemId)
            _isDelFavError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isDelFavError.postValue(true)
            e.printStackTrace()
        }
    }

    fun getComments(token: String, itemId: Int) = viewModelScope.launch {
        try {
            val response = repository.getComments(Utils.formatBearerToken(token), itemId)
            if (response.isSuccessful) {
                _listComment.postValue(response.body()!!.comments)
            }
            _isGetCommentError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isGetCommentError.postValue(true)
            e.printStackTrace()
        }
    }

    fun addComment(token: String, itemId: Int, content: String) = viewModelScope.launch {
        try {
            val response = repository.addComment(Utils.formatBearerToken(token), itemId, content)
            _isAddCommentError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isAddCommentError.postValue(true)
            e.printStackTrace()
        }
    }

    fun delComment(token: String, itemId: Int) = viewModelScope.launch {
        try {
            val response = repository.delComment(Utils.formatBearerToken(token), itemId)
            _isDelCommentError.postValue(!response.isSuccessful)
        } catch (e: Exception) {
            _isDelCommentError.postValue(true)
            e.printStackTrace()
        }
    }
}
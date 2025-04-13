package com.quangduy.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.quangduy.chatapp.ultils.Logger
import com.quangduy.chatapp.ultils.NetworkHelper
import com.quangduy.chatapp.ultils.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


abstract class BaseViewModel : ViewModel() {
    protected var parentJob: Job? = null

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.logE(throwable.message.toString())
        postError(throwable.message ?: "Unknown error")
    }

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    protected fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }

    protected fun postError(message: String) {
        _errorMessage.postValue(message)
    }

    protected fun <T> safeFirebaseCall(
        liveData: MutableLiveData<Resource<T>>,
        task: () -> Task<T>
    ) {
        liveData.postValue(Resource.loading(null))
        task().addOnSuccessListener {
            liveData.postValue(Resource.success(it))
        }.addOnFailureListener {
            liveData.postValue(Resource.error(it.localizedMessage ?: "Unknown error", null))
        }
    }


    inline fun <T> ViewModel.safeApiCall(
        liveData: MutableLiveData<Resource<T>>,
        networkHelper: NetworkHelper,
        crossinline apiCall: suspend () -> Response<T>
    ) {
        viewModelScope.launch {
            liveData.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                try {
                    val response = apiCall()
                    if (response.isSuccessful) {
                        liveData.postValue(Resource.success(response.body()))
                    } else {
                        liveData.postValue(Resource.error(response.message().toString(), null))
                    }
                } catch (e: Exception) {
                    liveData.postValue(Resource.error(e.localizedMessage ?: "Unknown error", null))
                }
            } else {
                liveData.postValue(Resource.error("No internet connection", null))
            }
        }
    }


    protected fun <T> performAction(
        resultLiveData: MutableLiveData<Resource<T>>,
        apiCall: () -> Call<T>
    ) {
        resultLiveData.value = Resource.loading(null)
        val call = apiCall()
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    responseData?.let {
                        resultLiveData.value = Resource.success(it)
                    }
                } else {
                    resultLiveData.value = Resource.error("Request failed", null)
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                resultLiveData.value = Resource.error(t.message.toString(), null)
            }
        })
    }
}
package ru.educationalwork.developerslifegifs.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.educationalwork.developerslifegifs.App
import ru.educationalwork.developerslifegifs.domain.GifInteractor
import ru.educationalwork.developerslifegifs.repository.model.DbGifItem

class GifViewModel : ViewModel() {
    private val gifLiveData = MutableLiveData<DbGifItem>()
    private val errorLiveData = MutableLiveData<String>()
    private val isLoadingLiveData = MutableLiveData<Boolean>()
    private val isLastLiveData = MutableLiveData<Boolean>()
    private val backStackCounterLiveData = MutableLiveData<Int>()

    private val gifInteractor = App.instance?.gifInteractor

    val gif : LiveData<DbGifItem>
        get() = gifLiveData

    val error : LiveData<String>
        get() = errorLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    val isLast: LiveData<Boolean>
        get() = isLastLiveData

    val backStackCounter: LiveData<Int>
        get() = backStackCounterLiveData


    fun getGif(category: String, action: String, saveCounter: Int){
        gifInteractor?.getGif(category, action, saveCounter, object : GifInteractor.GetGifCallback{
            override fun isLoading(load: Boolean) {
                isLoadingLiveData.postValue(load)
            }

            override fun onSuccess(gif: DbGifItem?) {
                gifLiveData.postValue(gif)
            }

            override fun onError(error: String) {
                errorLiveData.postValue(error)
            }

            override fun isLast(isLast: Boolean) {
                isLastLiveData.postValue(isLast)
            }

            override fun backStackCounter(counter: Int) {
                backStackCounterLiveData.postValue(counter)
            }
        })
    }

}
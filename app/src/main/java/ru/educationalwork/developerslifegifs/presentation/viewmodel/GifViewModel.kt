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

    private val gifInteractor = App.instance?.gifInteractor

    val gif : LiveData<DbGifItem>
        get() = gifLiveData

    val error : LiveData<String>
        get() = errorLiveData

    fun getGif(category: String, page: String, itemCounter: Int){
        gifInteractor?.getGif(category, page, itemCounter, object : GifInteractor.GetGifCallback{
            override fun onSuccess(gif: DbGifItem?) {
                gifLiveData.postValue(gif)
            }

            override fun onError(error: String) {
                errorLiveData.postValue(error)
            }
        })
    }

}
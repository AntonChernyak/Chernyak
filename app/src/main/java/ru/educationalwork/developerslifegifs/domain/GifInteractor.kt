package ru.educationalwork.developerslifegifs.domain

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.educationalwork.developerslifegifs.presentation.view.MainActivity.Companion.CATEGORY_RANDOM
import ru.educationalwork.developerslifegifs.repository.database.GifRepositoryInterface
import ru.educationalwork.developerslifegifs.repository.model.ApiResponse
import ru.educationalwork.developerslifegifs.repository.model.DbGifItem
import ru.educationalwork.developerslifegifs.repository.model.GifItemResponse
import ru.educationalwork.developerslifegifs.repository.service.ApiService
import java.util.concurrent.Executors

class GifInteractor(
    private val apiService: ApiService,
    private val repository: GifRepositoryInterface
) {

    fun getGifFromNet(category: String, page: String, itemCounter: Int, netCallback: GetGifNetCallback) {
        netCallback.isLoading(true)
      //  Log.d("TAGGGG", "$category, $page, $itemCounter")
        if (category == CATEGORY_RANDOM) {
            getRandomPostFromNet(netCallback)
        } else getSpecialPostFromNet(category, page, itemCounter, netCallback)
    }

    private fun getRandomPostFromNet(netCallback: GetGifNetCallback){
        apiService.getRandomPost().enqueue(object : Callback<GifItemResponse>{
            override fun onFailure(call: Call<GifItemResponse>, t: Throwable) {
                netCallback.onError(t.message.toString())
            }

            override fun onResponse(
                call: Call<GifItemResponse>,
                response: Response<GifItemResponse>
            ) {
                if (response.isSuccessful && response.body()?.gifURL != null ){
                    val dbGif = DbGifItem(description = response.body()!!.description, url = response.body()!!.gifURL)
                    Executors.newSingleThreadExecutor().execute {
                        repository.addGifToDb(dbGif)
                    }
                    netCallback.onSuccess(dbGif)
                } else {
                    netCallback.onError(response.code().toString())
                }
            }
        })
    }

    private fun getSpecialPostFromNet(category: String, page: String, itemCounter: Int, netCallback: GetGifNetCallback) {
        apiService.getSpecialPosts(category, page).enqueue(object : Callback<ApiResponse>{
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                netCallback.onError(t.message.toString())
            }

            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                if (response.isSuccessful  && response.body() != null ) {
                    val gif = response.body()?.items?.get(itemCounter)
                    val dbGif = DbGifItem(description = gif!!.description, url = gif.gifURL)
                    Executors.newSingleThreadExecutor().execute {
                        repository.addGifToDb(dbGif)
                    }
                    netCallback.onSuccess(dbGif)
                } else {
                    netCallback.onError(response.code().toString())
                }
            }

        })
    }

    fun getGifFromDb(dbCounter: Int, dbCallback: GetGifDbCallback){
        dbCallback.isLoading(true)
        Executors.newSingleThreadExecutor().execute {
            val gifList = repository.getAllGifs()
            if (gifList.size == dbCounter) dbCallback.isLast(true)
            else {
                Log.d("TAGGG", "${gifList[gifList.size - 1 - dbCounter]}")
                dbCallback.onSuccess(gifList[gifList.size - 1 - dbCounter])
            }
        }
    }

    interface GetGifNetCallback {
        fun isLoading(load: Boolean)
        fun onSuccess(gif: DbGifItem?)
        fun onError(error: String)
    }

    interface GetGifDbCallback{
        fun isLoading(load: Boolean)
        fun onSuccess(gif: DbGifItem?)
        fun isLast(isLast: Boolean)
    }
}
package ru.educationalwork.developerslifegifs.domain

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.educationalwork.developerslifegifs.presentation.view.MainActivity.Companion.ACTION_CHANGE_CATEGORY
import ru.educationalwork.developerslifegifs.presentation.view.MainActivity.Companion.ACTION_NEXT
import ru.educationalwork.developerslifegifs.presentation.view.MainActivity.Companion.ACTION_PREVIOUS
import ru.educationalwork.developerslifegifs.presentation.view.MainActivity.Companion.CATEGORY_RANDOM
import ru.educationalwork.developerslifegifs.repository.database.GifRepositoryInterface
import ru.educationalwork.developerslifegifs.repository.model.ApiResponse
import ru.educationalwork.developerslifegifs.repository.model.DbGifItem
import ru.educationalwork.developerslifegifs.repository.model.GifItemResponse
import ru.educationalwork.developerslifegifs.repository.service.ApiService
import java.util.concurrent.Executors
import kotlin.math.abs

class GifInteractor(
    private val apiService: ApiService,
    private val repository: GifRepositoryInterface
) {

    private var pageCounter: Int = 0
    private var itemCounter: Int = 0
    private var backStackCounter: Int = 0

    fun getGif(category: String, action: String, saveCounter: Int, callback: GetGifCallback){
        Log.d("TAGGG", "action = $action, cat = $category, counter = $backStackCounter")
        if ((action == ACTION_NEXT && backStackCounter == 0)) {
            getGifFromNet(category, callback)
            callback.isLast(false)
        } else if (action == ACTION_CHANGE_CATEGORY){
            getGifFromNet(category, callback)
            backStackCounter = 0
        }
        else getGifFromDb(action, callback)
    }

    private fun getGifFromNet(category: String, callback: GetGifCallback) {
        callback.isLoading(true)
        if (category == CATEGORY_RANDOM) {
            getRandomPostFromNet(callback)
        } else getSpecialPostFromNet(category, callback)
    }

    private fun getRandomPostFromNet(callback: GetGifCallback){
        apiService.getRandomPost().enqueue(object : Callback<GifItemResponse>{
            override fun onFailure(call: Call<GifItemResponse>, t: Throwable) {
                callback.onError("Проверьте подключение к сети")
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
                    callback.onSuccess(dbGif)
                } else {
                    callback.onError(response.code().toString())
                }
            }
        })
    }

    private fun getSpecialPostFromNet(category: String, callback: GetGifCallback) {
        apiService.getSpecialPosts(category, pageCounter.toString()).enqueue(object : Callback<ApiResponse>{
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                callback.onError("Проверьте подключение к сети")
            }

            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                if (response.isSuccessful  && response.body() != null ) {
                   val gifList  = response.body()?.items
                   if (gifList?.size == 0) callback.onError("Нет данных")
                   else {
                        val gif = gifList?.get(itemCounter)
                        val dbGif = DbGifItem(description = gif!!.description, url = gif.gifURL)
                        Executors.newSingleThreadExecutor().execute {
                            repository.addGifToDb(dbGif)
                        }
                        itemCounter++
                        if (itemCounter == gifList.size) {
                            pageCounter++
                            itemCounter = 0
                        }
                        callback.onSuccess(dbGif)
                    }
                } else {
                    callback.onError(response.code().toString())
                }
            }

        })
    }

    private fun getGifFromDb(action: String, callback: GetGifCallback){
        callback.isLoading(true)
        Executors.newSingleThreadExecutor().execute {
            val gifList = repository.getAllGifs()
            if (gifList.size - backStackCounter > 0) {
                if (action == ACTION_NEXT) backStackCounter--
                else if (action == ACTION_PREVIOUS) backStackCounter++
                callback.backStackCounter(backStackCounter)

                if (abs(gifList.size - backStackCounter) == 1) callback.isLast(true)
                else callback.isLast(false)

                Log.d("TAGGGG", "Previous: counter = $backStackCounter, size = ${gifList.size}")
                callback.onSuccess(gifList[gifList.size - 1 - backStackCounter])
            } else callback.onError("Нет данных")
        }
    }

    interface GetGifCallback {
        fun isLoading(load: Boolean)
        fun onSuccess(gif: DbGifItem?)
        fun onError(error: String)
        fun isLast(isLast: Boolean)
        fun backStackCounter(counter: Int)
    }

}
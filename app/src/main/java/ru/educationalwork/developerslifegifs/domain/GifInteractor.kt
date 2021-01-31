package ru.educationalwork.developerslifegifs.domain

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.educationalwork.developerslifegifs.presentation.view.MainActivity.Companion.CATEGORY_RANDOM
import ru.educationalwork.developerslifegifs.repository.database.GifRepositoryInterface
import ru.educationalwork.developerslifegifs.repository.model.DbGifItem
import ru.educationalwork.developerslifegifs.repository.model.GifItemResponse
import ru.educationalwork.developerslifegifs.repository.server.ApiService
import java.util.concurrent.Executors

class GifInteractor(
    private val apiService: ApiService,
    private val repository: GifRepositoryInterface
) {

    fun getGif(category: String, page: String, itemCounter: Int, callback: GetGifCallback) {
        if (category == CATEGORY_RANDOM) {
            getRandomPost(callback)
        } else getSpecialPost(category, page, itemCounter, callback)
    }

    /**
     * Или всё же execute
     */
    private fun getRandomPost(callback: GetGifCallback){
        apiService.getRandomPost().enqueue(object : Callback<GifItemResponse>{
            override fun onFailure(call: Call<GifItemResponse>, t: Throwable) {
                callback.onError(t.message.toString())
            }

            override fun onResponse(
                call: Call<GifItemResponse>,
                response: Response<GifItemResponse>
            ) {
                if (response.isSuccessful){
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

    /**
     * Или всё же execute
     */
    private fun getSpecialPost(category: String, page: String, itemCounter: Int, callback: GetGifCallback) {
        apiService.getSpecialPosts(category, page).enqueue(object : Callback<List<GifItemResponse>>{
            override fun onFailure(call: Call<List<GifItemResponse>>, t: Throwable) {
                callback.onError(t.message.toString())
            }

            override fun onResponse(
                call: Call<List<GifItemResponse>>,
                response: Response<List<GifItemResponse>>
            ) {
                if (response.isSuccessful) {
                    val gif = response.body()?.get(itemCounter)
                    val dbGif = DbGifItem(description = gif!!.description, url = gif.gifURL)
                    repository.addGifToDb(dbGif)
                    callback.onSuccess(dbGif)
                } else {
                    callback.onError(response.code().toString())
                }
            }

        })
    }

    interface GetGifCallback {
        fun onSuccess(gif: DbGifItem?)
        fun onError(error: String)
    }
}
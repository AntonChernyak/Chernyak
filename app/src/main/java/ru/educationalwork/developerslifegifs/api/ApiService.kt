package ru.educationalwork.developerslifegifs.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import ru.educationalwork.developerslifegifs.model.special_model.GifItem
import ru.educationalwork.developerslifegifs.model.random_model.GifItemRandomResponse

interface ApiService {

    @GET("random?json=true")
    fun getRandomPost(): Call<GifItemRandomResponse>

    @GET("{category}/{page_number}/?json=true")
    fun getSpecialPosts(
        @Path("category") category: String,
        @Path("page_number") pageNumber: String
    ): Call<List<GifItem>>

}
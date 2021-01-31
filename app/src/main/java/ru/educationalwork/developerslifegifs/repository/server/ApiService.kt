package ru.educationalwork.developerslifegifs.repository.server

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import ru.educationalwork.developerslifegifs.repository.model.GifItemResponse

interface ApiService {

    @GET("random?json=true")
    fun getRandomPost(): Call<GifItemResponse>

    @GET("{category}/{page_number}?json=true")
    fun getSpecialPosts(
        @Path("category") category: String,
        @Path("page_number") pageNumber: String
    ): Call<List<GifItemResponse>>

}
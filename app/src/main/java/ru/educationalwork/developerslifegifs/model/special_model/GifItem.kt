package ru.educationalwork.developerslifegifs.model.special_model

import com.google.gson.annotations.SerializedName


data class GifItem (
	@SerializedName("guid") val guid : Int,
	@SerializedName("content") val content : String

/*	@SerializedName("title") val title : String,
	@SerializedName("pubDate") val pubDate : String,
	@SerializedName("link") val link : String,
	@SerializedName("author") val author : String,
	@SerializedName("thumbnail") val thumbnail : String,
	@SerializedName("description") val description : String,*/
)
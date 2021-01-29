package ru.educationalwork.developerslifegifs.model.special_model

import com.google.gson.annotations.SerializedName


data class Feed (
	@SerializedName("url") val url : String,
	@SerializedName("title") val title : String,
	@SerializedName("link") val link : String,
	@SerializedName("author") val author : String,
	@SerializedName("description") val description : String,
	@SerializedName("image") val image : String
)
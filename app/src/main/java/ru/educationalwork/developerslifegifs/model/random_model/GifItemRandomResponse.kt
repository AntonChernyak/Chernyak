package ru.educationalwork.developerslifegifs.model.random_model

import com.google.gson.annotations.SerializedName

data class GifItemRandomResponse (
	@SerializedName("id") val id : Int,
	@SerializedName("gifURL") val gifURL : String
/*

	@SerializedName("description") val description : String,
	@SerializedName("votes") val votes : Int,
	@SerializedName("author") val author : String,
	@SerializedName("date") val date : String,
	@SerializedName("gifSize") val gifSize : Int,
	@SerializedName("previewURL") val previewURL : String,
	@SerializedName("videoURL") val videoURL : String,
	@SerializedName("videoPath") val videoPath : String,
	@SerializedName("videoSize") val videoSize : Int,
	@SerializedName("type") val type : String,
	@SerializedName("width") val width : Int,
	@SerializedName("height") val height : Int,
	@SerializedName("commentsCount") val commentsCount : Int,
	@SerializedName("fileSize") val fileSize : Int,
	@SerializedName("canVote") val canVote : Boolean*/
)
package ru.educationalwork.developerslifegifs.model.special_model

import com.google.gson.annotations.SerializedName
import ru.educationalwork.developerslifegifs.model.special_model.Enclosure

data class GifItem (
	@SerializedName("title") val title : String,
	@SerializedName("pubDate") val pubDate : String,
	@SerializedName("link") val link : String,
	@SerializedName("guid") val guid : Int,
	@SerializedName("author") val author : String,
	@SerializedName("thumbnail") val thumbnail : String,
	@SerializedName("description") val description : String,
	@SerializedName("content") val content : String,
	@SerializedName("enclosure") val enclosure : Enclosure,
	@SerializedName("categories") val categories : List<String>
)
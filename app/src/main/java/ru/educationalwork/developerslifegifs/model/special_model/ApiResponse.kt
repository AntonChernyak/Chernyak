package ru.educationalwork.developerslifegifs.model.special_model

import com.google.gson.annotations.SerializedName

data class ApiResponse (

	@SerializedName("status") val status : String,
	@SerializedName("feed") val feed : Feed,
	@SerializedName("items") val items : List<GifItem>
)
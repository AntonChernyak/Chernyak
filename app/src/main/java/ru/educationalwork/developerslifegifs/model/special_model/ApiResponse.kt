package ru.educationalwork.developerslifegifs.model.special_model

import com.google.gson.annotations.SerializedName

data class ApiResponse (
	@SerializedName("items") val items : List<GifItem>
)
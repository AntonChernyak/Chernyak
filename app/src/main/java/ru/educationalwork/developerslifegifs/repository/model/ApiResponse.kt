package ru.educationalwork.developerslifegifs.repository.model

import com.google.gson.annotations.SerializedName

data class ApiResponse (
	@SerializedName("result") val items : List<GifItemResponse>
)
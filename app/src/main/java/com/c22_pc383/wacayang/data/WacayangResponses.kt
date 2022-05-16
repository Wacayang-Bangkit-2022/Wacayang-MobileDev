package com.c22_pc383.wacayang.data

import com.google.gson.annotations.SerializedName

data class ListWayangResponse(
	@field:SerializedName("listWayang")
	val listWayang: List<Wayang>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DetailWayangResponse(
	@field:SerializedName("wayang")
	val wayang: Wayang,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class SearchWayangResponse(
	@field:SerializedName("wayangFound")
	val wayangFound: List<Wayang>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
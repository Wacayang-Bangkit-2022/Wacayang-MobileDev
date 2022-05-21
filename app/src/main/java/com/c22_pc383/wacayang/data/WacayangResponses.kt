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

data class WayangPredictResponse(
	@field:SerializedName("prediction_id")
	val predictionId: Int,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("prediction_label")
	val predictionLabel: String
)

data class FavoriteResponse(
	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("favorite")
	val favorite: List<Wayang>
)

data class AlterFavResponse(
	@field:SerializedName("data")
	val data: AlterFavData,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class AlterFavData(
	@field:SerializedName("wayang")
	val wayangId: Int,

	@field:SerializedName("user")
	val user: String
)
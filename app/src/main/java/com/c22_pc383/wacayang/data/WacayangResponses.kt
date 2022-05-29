package com.c22_pc383.wacayang.data

import com.google.gson.annotations.SerializedName

data class SignUserResponse(
	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("user")
	val user: String
)

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

data class CommentResponse(
	@field:SerializedName("comments")
	val comments: List<Comment>,

	@field:SerializedName("wayang_id")
	val wayangId: Int,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class Comment(
	@field:SerializedName("comment_id")
	val commentId: Int,

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("user_name")
	val userName: String,

	@field:SerializedName("comment")
	val comment: String,

	@field:SerializedName("user_photo")
	val userPhoto: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String
)

data class AddCommentResponse(
	@field:SerializedName("wayang")
	val wayangId: Int,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("user")
	val user: String,

	@field:SerializedName("content")
	val content: String
)

data class DelCommentResponse(
	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("comment_id")
	val commentId: Int
)
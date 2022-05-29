package com.c22_pc383.wacayang.network

import com.c22_pc383.wacayang.data.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("sign")
    suspend fun signUser(
        @Header("Authorization") token: String
    ): Response<SignUserResponse>

    @GET("wayangs")
    suspend fun getWayangs(
        @Header("Authorization") token: String,
        @Query("page") page: String
    ): Response<ListWayangResponse>

    @GET("search")
    suspend fun findWayang(
        @Header("Authorization") token: String,
        @Query("name") query: String
    ): Response<SearchWayangResponse>

    @GET("wayangs/{id}")
    suspend fun getWayangDetail(
        @Header("Authorization") token: String,
        @Path("id") query: String
    ): Response<DetailWayangResponse>

    @Multipart
    @POST("predict")
    suspend fun predictWayang(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<WayangPredictResponse>

    @POST("add-favorite")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Query("wayang") itemId: Int
    ): Response<AlterFavResponse>

    @POST("del-favorite")
    suspend fun delFavorite(
        @Header("Authorization") token: String,
        @Query("wayang") itemId: Int
    ): Response<AlterFavResponse>

    @GET("favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String,
        @Query("name") query: String?
    ): Response<FavoriteResponse>

    @POST("add-comment")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Query("wayang") itemId: Int,
        @Query("comment") content: String
    ): Response<AddCommentResponse>

    @POST("del-comment")
    suspend fun delComment(
        @Header("Authorization") token: String,
        @Query("comment") itemId: Int
    ): Response<DelCommentResponse>

    @GET("comments")
    suspend fun getComments(
        @Header("Authorization") token: String,
        @Query("wayang") itemId: Int
    ): Response<CommentResponse>
}
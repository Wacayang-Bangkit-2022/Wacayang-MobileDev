package com.c22_pc383.wacayang.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = Wayang.FAV_TABLE)
@Parcelize
data class Wayang(
    @PrimaryKey
    @field:SerializedName("id")
    var id: Int,

    @field:SerializedName("name")
    var name: String,

    @field:SerializedName("description")
    var description: String,

    @field:SerializedName("image")
    var image: String,

    @field:SerializedName("video")
    var video: String,

    @field:SerializedName("is_favorite")
    var isFavorite: Int = 0,

    @field:SerializedName("total_comments")
    var totalComments: Int = 0,

    @field:SerializedName("recent_comment")
    var recentComment: String = "",

    @field:SerializedName("commenter_photo")
    var commenterPhoto: String = ""

) : Parcelable {
    companion object {
        const val FAV_TABLE = "favorite_wayang"
    }
}
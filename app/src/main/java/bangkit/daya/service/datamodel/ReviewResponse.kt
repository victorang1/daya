package bangkit.daya.service.datamodel

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("post_id")
    val postId: Int,
    val description: String,
    val username: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("user_avatar")
    val avatar: String,
    val totalLike: Int,
    val isFavorite: Int
)

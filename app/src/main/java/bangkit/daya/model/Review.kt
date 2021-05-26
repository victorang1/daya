package bangkit.daya.model

data class Review(
    val description: String,
    val username: String,
    val createdAt: String,
    val avatar: String,
    val totalLike: Int,
    val isFavorite: Boolean
)
package bangkit.daya.service.datamodel

data class InsertReviewRequest(
    val description: String,
    val username: String,
    val userId: String,
    val userAvatar: String
)

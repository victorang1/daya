package bangkit.daya.service.datamodel

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
)
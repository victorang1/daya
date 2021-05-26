package bangkit.daya.service

import bangkit.daya.service.datamodel.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*


interface AppService {

    @GET("place/{place_id}")
    fun getPlaceById(@Path("place_id") placeId: String): Observable<ApiResponse<DetailPlaceResponse>>

    @FormUrlEncoded
    @POST("place/{place_id}/posts")
    fun getPlaceReviews(
        @Path("place_id") placeId: String,
        @Field("userId") userId: String
    ): Observable<ApiResponse<List<ReviewResponse>>>

    @POST("place/{place_id}/post")
    fun insertReview(
        @Path("place_id") placeId: String,
        @Body body: InsertReviewRequest
    ): Observable<ApiResponse<EmptyResponse>>

    @FormUrlEncoded
    @POST("post/{post_id}/favorite")
    fun likeReview(
        @Path("post_id") postId: Int,
        @Field("userId") userId: String
    ): Observable<ApiResponse<EmptyResponse>>
}
package bangkit.daya.repository.detail

import bangkit.daya.service.datamodel.ApiResponse
import bangkit.daya.service.datamodel.DetailPlaceResponse
import bangkit.daya.service.datamodel.EmptyResponse
import bangkit.daya.service.datamodel.ReviewResponse
import io.reactivex.rxjava3.core.Observable

interface DetailRepository {

    fun getPlaceDetailById(placeId: String): Observable<ApiResponse<DetailPlaceResponse>>
    fun getPlaceReviews(placeId: String): Observable<ApiResponse<List<ReviewResponse>>>
    fun insertReview(placeId: String, description: String): Observable<ApiResponse<EmptyResponse>>
    fun likeReview(postId: Int): Observable<ApiResponse<EmptyResponse>>

}
package bangkit.daya.repository.detail

import bangkit.daya.service.AppService
import bangkit.daya.service.datamodel.*
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.core.Observable

class DetailRepositoryImpl(private val appService: AppService) : DetailRepository {

    override fun getPlaceDetailById(placeId: String): Observable<ApiResponse<DetailPlaceResponse>> =
        appService.getPlaceById(placeId)

    override fun getPlaceReviews(placeId: String): Observable<ApiResponse<List<ReviewResponse>>> =
        appService.getPlaceReviews(placeId, getUserId())

    override fun insertReview(placeId: String, description: String): Observable<ApiResponse<EmptyResponse>> {
        val insertPostRequest = InsertReviewRequest(
            description,
            getUsername(),
            getUserId()
        )
        return appService.insertReview(placeId, insertPostRequest)
    }

    override fun likeReview(postId: Int): Observable<ApiResponse<EmptyResponse>> =
        appService.likeReview(postId, getUserId())

    private fun getUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
    }

    private fun getUsername(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.displayName ?: currentUser?.email ?: "Anonymous"
    }
}
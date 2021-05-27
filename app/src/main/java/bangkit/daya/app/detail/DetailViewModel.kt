package bangkit.daya.app.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.model.DetailPlace
import bangkit.daya.model.Review
import bangkit.daya.repository.detail.DetailRepository
import bangkit.daya.service.datamodel.ApiResponse
import bangkit.daya.service.datamodel.DetailPlaceResponse
import bangkit.daya.service.datamodel.ReviewResponse
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class DetailViewModel(private val detailRepository: DetailRepository) : ViewModel() {

    private val mSubscriptions = CompositeDisposable()

    private val _detailData = MutableLiveData<DetailPlace>()
    val detailData: LiveData<DetailPlace> = _detailData

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews

    private val _detailEvent = MutableLiveData<Int>()
    val detailEvent: LiveData<Int> = _detailEvent

    fun loadDetail(placeId: String) {
        val detailSubs = Observable.merge(requestDetailReviews(placeId), requestPlaceReviews(placeId))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe({
                _detailEvent.postValue(LOAD_SUCCESS)
            }, {
                _error.postValue(it)
            })

        mSubscriptions.add(detailSubs)
    }

    private fun requestDetailReviews(placeId: String): Observable<ApiResponse<DetailPlaceResponse>> {
       return detailRepository.getPlaceDetailById(placeId)
            .doOnNext(::handleDetailResponse)
            .doOnError { _error.postValue(it) }
    }

    private fun requestPlaceReviews(placeId: String): Observable<ApiResponse<List<ReviewResponse>>> {
        return detailRepository.getPlaceReviews(placeId)
            .doOnNext(::handleReviewResponse)
            .doOnError { _error.postValue(it) }
    }

    private fun handleDetailResponse(response: ApiResponse<DetailPlaceResponse>?) {
        val data = response?.data
        if (data != null) {
            with(data) {
                val detailPlace = DetailPlace(
                    this.placeId,
                    name,
                    location,
                    description,
                    photo,
                    openingTime,
                    rating
                )
                _detailData.postValue(detailPlace)
            }
        } else {
            _error.postValue(Throwable("No Data"))
        }
    }

    private fun handleReviewResponse(response: ApiResponse<List<ReviewResponse>>) {
        if (response.data.isNullOrEmpty()) {
            _reviews.postValue(listOf())
        } else {
            val reviews = response.data.map {
                Review(
                    it.postId,
                    it.description,
                    it.username,
                    it.createdAt,
                    it.avatar ?: "",
                    it.totalLike,
                    it.isFavorite == 1
                )
            }
            _reviews.postValue(reviews)
        }
    }

    fun likeReview(postId: Int) {
        val likeSubs = detailRepository.likeReview(postId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe()
        mSubscriptions.add(likeSubs)
    }

    fun refreshReview(placeId: String) {
        val subscriptions = requestPlaceReviews(placeId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe(::handleReviewResponse)
        mSubscriptions.add(subscriptions)
    }

    override fun onCleared() {
        super.onCleared()
        mSubscriptions.clear()
    }

    companion object {
        const val LOAD_SUCCESS = 1
    }
}
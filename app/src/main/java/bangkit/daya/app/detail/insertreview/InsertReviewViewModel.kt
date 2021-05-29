package bangkit.daya.app.detail.insertreview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.repository.detail.DetailRepository
import com.google.gson.Gson
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class InsertReviewViewModel(private val detailRepository: DetailRepository): ViewModel() {

    private val mSubscriptions: CompositeDisposable = CompositeDisposable()

    private val _insertReviewEvent = MutableLiveData<Int>()
    val insertReviewEvent: LiveData<Int> = _insertReviewEvent

    fun insertReview(placeId: String, description: String) {
        val insertSubs = detailRepository.insertReview(placeId, description)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe({
                Log.d("<RESULT>", "insertReview: ${Gson().toJson(it)}")
                _insertReviewEvent.postValue(INSERT_SUCCESS)
            }, {
                Log.d("<RESULT>", "insertReview: ${Gson().toJson(it)}")
                _insertReviewEvent.postValue(INSERT_ERROR)
            })
        mSubscriptions.add(insertSubs)
    }

    override fun onCleared() {
        super.onCleared()
        mSubscriptions.clear()
    }

    companion object {
        const val INSERT_SUCCESS = 1
        const val INSERT_ERROR = 2
    }
}
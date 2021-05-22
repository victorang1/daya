package bangkit.daya.app.ar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.model.Place
import bangkit.daya.model.PlaceWrapper
import bangkit.daya.repository.general.GeneralRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

class ArFeatureViewModel(private val generalRepository: GeneralRepository) : ViewModel() {

    private val mSubscriptions = CompositeDisposable()

    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> = _places

    fun getNearbyPlaces(lat: Double, lng: Double) {
        val subscriptions = generalRepository.getNearbyTouristAttractionPlaces(lat, lng)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe({
                _places.postValue(it.results)
            }, {
                it.printStackTrace()
                Log.d("<RESULT>", "getNearbyPlaces: ${it.message}")
            })
        mSubscriptions.add(subscriptions)
    }

    private fun handleNearbyPlacesRequest() {

    }

    override fun onCleared() {
        mSubscriptions.clear()
        super.onCleared()
    }
}
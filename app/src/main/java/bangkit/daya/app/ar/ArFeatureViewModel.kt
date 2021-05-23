package bangkit.daya.app.ar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.model.Geolocation
import bangkit.daya.model.Loading
import bangkit.daya.model.Place
import bangkit.daya.repository.general.GeneralRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uk.co.appoly.arcorelocation.LocationScene

class ArFeatureViewModel(private val generalRepository: GeneralRepository) : ViewModel() {

    private val mSubscriptions = CompositeDisposable()

    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> = _places

    private val _geolocation = MutableLiveData<Geolocation>()
    val geolocation: LiveData<Geolocation> = _geolocation

    private val _loading = MutableLiveData<Loading>()
    val loading: LiveData<Loading> = _loading

    private fun getNearbyPlaces(lat: Double, lng: Double): Observable<List<Place>> {
        return generalRepository.getNearbyTouristAttractionPlaces(lat, lng)
            .subscribeOn(Schedulers.io())
            .map { it.results }
    }

    private fun setLoading(isLoading: Boolean, loadingMessage: String = "") {
        _loading.postValue(Loading(isLoading, loadingMessage))
    }

    fun prepareLocationService(locationScene: LocationScene, messages: List<String>) {
        setLoading(true, messages[0])
        val locationSubs = createLocationServiceObservable(locationScene)
            .subscribeOn(Schedulers.computation())
            .flatMap { geolocation ->
                _geolocation.postValue(
                    Geolocation(
                        geolocation[0].toString(),
                        geolocation[1].toString()
                    )
                )
                setLoading(true, messages[1])
                getNearbyPlaces(geolocation[0], geolocation[1])
            }
            .observeOn(Schedulers.computation())
            .subscribe {
                setLoading(false)
                _places.postValue(it)
            }
        mSubscriptions.add(locationSubs)
    }

    private fun createLocationServiceObservable(locationScene: LocationScene): Observable<List<Double>> {
        return Observable.create { subscriber ->
            var deviceLatitude: Double?
            var deviceLongitude: Double?
            do {
                deviceLatitude = locationScene.deviceLocation?.currentBestLocation?.latitude
                deviceLongitude = locationScene.deviceLocation?.currentBestLocation?.longitude
            } while (deviceLatitude == null || deviceLongitude == null)
            val results = listOf(deviceLatitude, deviceLongitude)
            subscriber.onNext(results)
            subscriber.onComplete()
        }
    }

    override fun onCleared() {
        mSubscriptions.clear()
        super.onCleared()
    }
}
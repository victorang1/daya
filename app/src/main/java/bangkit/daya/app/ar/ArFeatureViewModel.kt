package bangkit.daya.app.ar

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.model.Place
import bangkit.daya.repository.general.GeneralRepository

class ArFeatureViewModel(private val generalRepository: GeneralRepository) : ViewModel() {

    fun getNearbyPlaces(location: Location): LiveData<List<Place>> {
        return generalRepository.getNearbyTouristAttractionPlaces(location)
    }
}
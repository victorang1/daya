package bangkit.daya.app.landing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.model.LandingItem
import bangkit.daya.repository.general.GeneralRepository

class LandingViewModel(private val generalRepository: GeneralRepository) : ViewModel() {

    private val _landingItems: MutableLiveData<MutableList<LandingItem>> = MutableLiveData()
    val landingItems: LiveData<MutableList<LandingItem>> = _landingItems

    fun loadLandingData() {
        val items =  generalRepository.getLandingItems()
        _landingItems.value = items
    }
}
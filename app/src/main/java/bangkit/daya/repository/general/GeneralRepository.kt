package bangkit.daya.repository.general

import android.location.Location
import androidx.lifecycle.LiveData
import bangkit.daya.model.DashboardItem
import bangkit.daya.model.LandingItem
import bangkit.daya.model.Place

interface GeneralRepository {

    fun getLandingItems(): MutableList<LandingItem>
    fun getDashboardItems(): MutableList<DashboardItem>
    fun getNearbyTouristAttractionPlaces(location: Location): LiveData<List<Place>>
}
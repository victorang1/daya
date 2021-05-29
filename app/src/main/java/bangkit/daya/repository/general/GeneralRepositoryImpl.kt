package bangkit.daya.repository.general

import android.content.Context
import bangkit.daya.R
import bangkit.daya.model.*
import bangkit.daya.service.MapService
import io.reactivex.rxjava3.core.Observable

class GeneralRepositoryImpl(private val mapService: MapService, private val context: Context) : GeneralRepository {

    override fun getLandingItems(): MutableList<LandingItem> {
        val items = mutableListOf<LandingItem>()
        items.add(LandingItem("Find any tourist attractions around you..", R.drawable.landing_1))
        items.add(LandingItem("Gain insights about that place!", R.drawable.landing_2))
        items.add(LandingItem("Share to the community what you know", R.drawable.landing_3))
        return items
    }

    override fun getNearbyTouristAttractionPlaces(lat: Double, lng: Double): Observable<PlaceWrapper> {
        return mapService.nearbyPlaces(
            apiKey = context.getString(R.string.google_maps_key),
            location = "$lat,$lng",
            radiusInMeters = 100,
            placeType = "tourist_attraction"
        )
    }
}
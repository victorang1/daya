package bangkit.daya.repository.general

import android.content.Context
import bangkit.daya.R
import bangkit.daya.model.*
import bangkit.daya.service.MapService
import io.reactivex.rxjava3.core.Observable

class GeneralRepositoryImpl(private val mapService: MapService, private val context: Context) : GeneralRepository {

    override fun getLandingItems(): MutableList<LandingItem> {
        val items = mutableListOf<LandingItem>()
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        return items
    }

    override fun getNearbyTouristAttractionPlaces(lat: Double, lng: Double): Observable<PlaceWrapper> {
        return mapService.nearbyPlaces(
            apiKey = context.getString(R.string.google_maps_key),
            location = "$lat,$lng",
            radiusInMeters = 1000,
            placeType = "tourist_attraction"
        )
    }
}
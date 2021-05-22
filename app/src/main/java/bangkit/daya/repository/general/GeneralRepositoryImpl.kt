package bangkit.daya.repository.general

import android.content.Context
import bangkit.daya.R
import bangkit.daya.model.*
import bangkit.daya.service.ApiService
import io.reactivex.rxjava3.core.Observable

class GeneralRepositoryImpl(private val apiService: ApiService, private val context: Context) : GeneralRepository {

    override fun getLandingItems(): MutableList<LandingItem> {
        val items = mutableListOf<LandingItem>()
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        return items
    }

    override fun getDashboardItems(): MutableList<DashboardItem> {
        val dashboardItems = mutableListOf<DashboardItem>()
        dashboardItems.add(DashboardItem(R.drawable.user_navigation, "AR", R.id.action_homeFragment_to_arFragment))
        dashboardItems.add(DashboardItem(R.drawable.user_navigation, "Object Detection", R.id.action_homeFragment_to_arFragment))
        dashboardItems.add(DashboardItem(R.drawable.user_navigation, "QnA", R.id.action_homeFragment_to_questionAnswerFragment))
        return dashboardItems
    }

    override fun getNearbyTouristAttractionPlaces(lat: Double, lng: Double): Observable<PlaceWrapper> {
        return apiService.nearbyPlaces(
            apiKey = context.getString(R.string.google_maps_key),
            location = "$lat,$lng",
            radiusInMeters = 10000,
            placeType = "tourist_attraction"
        )
    }
}
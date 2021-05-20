package bangkit.daya.repository.general

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import bangkit.daya.R
import bangkit.daya.model.DashboardItem
import bangkit.daya.model.LandingItem
import bangkit.daya.model.Place
import bangkit.daya.service.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

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

    override fun getNearbyTouristAttractionPlaces(location: Location): LiveData<List<Place>> {
        return flow {
            try {
                val response = apiService.nearbyPlaces(
                    apiKey = context.getString(R.string.google_maps_key),
                    location = "${location.latitude},${location.longitude}",
                    radiusInMeters = 2000,
                    placeType = "gas_station"
                )
                val data = response.body()
                Log.d("<RESULT>", "getNearbyTouristAttractionPlaces: ${Gson().toJson(data)}")
                if (data != null) {
                    emit(data.results)
                }
                else
                    emit(mutableListOf<Place>())
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }.flowOn(Dispatchers.IO).asLiveData()
    }
}
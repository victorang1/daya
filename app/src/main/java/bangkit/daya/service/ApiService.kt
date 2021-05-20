package bangkit.daya.service

import bangkit.daya.service.datamodel.NearbyPlacesResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("nearbysearch/json")
    suspend fun nearbyPlaces(
        @Query("key") apiKey: String,
        @Query("location") location: String,
        @Query("radius") radiusInMeters: Int,
        @Query("type") placeType: String
    ): Response<NearbyPlacesResponse>
}
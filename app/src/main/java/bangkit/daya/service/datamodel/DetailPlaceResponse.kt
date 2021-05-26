package bangkit.daya.service.datamodel

import com.google.gson.annotations.SerializedName

data class DetailPlaceResponse(
    @SerializedName("place_id")
    val placeId: String,
    val name: String,
    val location: String,
    val description: String,
    val photo: String,
    val rating: String,
    @SerializedName("opening_time")
    val openingTime: String
)
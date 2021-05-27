package bangkit.daya.model

import com.google.gson.annotations.SerializedName

data class Place(
    @SerializedName("place_id")
    val id: String,
    val icon: String,
    val name: String,
    val geometry: Geometry
) {
    override fun hashCode(): Int {
        return this.name.hashCode()
    }
}

data class Geometry(
    val location: GeometryLocation
)

data class GeometryLocation(
    val lat: Double,
    val lng: Double
)
package bangkit.daya.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.utils.sphericalHeading
import kotlin.math.cos
import kotlin.math.sin

data class Place(
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
) {
    val latLng: LatLng
        get() = LatLng(lat, lng)
}

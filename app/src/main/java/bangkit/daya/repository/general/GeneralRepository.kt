package bangkit.daya.repository.general

import bangkit.daya.model.*
import io.reactivex.rxjava3.core.Observable

interface GeneralRepository {

    fun getLandingItems(): MutableList<LandingItem>
    fun getNearbyTouristAttractionPlaces(lat: Double, lng: Double): Observable<PlaceWrapper>
}
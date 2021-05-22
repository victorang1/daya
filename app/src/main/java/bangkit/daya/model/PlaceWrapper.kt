package bangkit.daya.model

import com.google.gson.annotations.SerializedName

data class PlaceWrapper(
    @SerializedName("results") var results: List<Place>
)
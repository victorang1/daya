package bangkit.daya.model

data class Recognition(var label:String, val confidence:Float, var placeId: String = "") {

    override fun toString():String{
        return "$label / $probabilityString"
    }

    val probabilityString = if (confidence == 0F) {
        ""
    } else String.format("%.1f%%", confidence * 100.0f)
}
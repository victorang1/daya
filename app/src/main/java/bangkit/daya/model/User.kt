package bangkit.daya.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var email: String = "",
    var password: String = "",
    var phoneNumber: String = ""
): Parcelable
package bangkit.daya.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var email: String = "",
    var password: String = "",
    var phoneNumber: String = ""
): Parcelable
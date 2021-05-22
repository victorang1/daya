package bangkit.daya.base

import android.Manifest
import com.google.ar.sceneform.ux.ArFragment

class ApplicationArFragment : ArFragment() {

    override fun getAdditionalPermissions(): Array<String> =
        listOf(Manifest.permission.ACCESS_FINE_LOCATION)
            .toTypedArray()
}
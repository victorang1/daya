package bangkit.daya.pref

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val edit: SharedPreferences.Editor = pref.edit()

    fun shouldShowAppFeaturesDescription(): Boolean {
        return pref.getBoolean(PREF_FIRST_TIME, true)
    }

    fun disableShouldShowAppFeaturesDescription() {
        edit.putBoolean(PREF_FIRST_TIME, false)
        edit.apply()
    }

    companion object {
        private const val PREF_NAME = "daya.pref"
        private const val PREF_FIRST_TIME = "PREF_FIRST_TIME"
    }
}
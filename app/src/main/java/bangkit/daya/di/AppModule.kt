package bangkit.daya.di

import bangkit.daya.pref.AppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single { AppPreferences(androidContext()) }
}
package bangkit.daya

import android.app.Application
import bangkit.daya.di.appModule
import bangkit.daya.di.networkModule
import bangkit.daya.di.repositoryModule
import bangkit.daya.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Created by victor on 26-Apr-21 7:14 PM.
 */
@Suppress("unused")
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@BaseApplication)
            modules(appModule, networkModule, viewModelModule, repositoryModule)
        }
    }
}
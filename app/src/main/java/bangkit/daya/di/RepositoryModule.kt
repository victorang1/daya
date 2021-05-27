package bangkit.daya.di

import bangkit.daya.repository.detail.DetailRepository
import bangkit.daya.repository.detail.DetailRepositoryImpl
import bangkit.daya.repository.general.GeneralRepository
import bangkit.daya.repository.general.GeneralRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<GeneralRepository> { GeneralRepositoryImpl(get(), androidContext()) }
    single<DetailRepository> { DetailRepositoryImpl(get()) }
}

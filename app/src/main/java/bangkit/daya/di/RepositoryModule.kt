package bangkit.daya.di

import bangkit.daya.repository.auth.AuthRepository
import bangkit.daya.repository.auth.AuthRepositoryImpl
import bangkit.daya.repository.general.GeneralRepository
import bangkit.daya.repository.general.GeneralRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl() }
    single<GeneralRepository> { GeneralRepositoryImpl(get(), androidContext()) }
}

package bangkit.daya.di

import bangkit.daya.app.home.HomeViewModel
import bangkit.daya.app.landing.LandingViewModel
import bangkit.daya.app.login.LoginViewModel
import bangkit.daya.app.signup.SignUpViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { SignUpViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { LandingViewModel(get()) }
    viewModel { HomeViewModel(get()) }
}
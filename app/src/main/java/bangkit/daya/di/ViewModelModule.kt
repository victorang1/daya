package bangkit.daya.di

import bangkit.daya.app.ar.ArFeatureViewModel
import bangkit.daya.app.detail.DetailViewModel
import bangkit.daya.app.home.HomeViewModel
import bangkit.daya.app.imagerecognition.ImageRecognitionViewModel
import bangkit.daya.app.landing.LandingViewModel
import bangkit.daya.app.login.LoginViewModel
import bangkit.daya.app.signup.SignUpViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SignUpViewModel() }
    viewModel { LoginViewModel() }
    viewModel { LandingViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ArFeatureViewModel(get()) }
    viewModel { ImageRecognitionViewModel() }
    viewModel { DetailViewModel(get()) }
}
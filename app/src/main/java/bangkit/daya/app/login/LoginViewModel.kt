package bangkit.daya.app.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.model.User
import bangkit.daya.repository.auth.AuthRepository
import bangkit.daya.util.Event

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _snackBarText: MutableLiveData<Event<String>> = MutableLiveData()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    private val _loginEvent: MutableLiveData<Event<Int>> = MutableLiveData()
    val loginEvent: LiveData<Event<Int>> = _loginEvent

    fun login(user: User?) {
        if (isUserValid(user)) {
            authRepository.login()
            _loginEvent.value = Event(LOGIN_SUCCESS)
        }
        else {
            _snackBarText.value = Event("All field must be input")
        }
    }

    private fun isUserValid(user: User?): Boolean {
        return user != null && user.email.isNotEmpty() && user.password.isNotEmpty()
    }
}

const val LOGIN_SUCCESS = 0
package bangkit.daya.app.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.model.User
import bangkit.daya.util.Event

class LoginViewModel : ViewModel() {

    private val _snackBarText: MutableLiveData<Event<String>> = MutableLiveData()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    private val _loginEvent: MutableLiveData<Event<Int>> = MutableLiveData()
    val loginEvent: LiveData<Event<Int>> = _loginEvent

    fun isValid(user: User): Boolean {
        if (!isUserValid(user)) {
            _snackBarText.value = Event("All field must be input")
            return false
        }
        return true
    }

    private fun isUserValid(user: User): Boolean = user.email.isNotEmpty() && user.password.isNotEmpty()

    fun sendEvent(eventId: Int) {
        _loginEvent.value = Event(eventId)
    }
}

const val LOGIN_SUCCESS: Int = 1
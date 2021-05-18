package bangkit.daya.app.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.model.User
import bangkit.daya.util.Event

class SignUpViewModel : ViewModel() {

    private val _snackBarText: MutableLiveData<Event<String>> = MutableLiveData()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    private val _registerEvent: MutableLiveData<Event<Int>> = MutableLiveData()
    val registerEvent: LiveData<Event<Int>> = _registerEvent

    fun isValid(user: User): Boolean {
        if (!isUserValid(user)) {
            _snackBarText.value = Event("All field must be input")
            return false
        }
        return true
    }

    fun sendEvent(eventId: Int) {
        _registerEvent.value = Event(eventId)
    }

//    fun register(user: User?) {
//        if (isSecondStepValid(user)) {
//            authRepository.register(user!!)
//            _registerEvent.value = Event(REGISTER_SUCCESS)
//        }
//        else {
//            _snackBarText.value = Event("All field must be input")
//        }
//    }

    private fun isUserValid(user: User): Boolean =user.email.isNotEmpty() && user.password.isNotEmpty()

//    private fun isSecondStepValid(user: User?): Boolean {
//        return user != null && user.phoneNumber.isNotEmpty()
//    }
}

const val REDIRECT_TO_SECOND_STEP: Int = 0
const val REGISTER_SUCCESS: Int = 1
const val REGISTER_ERROR: Int = 2
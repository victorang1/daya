package bangkit.daya.app.imagerecognition

import android.system.Os.open
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.model.Recognition
import java.io.BufferedReader
import java.io.InputStreamReader

class ImageRecognitionViewModel : ViewModel() {

    private val _showedItem = MutableLiveData<Recognition>()
    val showedItem: LiveData<Recognition> = _showedItem

    fun updateData(recognition: List<Recognition>){
        val notFound = Recognition("No object is found", 0F)
        if (recognition.isNullOrEmpty()) {
            _showedItem.postValue(notFound)
            return
        }
        val res = recognition[0]
        if (res.confidence < 0.75) {
            _showedItem.postValue(notFound)
            return
        }
        _showedItem.postValue(recognition[0])
    }

    fun resetValue() {
        _showedItem.postValue(Recognition("", 0F))
    }
}
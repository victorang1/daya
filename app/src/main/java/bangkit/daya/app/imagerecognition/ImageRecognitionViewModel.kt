package bangkit.daya.app.imagerecognition

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.model.ModelResult
import bangkit.daya.model.Recognition

class ImageRecognitionViewModel : ViewModel() {

    private val _showedItem = MutableLiveData<Recognition>()
    val showedItem: LiveData<Recognition> = _showedItem

    private val _modelResultLabels = MutableLiveData<MutableList<ModelResult>>()
    val modelResultLabels: LiveData<MutableList<ModelResult>> = _modelResultLabels

    private val _fabRetryVisibility = MutableLiveData<Int>()
    val fabRetryVisibility: LiveData<Int> = _fabRetryVisibility

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
        val tempResult: ModelResult? = _modelResultLabels.value?.find {
            it.label == recognition[0].label
        }
        tempResult?.let {
            recognition[0].placeId = it.placeId
        }
        _fabRetryVisibility.postValue(View.VISIBLE)
        _showedItem.postValue(recognition[0])
    }

    fun setModelResult(results: MutableList<ModelResult>) {
        _modelResultLabels.postValue(results)
    }

    fun resetValue() {
        hideRetryButton()
        _showedItem.postValue(Recognition("", 0F))
    }

    fun hideRetryButton() {
        _fabRetryVisibility.postValue(View.GONE)
    }
}
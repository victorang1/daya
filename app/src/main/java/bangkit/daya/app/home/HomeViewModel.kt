package bangkit.daya.app.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.daya.model.DashboardItem
import bangkit.daya.repository.general.GeneralRepository

/**
 * Created by victor on 13-May-21 10:43 AM.
 */
class HomeViewModel(private val generalRepository: GeneralRepository): ViewModel() {

    private val _dashboardItems: MutableLiveData<MutableList<DashboardItem>> = MutableLiveData()
    val dashboardItems: LiveData<MutableList<DashboardItem>> = _dashboardItems

    fun loadDashboardData() {
        val items =  generalRepository.getDashboardItems()
        _dashboardItems.value = items
    }
}
package bangkit.daya.repository.general

import bangkit.daya.model.DashboardItem
import bangkit.daya.model.LandingItem

interface GeneralRepository {

    fun getLandingItems(): MutableList<LandingItem>
    fun getDashboardItems(): MutableList<DashboardItem>
}
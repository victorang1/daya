package bangkit.daya.repository.general

import bangkit.daya.R
import bangkit.daya.model.DashboardItem
import bangkit.daya.model.LandingItem

class GeneralRepositoryImpl : GeneralRepository {

    override fun getLandingItems(): MutableList<LandingItem> {
        val items = mutableListOf<LandingItem>()
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        return items
    }

    override fun getDashboardItems(): MutableList<DashboardItem> {
        val dashboardItems = mutableListOf<DashboardItem>()
        dashboardItems.add(DashboardItem(R.drawable.user_navigation, "AR"))
        dashboardItems.add(DashboardItem(R.drawable.user_navigation, "Object Detection"))
        dashboardItems.add(DashboardItem(R.drawable.user_navigation, "QnA"))
        return dashboardItems
    }
}
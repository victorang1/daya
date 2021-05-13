package bangkit.daya.app.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bangkit.daya.databinding.DashboardItemBinding
import bangkit.daya.model.DashboardItem

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.DashboardViewHolder>() {

    private val features: MutableList<DashboardItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = DashboardItemBinding.inflate(layoutInflater, parent, false)
        return DashboardViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val dashboardItem = features[position]
        holder.bind(dashboardItem)
    }

    override fun getItemCount(): Int = features.count()

    fun setData(newFeatures: MutableList<DashboardItem>) {
        this.features.clear()
        this.features.addAll(newFeatures)
        notifyDataSetChanged()
    }

    inner class DashboardViewHolder(private val itemBinding: DashboardItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: DashboardItem) {
            itemBinding.dashboardItem = item
        }
    }
}
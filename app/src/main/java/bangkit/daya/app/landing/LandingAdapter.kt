package bangkit.daya.app.landing

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bangkit.daya.databinding.LandingItemLayoutBinding
import bangkit.daya.model.LandingItem

/**
 * Created by victor on 26-Apr-21 8:13 PM.
 */
class LandingAdapter(private val mContext: Context) :
    RecyclerView.Adapter<LandingAdapter.LandingViewHolder>() {

    private val items: MutableList<LandingItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandingViewHolder {
        val layoutInflater = LayoutInflater.from(mContext)
        val itemLayoutBinding = LandingItemLayoutBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return LandingViewHolder(itemLayoutBinding)
    }

    override fun onBindViewHolder(holder: LandingViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.count()

    inner class LandingViewHolder(private val itemLayoutBinding: LandingItemLayoutBinding) :
        RecyclerView.ViewHolder(itemLayoutBinding.root) {

        fun bind(item: LandingItem) {
            itemLayoutBinding.landingItem = item
        }
    }

    fun setData(newItems: MutableList<LandingItem>) {
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }
}
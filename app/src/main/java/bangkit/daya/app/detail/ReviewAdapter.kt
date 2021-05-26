package bangkit.daya.app.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bangkit.daya.databinding.PostItemLayoutBinding
import bangkit.daya.model.Review
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding4.view.clicks
import java.util.concurrent.TimeUnit

class ReviewAdapter(private val onButtonLikeClick: (review: Review) -> Unit): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private var reviews = listOf<Review>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = PostItemLayoutBinding.inflate(layoutInflater, parent, false)
        return ReviewViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int = reviews.count()

    fun setData(reviews: List<Review>) {
        this.reviews = reviews
        notifyDataSetChanged()
    }

    inner class ReviewViewHolder(private val postItemLayoutBinding: PostItemLayoutBinding) :
        RecyclerView.ViewHolder(postItemLayoutBinding.root) {

        fun bind(reviewItem: Review) {
            with (postItemLayoutBinding) {
                review = reviewItem
                btnLike.clicks()
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe {
                        reviewItem.toggleFavorite()
                        onButtonLikeClick.invoke(reviewItem)
                    }
                Glide.with(postItemLayoutBinding.root.context)
                    .load(reviewItem.avatar)
                    .circleCrop()
                    .into(cvProfile)
            }
        }
    }
}
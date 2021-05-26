package bangkit.daya.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import bangkit.daya.BR
import bangkit.daya.R

data class Review(
    val postId: Int,
    val description: String,
    val username: String,
    val createdAt: String,
    val avatar: String,
    val totalLike: Int,
    @Bindable
    var isFavorite: Boolean
): BaseObservable() {

    fun toggleFavorite() {
        this.isFavorite = !isFavorite
        notifyPropertyChanged(BR.isFavorite)
        notifyPropertyChanged(BR.btnLikeDrawable)
    }

    @Bindable
    fun getBtnLikeDrawable(): Int {
        return if (isFavorite) R.drawable.ic_like else R.drawable.ic_dislike
    }
}
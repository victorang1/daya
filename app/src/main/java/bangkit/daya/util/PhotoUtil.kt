package bangkit.daya.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter

/**
 * Created by victor on 27-Apr-21 8:51 AM.
 */
@BindingAdapter("android:src")
fun setImageViewResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}
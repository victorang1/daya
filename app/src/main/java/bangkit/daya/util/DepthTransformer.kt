package bangkit.daya.util

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs


/**
 * Created by victor on 27-Apr-21 9:19 AM.
 */
class DepthTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        when (position) {
            in Float.NEGATIVE_INFINITY..-2F -> { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            in -1F..0F -> { // [-1,0]
                page.alpha = 1F
                page.translationX = 0F
                page.scaleX = 1F
                page.scaleY = 1F
            }
            in 0F..1F -> { // (0,1]
                page.translationX = -position*page.width
                page.alpha = 1- abs(position)
                page.scaleX = 1- abs(position)
                page.scaleY = 1- abs(position)
            }
            else -> { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }
}
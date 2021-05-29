package bangkit.daya.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.MarginLayoutParams

import com.google.android.material.floatingactionbutton.FloatingActionButton

class MovableFloatingActionButton : FloatingActionButton, View.OnTouchListener {
    private var downRawX = 0f
    private var downRawY = 0f
    private var dX = 0f
    private var dY = 0f
    private var viewWidth = 0
    private var viewHeight = 0
    private var viewParent: View? = null
    private var parentWidth = 0
    private var parentHeight = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setOnTouchListener(this)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        viewWidth = view.width
        viewHeight = view.height
        viewParent = view.parent as View
        parentWidth = viewParent!!.width
        parentHeight = viewParent!!.height
        val layoutParams = view.layoutParams as MarginLayoutParams
        val action = motionEvent.action
        return if (action == MotionEvent.ACTION_DOWN) {
            downRawX = motionEvent.rawX
            downRawY = motionEvent.rawY
            dX = view.x - downRawX
            dY = view.y - downRawY
            true
        } else if (action == MotionEvent.ACTION_MOVE) {
            val viewWidth: Int = view.width
            val viewHeight: Int = view.height
            val viewParent: View = view.parent as View
            val parentWidth: Int = viewParent.width
            val parentHeight: Int = viewParent.height
            var newX = motionEvent.rawX + dX
            newX = (parentWidth - viewWidth).toFloat().coerceAtMost(newX)
            if (newX < 0) newX = 0f else if (newX > parentWidth) newX = parentWidth.toFloat()
            var newY = motionEvent.rawY + dY
            newY = (parentHeight - viewHeight).toFloat().coerceAtMost(newY)
            if (newY < 0) newY = 0f else if (newY > parentHeight) newY = parentHeight.toFloat()
            view.animate()
                .x(newX)
                .y(newY)
                .setDuration(0)
                .start()
            true
        } else if (action == MotionEvent.ACTION_UP) {
            val upRawX = motionEvent.rawX
            val upRawY = motionEvent.rawY
            val upDX = upRawX - downRawX
            val upDY = upRawY - downRawY
            val batasRight = parentWidth - viewWidth //926
            val batasBottom = parentHeight - viewHeight //1622
            val koordinatX: Float = view.x
            val koordinatY: Float = view.y
            val rangeKanan = batasRight - koordinatX
            val minimumKiriKanan = koordinatX.coerceAtMost(rangeKanan)
            if (minimumKiriKanan == koordinatX) {
                view.animate()
                    .x(layoutParams.leftMargin.toFloat())
                    .y(checkCurrentYPosition(koordinatY, layoutParams, batasBottom))
                    .setDuration(500)
                    .start()
            } else if (minimumKiriKanan == rangeKanan) {
                view.animate()
                    .x(batasRight - layoutParams.rightMargin.toFloat())
                    .y(checkCurrentYPosition(koordinatY, layoutParams, batasBottom))
                    .setDuration(500)
                    .start()
            }
            if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) {
                performClick()
            } else {
                true
            }
        } else {
            super.onTouchEvent(motionEvent)
        }
    }

    private fun checkCurrentYPosition(
        koordinatY: Float,
        layoutParams: MarginLayoutParams,
        batasBottom: Int
    ): Float {
        return if (koordinatY < layoutParams.topMargin) layoutParams.topMargin.toFloat() else if (koordinatY > batasBottom - layoutParams.bottomMargin) (batasBottom - layoutParams.bottomMargin).toFloat() else koordinatY
    }

    companion object {
        private const val CLICK_DRAG_TOLERANCE = 10f
    }
}
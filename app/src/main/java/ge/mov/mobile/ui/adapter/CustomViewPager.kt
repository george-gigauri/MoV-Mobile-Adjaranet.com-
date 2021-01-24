package ge.mov.mobile.ui.adapter

import android.content.Context
import android.view.View
import androidx.viewpager.widget.ViewPager

class CustomViewPager(context: Context) : ViewPager(context) {
    private var mCurrentPagePosition = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        try {
            val wrapHeight = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST
            if (wrapHeight) {
                val child: View? = getChildAt(mCurrentPagePosition)
                if (child != null) {
                    child.measure(
                        widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    )
                    val h: Int = child.measuredHeight
                    this.minimumHeight = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun reMeasureCurrentPage(position: Int) {
        mCurrentPagePosition = position
        requestLayout()
    }
}
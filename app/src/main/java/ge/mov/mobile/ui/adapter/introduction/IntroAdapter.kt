package ge.mov.mobile.ui.adapter.introduction

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import ge.mov.mobile.R
import ge.mov.mobile.data.model.IntroModel

class IntroAdapter (
    private val context: Context,
    private val arr: List<IntroModel>
) : PagerAdapter() {

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val i = arr[position]
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.intro_item, null)

        val imageView: ImageView = view.findViewById(R.id.image_intro)
        val title: TextView = view.findViewById(R.id.title_intro)
        val description: TextView = view.findViewById(R.id.description_intro)

        imageView.setImageDrawable(ContextCompat.getDrawable(context, i.image))
        title.text = i.title
        description.text = i.description

        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return arr.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
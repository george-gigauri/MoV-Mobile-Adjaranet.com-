package ge.mov.mobile.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import ge.mov.mobile.R
import ge.mov.mobile.model.featured.FeaturedModel
import ge.mov.mobile.ui.activity.MovieActivity

class SliderAdapter (
    private val context: Context,
    val slides: List<FeaturedModel>
): PagerAdapter() {
    private lateinit var inflater: LayoutInflater
    override fun getCount(): Int {
        return slides.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.slider_item, container, false)

        val image: ImageView = view.findViewById(R.id.slider_image)
        val i = slides[position]

        val poster = if (i.cover.large != "") {
            i.cover.large
        } else {
            i.poster
        }

        Glide.with(context)
            .asDrawable()
            .load(poster)
            .into(image)

        view.setOnClickListener {
            val intent = Intent(context, MovieActivity::class.java)
            intent.putExtra("id", slides[position].id)
            intent.putExtra("adjaraId", slides[position].adjaraId)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

    fun getListCount() : Int {
        return slides.size
    }
}
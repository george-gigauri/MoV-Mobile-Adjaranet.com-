package ge.mov.mobile.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import coil.load
import coil.request.CachePolicy
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ge.mov.mobile.R
import ge.mov.mobile.model.featured.FeaturedModel
import ge.mov.mobile.ui.activity.MovieActivity
import ge.mov.mobile.util.Utils

class SliderAdapter (
    private val context: Context,
    private val slides: List<FeaturedModel>
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
        val title: TextView = view.findViewById(R.id.slider_title)

        val i = slides[position]

        val language = Utils.loadLanguage(context)
        val lang_code = if (language?.id == "ka") "GEO" else "ENG"

        title.text = if (lang_code == "GEO")
            if (i.primaryName != "")
                i.primaryName
            else
                i.secondaryName
        else
            i.originalName

        val poster = if (i.cover.large != "") {
            i.cover.large
        } else {
            i.poster
        }

        image.load(poster) {
            crossfade(true); crossfade(420);
            memoryCachePolicy(CachePolicy.DISABLED);
            diskCachePolicy(CachePolicy.DISABLED)
        }

        view.setOnClickListener {
            Glide.get(context).clearMemory()
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
}
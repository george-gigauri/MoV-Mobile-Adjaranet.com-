package ge.mov.mobile.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import coil.load
import coil.request.CachePolicy
import ge.mov.mobile.MovApplication.Companion.language
import ge.mov.mobile.data.model.featured.FeaturedModel
import ge.mov.mobile.databinding.SliderItemBinding

class SliderAdapter (
    private val context: Context,
    private val slides: List<FeaturedModel>,
    private val listener: OnClickListener
): PagerAdapter() {
    private lateinit var inflater: LayoutInflater
    private lateinit var binding: SliderItemBinding

    override fun getCount() = slides.size

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = SliderItemBinding.inflate(inflater, container, false)
        val view = binding.root

        val i = slides[position]

        val lang_code = if (language?.id == "ka") "GEO" else "ENG"

        binding.sliderTitle.text = if (lang_code == "GEO")
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

        binding.sliderImage.load(poster) {
            memoryCachePolicy(CachePolicy.DISABLED)
            diskCachePolicy(CachePolicy.DISABLED)
        }

        binding.movieHover.setOnClickListener { listener.onSlideClick(i) }

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) { container.removeView(`object` as ConstraintLayout) }

    interface OnClickListener {
        fun onSlideClick(item: FeaturedModel)
    }
}
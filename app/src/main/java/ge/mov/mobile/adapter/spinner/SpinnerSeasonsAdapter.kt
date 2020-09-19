package ge.mov.mobile.adapter.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ge.mov.mobile.R
import ge.mov.mobile.model.Series.Episode
import ge.mov.mobile.model.Series.EpisodeFiles
import kotlinx.android.synthetic.main.spinner_item_view.view.*

class SpinnerSeasonsAdapter (
    private val ctx: Context,
    private val list: EpisodeFiles
): ArrayAdapter<Episode>(ctx, 0, list.data) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup) : View {
        var convertVieww = convertView

        if (convertVieww == null)
            convertVieww = LayoutInflater.from(ctx).inflate(
                R.layout.spinner_item_view,
                parent,
                false
            )

        val i = list.data[position]

        convertVieww?.rootView?.txttitle?.text = i.episode.toString()

        return convertVieww!!
    }

    override fun getCount(): Int {
        return list.data.size
    }
}
package ge.mov.mobile.adapter.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ge.mov.mobile.R
import ge.mov.mobile.model.Series.Episode
import ge.mov.mobile.model.Series.File
import kotlinx.android.synthetic.main.spinner_item_view.view.*

class SpinnerEpisodeAdapter(
    private val ctx: Context,
    private val episodes: Episode
): ArrayAdapter<File>(ctx, 0, episodes.files) {
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

        val i = episodes.files[position]

        convertVieww?.rootView?.txttitle?.text = i.lang

        return convertVieww!!
    }
}
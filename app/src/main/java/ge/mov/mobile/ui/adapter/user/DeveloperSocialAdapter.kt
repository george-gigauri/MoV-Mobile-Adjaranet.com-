package ge.mov.mobile.ui.adapter.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import ge.mov.mobile.R
import ge.mov.mobile.data.model.Social
import kotlinx.android.synthetic.main.developer_info_item.view.*

class DeveloperSocialAdapter(
    private val listener: OnSocialItemClicked,
    private val array: List<Social>
) : RecyclerView.Adapter<DeveloperSocialAdapter.VH>() {
    inner class VH(i: View) : RecyclerView.ViewHolder(i)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.developer_info_item, parent, false)
        )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val i = array[position]

        holder.itemView.icon.load(i.icon) {
            diskCachePolicy(CachePolicy.DISABLED); memoryCachePolicy(
            CachePolicy.DISABLED
        )
        }
        holder.itemView.setOnClickListener { listener.onSocialItemClick(i) }
    }

    override fun getItemCount() = array.size

    interface OnSocialItemClicked {
        fun onSocialItemClick(i: Social)
    }
}
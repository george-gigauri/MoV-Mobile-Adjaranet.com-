package ge.mov.mobile.adapter.user

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import ge.mov.mobile.R
import ge.mov.mobile.model.DeveloperInfo
import ge.mov.mobile.model.Social
import kotlinx.android.synthetic.main.developer_info_item.view.*

class DeveloperSocialAdapter (
    private val context: Context,
    private val array: List<Social>
) : RecyclerView.Adapter<DeveloperSocialAdapter.VH>() {
    inner class VH(i: View): RecyclerView.ViewHolder(i)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context)
            .inflate(R.layout.developer_info_item, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val i = array[position]

        holder.itemView.icon.load(i.icon) { diskCachePolicy(CachePolicy.DISABLED); memoryCachePolicy(CachePolicy.DISABLED) }
        holder.itemView.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse(i.url)
                context.startActivity(intent)
            } catch (e: Exception) { }
        }
    }

    override fun getItemCount() = array.size
}
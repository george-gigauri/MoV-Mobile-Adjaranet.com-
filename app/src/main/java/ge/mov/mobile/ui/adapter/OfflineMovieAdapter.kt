package ge.mov.mobile.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.R
import ge.mov.mobile.data.database.entity.OfflineMovieEntity
import ge.mov.mobile.databinding.ItemOfflineMovieBinding
import ge.mov.mobile.extension.visible
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OfflineMovieAdapter(
    private val listener: OfflineMovieActionListener
) : RecyclerView.Adapter<OfflineMovieAdapter.VH>() {

    private val arr = ArrayList<OfflineMovieEntity>()
    val selectedItems = ArrayList<OfflineMovieEntity>()

    var isSelectMode = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class VH(private val binding: ItemOfflineMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(i: OfflineMovieEntity) {
            if (selectedItems.contains(i)) {
                binding.select.visible(true)
                binding.info.isSelected = true
            } else {
                binding.select.visible(false)
                binding.info.isSelected = false
            }

            binding.thumb.setImageBitmap(i.image)
            binding.title.text = i.title
            binding.season.text = "${binding.root.context.getString(R.string.season)}:  ${i.season}"
            binding.episode.text =
                "${binding.root.context.getString(R.string.episode)}:  ${i.episode}"
            binding.createdAt.text = SimpleDateFormat("dd MMM. HH:mm").format(Date(i.savedAt!!))
            binding.expires.text = SimpleDateFormat("dd MMM.").format(Date(i.expiresAt!!))

            if (i.isExpiringSoon!!)
                binding.expires.visible(true)
            else binding.expires.visible(false)

            binding.root.setOnClickListener { listener.onClick(i, arr.indexOf(i)) }

            binding.root.setOnLongClickListener {
                listener.onLongClick(i, arr.indexOf(i))
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            ItemOfflineMovieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OfflineMovieAdapter.VH, position: Int) {
        val i = arr[position]

        holder.bind(i)
    }

    override fun getItemCount(): Int = arr.size

    fun addAll(items: List<OfflineMovieEntity>) {
        arr.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        arr.clear()
        notifyDataSetChanged()
    }

    fun setSelected(i: OfflineMovieEntity, position: Int) {
        if (selectedItems.contains(i))
            selectedItems.remove(i)
        else selectedItems.add(i)

        notifyItemChanged(position)
    }

    fun selectAll() {
        arr.forEach {
            setSelected(it, arr.indexOf(it))
        }

        notifyDataSetChanged()
    }

    fun unselectAll() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    interface OfflineMovieActionListener {
        fun onClick(i: OfflineMovieEntity, position: Int)
        fun onLongClick(i: OfflineMovieEntity, position: Int)
    }
}
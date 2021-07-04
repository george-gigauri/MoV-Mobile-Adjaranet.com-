package ge.mov.mobile.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.R
import ge.mov.mobile.data.model.Language
import ge.mov.mobile.databinding.GenreModelBinding
import ge.mov.mobile.util.LanguageUtil

class LanguageAdapter(private val listener: OnLanguageSelectedListener) :
    RecyclerView.Adapter<LanguageAdapter.VH>() {
    private val languages = ArrayList<Language>()

    inner class VH(private val binding: GenreModelBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.genre.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    select(position)
                    val item = languages[position]
                    if (item.selected)
                        listener.onLanguageSelected(item)
                }
            }
        }

        fun bind(language: Language) {
            val locale = LanguageUtil.language
            binding.genre.text = if (locale != null) {
                if (locale.code == "GE") language.primaryNameTurned else language.secondaryName
            } else language.primaryNameTurned

            binding.genre.isSelected = language.selected
            if (language.selected) {
                binding.genre.setTextColor(binding.root.context.resources.getColor(R.color.colorAccent))
            } else {
                binding.genre.setTextColor(Color.BLACK)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        GenreModelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val i = languages[position]

        holder.bind(i)
    }

    override fun getItemCount(): Int = languages.count()

    fun addAll(newLanguages: List<Language>) {
        this.languages.addAll(newLanguages)
        this.notifyDataSetChanged()
    }

    fun select(position: Int) {
        for (i in languages)
            i.selected = false
        this.languages[position].selected = true
        notifyDataSetChanged()
    }

    fun select(code: String) {
        for (i in languages) {
            i.selected = i.code == code
        }
        notifyDataSetChanged()
    }

    fun firstSelectedPosition(): Int{
        val selectedItem = languages.filter { it.selected }

        return if (!selectedItem.isNullOrEmpty()) {
            val index = languages.indexOf(selectedItem[0])

            if (index >= 3)
                index - 1
            else index
        } else 0
    }

    interface OnLanguageSelectedListener {
        fun onLanguageSelected(language: Language)
    }
}
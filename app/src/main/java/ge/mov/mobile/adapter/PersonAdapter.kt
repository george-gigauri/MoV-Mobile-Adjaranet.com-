package ge.mov.mobile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.mov.mobile.R
import ge.mov.mobile.model.Series.PersonModel
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.cast_model.view.*
import kotlinx.android.synthetic.main.fragment_movies.view.*

class PersonAdapter (
    private val arr: List<PersonModel>,
    private val context: Context
) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {
    class ViewHolder(i: View) : RecyclerView.ViewHolder(i)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(
                R.layout.cast_model,
                parent,
                false
            ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = arr[position]
        val role = i.personRole.data

        val language = Utils.loadLanguage(context)
        val lang_code = if (language?.id == "ka") "GEO" else "ENG"
        holder.itemView.person_name.text = if (lang_code == "GEO")
            if (i.primaryName != "")
                i.primaryName
            else
                i.secondaryName
        else
            i.secondaryName

        holder.itemView.person_role.text = if (role.character != "") role.character else role.role

        Glide.with(context)
            .asDrawable()
            .placeholder(R.color.colorAccent)
            .error(R.color.colorAccent)
            .load(i.poster)
            .into(holder.itemView.person_img)
    }

    override fun getItemCount(): Int {
        return arr.size
    }
}
package ge.mov.mobile.ui.activity.offline

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.data.database.entity.OfflineMovieEntity
import ge.mov.mobile.databinding.ActivityDownloadedMoviesBinding
import ge.mov.mobile.extension.setPreferredColor
import ge.mov.mobile.extension.startFuckingService
import ge.mov.mobile.extension.visible
import ge.mov.mobile.service.DeleteMovieService
import ge.mov.mobile.ui.activity.base.BaseActivity
import ge.mov.mobile.ui.activity.movie.WatchActivity
import ge.mov.mobile.ui.activity.offline.bottom.BottomInfo
import ge.mov.mobile.ui.adapter.OfflineMovieAdapter
import java.io.File

@AndroidEntryPoint
class DownloadedMoviesActivity : BaseActivity<ActivityDownloadedMoviesBinding>(),
    OfflineMovieAdapter.OfflineMovieActionListener {

    override val bindingFactory: (LayoutInflater) -> ActivityDownloadedMoviesBinding
        get() = { ActivityDownloadedMoviesBinding.inflate(it) }

    private val vm: OfflineViewModel by viewModels()
    private lateinit var adapter: OfflineMovieAdapter

    override fun setup(savedInstanceState: Bundle?) {
        adapter = OfflineMovieAdapter(this)
        binding.offlineMoviesRv.adapter = adapter

        setPreferredColor(binding.root)

        binding.goBack.setOnClickListener { onBack() }
        binding.actionInfo.setOnClickListener { openInfoDialog() }
        binding.actionEdit.setOnClickListener { enterEditing() }
        binding.delete.setOnClickListener { delete() }
        binding.selectAll.setOnClickListener { selectAll() }

        vm.allMovies.observe(this) {
            if (it.isNullOrEmpty())
                binding.empty.visible(true)
            else binding.empty.visible(false)

            adapter.clear()
            adapter.addAll(it)
        }
    }

    private fun openInfoDialog() {
        val dialog = BottomInfo.getInstance(
            "ჩამოტვირთული ფილმები",
            "ჩამოტვირთული ფილმები ის ფილმებია, რომლებსაც შეგიძლია უყურო მაშინაც კი, როცა არ გაქვს " +
                    "ინტერნეტთან კავშირი.\nფილმის ჩამოსატვირთად დაბრუნდი მთავარ გვერდზე, აირჩიე რისი ყურება გინდა, დააჭირე ფლეერს და " +
                    "დააჭირე 'გადმოწერე'-ს.\n\nგადმოწერილი ფილმები შენს სმარტფონში სამუდამოდ შეინახება." +
                    "\n\nგაითვალისწინე:  ტელეფონში უნდა გქონდეს საკმარისი მეხსიერება რომ ფილმი დაეტიოს და ხარვეზებიც ავირიდოთ თავიდან :)"
        )
        dialog.show(supportFragmentManager, null)
    }

    private fun enterEditing() {
        adapter.isSelectMode = true

        binding.selectAll.visible(true)
        binding.actionEdit.visible(false)
        binding.delete.visible(true)
    }

    private fun exitEditing() {
        adapter.unselectAll()
        adapter.isSelectMode = false

        binding.selectAll.visible(false)
        binding.actionEdit.visible(true)
        binding.delete.visible(false)
    }

    private fun selectAll() {
        if (adapter.isSelectMode) {
            adapter.selectAll()
        }
    }

    private fun delete() {
        val items = adapter.selectedItems

        if (!items.isNullOrEmpty()) {
            val its = ArrayList(items.map { File(it.src!!).name })

            val intent = Intent(this, DeleteMovieService::class.java)
            intent.putExtra("names", its)
            startFuckingService(intent)
        } else Log.i("FileName", "Null")

        vm.deleteByIds(items.map { it.i!! })
        exitEditing()
    }

    private fun onBack() {
        if (adapter.isSelectMode) {
            exitEditing()
        } else finish()
    }

    override fun onBackPressed() {
        onBack()
    }

    override fun onClick(i: OfflineMovieEntity, position: Int) {
        if (adapter.isSelectMode)
            adapter.setSelected(i, position)
        else {
            Log.i("MediaToSend", i.src!!)

            val intent = Intent(this, WatchActivity::class.java)
            intent.putExtra("src", i.src)
            intent.putExtra("id", i.id)
            intent.putExtra("s", i.season)
            intent.putExtra("e", i.episode)
            startActivity(intent)
        }
    }

    override fun onLongClick(i: OfflineMovieEntity, position: Int) {
        enterEditing()
        adapter.setSelected(i, position)
    }
}
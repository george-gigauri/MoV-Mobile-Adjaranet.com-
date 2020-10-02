package ge.mov.mobile.ui.activity

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import ge.mov.mobile.R
import ge.mov.mobile.adapter.GenreAdapter
import ge.mov.mobile.adapter.MovieAdapter
import ge.mov.mobile.adapter.SliderAdapter
import ge.mov.mobile.databinding.ActivityMainBinding
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.ui.activity.viewmodel.MainActivityViewModel
import ge.mov.mobile.ui.fragment.SearchFragment
import ge.mov.mobile.util.Utils
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager
    lateinit var sliderAdapter: SliderAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myLang = Utils.loadLanguage(this)
        if (myLang != null) {
            Utils.saveLanguage(this, myLang)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        if (Utils.isFirstUse(this)) {
            startActivity(
                Intent(
                    applicationContext,
                    ApplicationSetupActivity::class.java
                ).addFlags(
                    FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
                )
            )
        }

        viewPager = findViewById(R.id.slider)

        val vm = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        binding.main = vm

        binding.btnProfilePic.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        binding.btnSearchMovie.setOnClickListener {
            supportFragmentManager.beginTransaction().add(R.id.root_main, SearchFragment(), "null").addToBackStack(
                "search"
            ).commit()
        }

        vm.getGenresFull().observe(this, Observer {
            binding.progress.visibility = View.VISIBLE
            binding.categories.adapter = GenreAdapter(it.data, this, 1)
            binding.categories.post {
                binding.progress.visibility = View.GONE
            }
        })

        vm.getMovies().observe(this, Observer {
            binding.progress.visibility = View.VISIBLE
            binding.movies.adapter = MovieAdapter(applicationContext, it as ArrayList<MovieModel>)

            binding.movies.post {
                binding.progress.visibility = View.GONE
            }
        })

        vm.getSeries().observe(this, Observer {
            binding.progress.visibility = View.VISIBLE
            binding.series.adapter = MovieAdapter(applicationContext, it as ArrayList<MovieModel>)

            binding.series.post {
                binding.progress.visibility = View.GONE
            }
        })

        vm.getSlides().observe(this, Observer {
            sliderAdapter = SliderAdapter(applicationContext, it)
            binding.slider.adapter = sliderAdapter

            timer = Timer()
            timer.scheduleAtFixedRate(SliderTimerTask(), 3500, 4000)
        })

    }

    inner class SliderTimerTask : TimerTask()
    {
        override fun run() {
            runOnUiThread {
                if (viewPager.currentItem == sliderAdapter.count - 1)
                    viewPager.currentItem = 0
                else
                    viewPager.currentItem++
            }
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
            supportFragmentManager.beginTransaction().remove(supportFragmentManager.fragments[0])
        }
    }
}
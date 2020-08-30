package ge.mov.mobile.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import ge.mov.mobile.R
import ge.mov.mobile.adapter.MovieAdapter
import ge.mov.mobile.adapter.SliderAdapter
import ge.mov.mobile.ui.activity.viewmodel.MainActivityViewModel
import ge.mov.mobile.util.toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager
    lateinit var sliderAdapter: SliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.slider)

        val vm = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        vm.getMovies().observe(this, Observer {
            movies.adapter = MovieAdapter(applicationContext, it)
        })

        vm.getSeries().observe(this, Observer {
            series.adapter = MovieAdapter(applicationContext, it)
        })

        vm.getSlides().observe(this, Observer {
            sliderAdapter = SliderAdapter(this, it)
            viewPager.adapter = sliderAdapter

            val timer = Timer()
            timer.scheduleAtFixedRate(SliderTimerTask(), 1500, 2000)
        })
    }

    inner class SliderTimerTask : TimerTask()
    {
        override fun run() {
            runOnUiThread {
                if (viewPager.currentItem == sliderAdapter.getListCount() - 1)
                    viewPager.currentItem = 0
                else
                    viewPager.currentItem++
            }
        }

    }
}
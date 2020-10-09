package ge.mov.mobile.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.ump.*
import ge.mov.mobile.R
import ge.mov.mobile.adapter.GenreAdapter
import ge.mov.mobile.adapter.MovieAdapter
import ge.mov.mobile.adapter.SliderAdapter
import ge.mov.mobile.databinding.ActivityMainBinding
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.ui.viewmodel.MainActivityViewModel
import ge.mov.mobile.ui.fragment.SearchFragment
import ge.mov.mobile.util.Utils
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager
    lateinit var sliderAdapter: SliderAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var timer: Timer
    private lateinit var consentForm: ConsentForm
    private lateinit var consentInformation: ConsentInformation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myLang = Utils.loadLanguage(this)
        if (myLang != null) {
            Utils.saveLanguage(this, myLang)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

      //  loadConsentInfo()

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

            if(!it.data.isNullOrEmpty())
                binding.categories.adapter = GenreAdapter(it.data, this, 1)

            binding.categories.post {
                binding.progress.visibility = View.GONE
            }
        })

        vm.getMovies().observe(this, {
            binding.progress.visibility = View.VISIBLE

            if(!it.isNullOrEmpty())
                binding.movies.adapter =
                    MovieAdapter(applicationContext, (it as ArrayList<MovieModel>))

            binding.movies.post {
                binding.progress.visibility = View.GONE
            }
        })

        vm.getSeries().observe(this, {
            binding.progress.visibility = View.VISIBLE

            if(!it.isNullOrEmpty())
                binding.series.adapter = MovieAdapter(applicationContext, it as ArrayList<MovieModel>)

            binding.series.post {
                binding.progress.visibility = View.GONE
            }
        })

        vm.getSlides().observe(this, {
            sliderAdapter = SliderAdapter(applicationContext, it)
            binding.slider.pageMargin = 85
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

    private fun loadConsentInfo() {
        val params = ConsentRequestParameters.Builder().build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(this, params,
            {
                if (consentInformation.isConsentFormAvailable) {
                    UserMessagingPlatform.loadConsentForm(
                        this,
                        { consentForm ->
                            this@MainActivity.consentForm = consentForm

                            if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED)
                            {
                                consentForm.show(
                                    this
                                ) {
                                    loadConsentInfo()
                                }
                            }
                        }
                    ) {
                        // Handle the error
                    }
                }
            }, {
            //    toast(it.message)
            })
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

    override fun onDestroy() {
        super.onDestroy()

        Glide.get(this)
            .clearMemory()
    }
}
package ge.mov.mobile.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.listener.StateUpdatedListener
import com.google.android.ump.*
import ge.mov.mobile.R
import ge.mov.mobile.adapter.GenreAdapter
import ge.mov.mobile.adapter.MovieAdapter
import ge.mov.mobile.adapter.SliderAdapter
import ge.mov.mobile.databinding.ActivityMainBinding
import ge.mov.mobile.model.basic.Data
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.ui.viewmodel.MainActivityViewModel
import ge.mov.mobile.ui.fragment.SearchFragment
import ge.mov.mobile.util.Utils
import ge.mov.mobile.util.toast
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager
    lateinit var sliderAdapter: SliderAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var timer: Timer
    private lateinit var consentForm: ConsentForm
    private lateinit var consentInformation: ConsentInformation

    private val PLAY_STORE_FLEXIBE_UPDATE_REQUEST = 101
    private val PLAY_STORE_IMMEDIATE_UPDATE_REQUEST = 102

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

        checkForUpdate()

        binding.btnProfilePic.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        binding.btnSearchMovie.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.root_main, SearchFragment(), "null").addToBackStack(
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

        vm.getMovies().observe(this) {
            binding.progress.visibility = View.VISIBLE

            if(!it.isNullOrEmpty())
                binding.movies.adapter =
                    MovieAdapter(applicationContext, (it as ArrayList<Data>))

            binding.movies.post {
                binding.progress.visibility = View.GONE
            }
        }

        vm.getSeries().observe(this) {
            binding.progress.visibility = View.VISIBLE

            if(!it.isNullOrEmpty())
                binding.series.adapter = MovieAdapter(applicationContext, it as ArrayList<Data>)

            binding.series.post {
                binding.progress.visibility = View.GONE
            }
        }

        vm.getSlides().observe(this) {
            sliderAdapter = SliderAdapter(applicationContext, it)
            binding.slider.pageMargin = 85
            binding.slider.adapter = sliderAdapter

            timer = Timer()
            timer.scheduleAtFixedRate(SliderTimerTask(), 3500, 4000)
        }

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

    private fun listener(appUpdateManager: AppUpdateManager): InstallStateUpdatedListener? = InstallStateUpdatedListener { installState ->
        if (installState.installStatus() == com.google.android.play.core.install.model.InstallStatus.DOWNLOADED) {
            appUpdateManager.completeUpdate()
            appUpdateManager.unregisterListener(listener(appUpdateManager))
        }
    }

    private fun checkForUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if(appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    appUpdateManager.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        AppUpdateType.IMMEDIATE,
                        // The current activity making the update request.
                        this,
                        // Include a request code to later monitor this update request.
                        PLAY_STORE_IMMEDIATE_UPDATE_REQUEST)
                } else {
                    appUpdateManager.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        AppUpdateType.FLEXIBLE,
                        // The current activity making the update request.
                        this,
                        // Include a request code to later monitor this update request.
                        PLAY_STORE_FLEXIBE_UPDATE_REQUEST)
                }
            }
        }
        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(listener(appUpdateManager))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLAY_STORE_FLEXIBE_UPDATE_REQUEST) {
            if (resultCode != Activity.RESULT_OK) {
                toast("Error occurred during the update, try again later!")
            }
        }
    }
}
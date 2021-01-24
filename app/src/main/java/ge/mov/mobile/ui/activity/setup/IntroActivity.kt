package ge.mov.mobile.ui.activity.setup

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ge.mov.mobile.R
import ge.mov.mobile.ui.adapter.introduction.IntroAdapter
import ge.mov.mobile.databinding.ActivityIntroBinding
import ge.mov.mobile.data.model.IntroModel
import ge.mov.mobile.ui.activity.main.MainActivity
import ge.mov.mobile.util.Utils

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private var arr = ArrayList<IntroModel>()
    private lateinit var adapter: IntroAdapter
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        binding.lifecycleOwner = this

        firstPageVisible(true)
        lastPageVisible(false)
        viewPagerVisible(false)
        binding.indicator.setupWithViewPager(binding.viewPagerIntro)

        populateListView()

        binding.buttonGetStarted.setOnClickListener {
            firstPageVisible(false)
            lastPageVisible(false)
            viewPagerVisible(true)
        }

        binding.buttonNextTutorial.setOnClickListener {
            position = binding.viewPagerIntro.currentItem
            if (position < arr.size) {
                position++
                binding.viewPagerIntro.currentItem = position
            }

            if (position == arr.size) {
                firstPageVisible(false)
                viewPagerVisible(false)
                lastPageVisible(true)
            }
        }

        binding.finishButton.setOnClickListener {
            Utils.saveSetup(this)
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun firstPageVisible(isVisible: Boolean) {
        binding.greetingContainer.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun lastPageVisible(isVisible: Boolean) {
        val visible = if (isVisible) View.VISIBLE else View.GONE

        binding.finishedContainer.visibility = visible

        if (isVisible)
            binding.buttonNextTutorial.visibility = View.GONE
        else
            binding.buttonNextTutorial.visibility = View.VISIBLE
    }

    private fun viewPagerVisible(isVisible: Boolean) {
        val visible = if (isVisible) View.VISIBLE else View.GONE
        binding.viewPagerIntro.visibility = visible
        binding.bottomContainer.visibility = visible
    }

    private fun populateListView() {
        arr.add(
            IntroModel(
                R.drawable.movies_intro,
                this.getString(R.string.movies_and_series),
                this.getString(R.string.movies_and_series_intro_text)
            )
        )

        arr.add(
            IntroModel(
                R.drawable.play_movie_intro,
                this.getString(R.string.play_movie),
                this.getString(R.string.play_movie_intro_text)
            )
        )

        arr.add(
            IntroModel(
                R.drawable.ic_heart_outline,
                this.getString(R.string.saving_movie),
                this.getString(R.string.saving_movie_intro_text)
            )
        )

        arr.add(
            IntroModel(
                R.drawable.settings_intro,
                this.getString(R.string.settings),
                this.getString(R.string.settings_intro_text)
            )
        )

        adapter = IntroAdapter(this, arr)
        binding.viewPagerIntro.adapter = adapter
    }
}
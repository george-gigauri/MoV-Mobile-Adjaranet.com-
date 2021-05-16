package ge.mov.mobile.ui.activity.settings

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import ge.mov.mobile.BuildConfig
import ge.mov.mobile.R
import ge.mov.mobile.databinding.ActivitySettingsBinding
import ge.mov.mobile.extension.setPreferredColor
import ge.mov.mobile.ui.activity.base.BaseActivity
import ge.mov.mobile.ui.activity.main.MainActivity
import ge.mov.mobile.ui.activity.offline.DownloadedMoviesActivity
import ge.mov.mobile.ui.fragment.settings.FragmentDeveloper
import ge.mov.mobile.util.Constants.AVAILABLE_LANGUAGES
import ge.mov.mobile.util.Constants.POPUP_STYLES
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_movie.*

@AndroidEntryPoint
class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {

    private val vm: ActivitySettingsViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override val bindingFactory: (LayoutInflater) -> ActivitySettingsBinding =
        { ActivitySettingsBinding.inflate(it) }

    override fun setup(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        onSeekParameterChanged()
        loadSpinners()
        spinnerListeners()
        onClickListeners()
        onSeekParameterChanged()

        binding.themePicker.setColorFilter(setPreferredColor(binding.root))

        binding.appVersion.text = BuildConfig.VERSION_NAME

        vm.getDownloadedMoviesCount().observe(this) {
            binding.downloadedMovieCount.text = it.toString()
        }
    }


    private fun spinnerListeners() {
        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selected = binding.languageSpinner.selectedItem.toString()

                for (i in AVAILABLE_LANGUAGES) {
                    if (i.name == selected) {
                        Utils.saveLanguage(this@SettingsActivity, i)
                        break
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }
        binding.popupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val editor = sharedPreferences.edit()
                editor.putInt("popup_style", position)
                editor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun onClickListeners() {
        binding.goback.setOnClickListener {
            binding.languageSpinner.onItemSelectedListener = null
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.themePicker.setOnClickListener {
            ColorSheet().colorPicker(
                colors = intArrayOf(
                    resources.getColor(R.color.colorPrimaryDark),
                    Color.BLACK,
                    Color.parseColor("#242424"),
                    Color.parseColor("#162324")
                ),
                noColorOption = true,
                listener = { color ->
                    logger.logThemeChanged(ColorSheetUtils.colorToHex(color))

                    if (color != ColorSheet.NO_COLOR) {
                        savePreferredColor(color)
                        binding.themePicker.setColorFilter(setPreferredColor(binding.settingsRoot))
                    } else {
                        savePreferredColor(resources.getColor(R.color.colorPrimaryDark))
                        binding.themePicker.setColorFilter(setPreferredColor(binding.settingsRoot))
                    }
                }
            ).show(supportFragmentManager)
        }

        binding.favs.setOnClickListener {
            val intent = Intent(applicationContext, SavedMoviesFragment::class.java)
            startActivity(intent)
        }

        binding.downloads.setOnClickListener {
            val intent = Intent(applicationContext, DownloadedMoviesActivity::class.java)
            startActivity(intent)
        }

        binding.developerInfo.setOnClickListener {
            logger.logDeveloperProfileClicked()
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_root, FragmentDeveloper(), "dev").addToBackStack("dev")
                .commit()
        }

        binding.plusInterval.setOnClickListener { onIntervalPlusClick() }
        binding.minusInterval.setOnClickListener { onIntervalMinusClick() }
    }

    private fun loadSpinners() {
        val langs: ArrayList<String> = ArrayList()
        AVAILABLE_LANGUAGES.forEach {
            langs.add(it.name)
        }
        binding.languageSpinner.adapter = ArrayAdapter(applicationContext, R.layout.spinner_white_text, langs)

        val currentLanguage = Utils.loadLanguage(applicationContext)
        for (i in 0 until langs.size) {
            if (currentLanguage.name == langs[i]) {
                binding.languageSpinner.setSelection(i)
                break
            }
        }

        binding.popupSpinner.adapter =
            ArrayAdapter(applicationContext, R.layout.spinner_white_text, POPUP_STYLES)

        val popup = sharedPreferences.getInt("popup_style", 1)
        binding.popupSpinner.setSelection(popup)
    }

    private fun savePreferredColor(color: Int) {
        val editor = getSharedPreferences("AppPreferences", MODE_PRIVATE).edit()
        editor.putInt("color", color)
        editor.apply()
    }

    private fun onSeekParameterChanged() {
        binding.intervalValue.text =
            (sharedPreferences.getLong("seek_interval", 5000) / 1000).toString()
    }

    private fun onIntervalPlusClick() {
        val current = (sharedPreferences.getLong("seek_interval", 5000) / 1000).toInt()
        binding.intervalValue.text = (current + 1).toString()
        saveIntervalSetting()
    }

    private fun onIntervalMinusClick() {
        val current = (sharedPreferences.getLong("seek_interval", 5000) / 1000).toInt()

        if (current > 5) {
            binding.intervalValue.text = (current - 1).toString()
            saveIntervalSetting()
        }
    }

    private fun saveIntervalSetting() {
        val newVal = binding.intervalValue.text.toString()
        val editor = sharedPreferences.edit()
        editor.putLong("seek_interval", "${newVal}000".toLong())
        editor.apply()
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        binding.languageSpinner.onItemSelectedListener = null

        if (count == 0) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()

        binding.savedMoviesCount.text = vm.getMoviesCount(this)
    }
}
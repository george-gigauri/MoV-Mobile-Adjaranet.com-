package ge.mov.mobile.ui.activity.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ge.mov.mobile.R
import ge.mov.mobile.databinding.ActivitySettingsBinding
import ge.mov.mobile.ui.activity.main.MainActivity
import ge.mov.mobile.ui.fragment.settings.FragmentDeveloper
import ge.mov.mobile.util.Constants.AVAILABLE_LANGUAGES
import ge.mov.mobile.util.Constants.POPUP_STYLES
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_movie.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var vm: ActivitySettingsViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        vm = ViewModelProvider(this)[ActivitySettingsViewModel::class.java]
        binding.lifecycleOwner = this
        binding.settings = vm

        loadSpinners()
        spinnerListeners()
        onClickListeners()
        onSeekParameterChanged()
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

        binding.favs.setOnClickListener {
            val intent = Intent(applicationContext, SavedMoviesFragment::class.java)
            startActivity(intent)
        }

        binding.developerInfo.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.settings_root, FragmentDeveloper(), "dev").addToBackStack("dev").commit()
        }
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

    private fun onSeekParameterChanged() {
        binding.editTextForwardInterval.apply {
            setText((sharedPreferences.getLong("seek_interval", 5000) / 1000).toString())
            doAfterTextChanged {
                if (!text.isNullOrEmpty() and !text.isNullOrBlank() and text.isDigitsOnly()) {
                    val editor = sharedPreferences.edit()
                    editor.putLong("seek_interval", "${text}000".toLong())
                    editor.apply()
                }
            }
        }
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
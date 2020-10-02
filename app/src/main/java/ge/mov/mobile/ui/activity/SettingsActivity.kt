package ge.mov.mobile.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import ge.mov.mobile.R
import ge.mov.mobile.databinding.ActivitySettingsBinding
import ge.mov.mobile.ui.fragment.FragmentDeveloper
import ge.mov.mobile.ui.fragment.SavedMoviesFragment
import ge.mov.mobile.ui.viewmodel.ActivitySettingsViewModel
import ge.mov.mobile.util.Constants.AVAILABLE_LANGUAGES
import ge.mov.mobile.util.Utils

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var vm: ActivitySettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        vm = ViewModelProviders.of(this).get(ActivitySettingsViewModel::class.java)
        binding.lifecycleOwner = this
        binding.settings = vm

        val langs: ArrayList<String> = ArrayList()
        AVAILABLE_LANGUAGES.forEach {
            langs.add(it.name)
        }

        binding.languageSpinner.adapter = ArrayAdapter(applicationContext, R.layout.spinner_white_text, langs)

        val currentLanguage = Utils.loadLanguage(this)
        for (i in 0 until langs.size) {
            if (currentLanguage?.name == langs[i]) {
                binding.languageSpinner.setSelection(i)
                break
            }
        }

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

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selected = binding.languageSpinner.selectedItem.toString()

                for (i in AVAILABLE_LANGUAGES) {
                    if (i.name == selected) {
                        Utils.saveLanguage(applicationContext, i)
                        break
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { }
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
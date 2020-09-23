package ge.mov.mobile.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ge.mov.mobile.R
import ge.mov.mobile.databinding.ActivitySettingsBinding
import ge.mov.mobile.model.LocaleModel
import ge.mov.mobile.ui.fragment.SavedMoviesFragment
import ge.mov.mobile.ui.viewmodel.ActivitySettingsViewModel
import ge.mov.mobile.util.Constants.AVAILABLE_LANGUAGES
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.activity_settings.*

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

        binding.languageSpinner.adapter = ArrayAdapter(this, R.layout.spinner_white_text, langs)

        val currentLanguage = Utils.loadLanguage(this)
        //val position: Int
        for (i in 0 until langs.size) {
            if (currentLanguage?.name == langs[i]) {
                binding.languageSpinner.setSelection(i)
                break
            }
        }

        binding.goback.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        binding.favs.setOnClickListener {
         /*   supportFragmentManager.beginTransaction().add(
                R.id.settings_root,
                SavedMoviesFragment(),
                "null").addToBackStack("Saved").commit() */
            val intent = Intent(this, SavedMoviesFragment::class.java)
            startActivity(intent)
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

        if (count == 0) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
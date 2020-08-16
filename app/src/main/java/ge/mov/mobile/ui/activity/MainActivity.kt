package ge.mov.mobile.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ge.mov.mobile.R
import ge.mov.mobile.adapter.MovieAdapter
import ge.mov.mobile.ui.activity.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val vm = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
//        movies.adapter = MovieAdapter(applicationContext, vm.getMovies().value!!)
        vm.getMovies().observe(this, Observer {
            movies.adapter = MovieAdapter(applicationContext, it)

            Log.i("OKMyValue", it[0].toString())
        })
    }
}
package ge.mov.mobile.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.ads.InterstitialAd
import ge.mov.mobile.ui.fragment.MovieCastFragment
import ge.mov.mobile.ui.fragment.SimilarMoviesFragment

class MoviePagerAdapter(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle,
    private val id: Int,
    private val adjaraId: Int,
    private val ad: InterstitialAd
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    val similarMoviesFragment = SimilarMoviesFragment.getInstance(id, adjaraId)

    private val fragments =
        arrayListOf(similarMoviesFragment, MovieCastFragment(id))

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
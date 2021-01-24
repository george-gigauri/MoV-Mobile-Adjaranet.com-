package ge.mov.mobile.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.ads.InterstitialAd
import ge.mov.mobile.ui.fragment.movie.MovieCastFragment
import ge.mov.mobile.ui.fragment.movie.SimilarMoviesFragment

class MoviePagerAdapter(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle,
    private val id: Int,
    private val adjaraId: Int,
    private val ad: InterstitialAd
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragments = arrayListOf(SimilarMoviesFragment(id, adjaraId, ad), MovieCastFragment(id))

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
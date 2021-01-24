package ge.mov.mobile.util

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import ge.mov.mobile.BuildConfig
import ge.mov.mobile.R
import ge.mov.mobile.data.model.Series.Episode
import ge.mov.mobile.data.model.Series.EpisodeFiles
import ge.mov.mobile.data.model.movie.Seasons
import kotlin.reflect.typeOf

fun d(tag: String, msg: String) {
    Log.d(tag, msg)
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun ImageView.loadWithProgressBar(progressBar: ProgressBar?, url: String?) {
    this.load(url) {
        placeholder(R.color.colorPrimaryDark)
        error(R.color.colorAccent)
        diskCachePolicy(CachePolicy.DISABLED)
        memoryCachePolicy(CachePolicy.DISABLED)
        listener(object : ImageRequest.Listener {
            override fun onCancel(request: ImageRequest) {
                super.onCancel(request)

                progressBar?.visibility = View.GONE
            }

            override fun onError(request: ImageRequest, throwable: Throwable) {
                super.onError(request, throwable)

                progressBar?.visibility = View.GONE
            }

            override fun onStart(request: ImageRequest) {
                super.onStart(request)

                progressBar?.visibility = View.VISIBLE
            }

            override fun onSuccess(request: ImageRequest, metadata: ImageResult.Metadata) {
                super.onSuccess(request, metadata)

                progressBar?.visibility = View.GONE
            }
        })
    }

  /**  Glide.with(context!!)
        .asDrawable()
        .load(url)
        .into(this) **/
}

fun ImageView.loadWithAnimationAndProgressBar(progressBar: ProgressBar, url: String, milliseconds: Int) {
    this.load(url) {
        placeholder(R.color.colorPrimaryDark)
        error(R.color.colorAccent)
       // crossfade(true, milliseconds)
        crossfade(milliseconds)
        diskCachePolicy(CachePolicy.DISABLED)
        memoryCachePolicy(CachePolicy.DISABLED)
        listener(object : ImageRequest.Listener {
            override fun onCancel(request: ImageRequest) {
                super.onCancel(request)

                progressBar.visibility = View.GONE
            }

            override fun onError(request: ImageRequest, throwable: Throwable) {
                super.onError(request, throwable)

                progressBar.visibility = View.GONE
            }

            override fun onStart(request: ImageRequest) {
                super.onStart(request)

                progressBar.visibility = View.VISIBLE
            }

            override fun onSuccess(request: ImageRequest, metadata: ImageResult.Metadata) {
                super.onSuccess(request, metadata)

                progressBar.visibility = View.GONE
            }
        })
    }
}

fun ProgressBar.visible(isVisible: Boolean) {
    this.visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun FrameLayout.visible(isVisible: Boolean) {
    this.visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun View.visible(isVisible: Boolean) {
    this.visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun Spinner.adapt(obj: Any) {
    this.adapter = when (obj) {
        is List<*> -> ArrayAdapter(this.context, R.layout.spinner_item_view, obj)
        is EpisodeFiles -> ArrayAdapter(this.context, R.layout.spinner_item_view, getEpisodesAsString(obj))
        else -> null
    }
}

private fun getEpisodesAsString(episodeFiles: EpisodeFiles): List<String> {
    val temp = ArrayList<String>()
    episodeFiles.data.forEach {
        temp.add("${it.episode} - ${it.title}")
    }
    return temp
}

fun Activity.loadAd(): InterstitialAd {
    val mIntestitialAd = InterstitialAd(this)
    mIntestitialAd.adUnitId = BuildConfig.ADMOB_ID_INTERSTITIAL

    if (!Constants.approved)
        mIntestitialAd.loadAd(AdRequest.Builder().build())
    return mIntestitialAd
}

fun Context.showAd(ad: InterstitialAd) {
    if (!Constants.approved && ad.isLoaded)
        ad.show()
    ad.loadAd(AdRequest.Builder().build())
}

fun Activity.drawable(src: Int) : Drawable? = ContextCompat.getDrawable(this, src)

fun Fragment.drawable(src: Int) : Drawable? = ContextCompat.getDrawable(requireActivity(), src)
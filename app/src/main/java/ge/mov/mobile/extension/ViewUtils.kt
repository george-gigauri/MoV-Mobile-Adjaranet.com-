package ge.mov.mobile.extension

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import ge.mov.mobile.BuildConfig
import ge.mov.mobile.R
import ge.mov.mobile.util.Constants
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat


fun d(tag: String, msg: String) {
    Log.d(tag, msg)
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
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

fun Spinner.adapt(obj: List<Any>) {
    adapter = ArrayAdapter(context, R.layout.spinner_item_view, obj)
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

fun Activity.toBitmap(url: String?): Bitmap {
    return try {
        val _url = URL(url)
        val connection = _url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val inputStream = connection.inputStream

        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        Log.i("toBitmap()", e.message.toString())
        e.printStackTrace()
        BitmapFactory.decodeResource(resources, R.drawable.backgrund_category)
    }
}

fun Activity.drawable(src: Int): Drawable? = ContextCompat.getDrawable(this, src)

fun Fragment.drawable(src: Int): Drawable? = ContextCompat.getDrawable(requireActivity(), src)

fun Context.download(url: String?): Long? {
    if (url != null || url != "") {
        val path = File(filesDir, "Downloads")
        if (!path.exists())
            path.mkdirs()

        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDestinationUri(path.toUri())
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setTitle("Downloading...")

        return dm.enqueue(request)
    }
    return null
}

fun Context.startFuckingService(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        startForegroundService(intent)
    else startService(intent)
}

fun Activity.setPreferredColor(view: View): Int {
    val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
    val color = sharedPreferences.getInt("color", 0)

    return if (color == 0) {
        setStatusBarColor(color)
        view.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        resources.getColor(R.color.colorPrimaryDark)
    } else {
        setStatusBarColor(color)
        view.setBackgroundColor(color)
        color
    }
}

fun Fragment.setPreferredColor(view: View) {
    val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE)
    val color = sharedPreferences.getInt("color", 0)

    if (color == 0) {
        view.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
    } else {
        view.setBackgroundColor(color)
    }
}

fun Activity.setStatusBarColor(color: Int) {
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.parseColor(ColorSheetUtils.colorToHex(color))
}

fun String.toBdayDate(): Long {
    val sdf = SimpleDateFormat("dd-MM-yyyy").parse(this)
    return sdf?.time ?: System.currentTimeMillis()
}
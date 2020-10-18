package ge.mov.mobile.util

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.bumptech.glide.Glide
import ge.mov.mobile.R

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
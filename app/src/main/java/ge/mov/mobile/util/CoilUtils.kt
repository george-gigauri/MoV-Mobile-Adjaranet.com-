package ge.mov.mobile.util

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import ge.mov.mobile.R

object CoilUtils {
    fun loadWithProgressBar(
        imageView: ImageView,
        progressBar: ProgressBar,
        url: String) {
        imageView.load(url) {
            placeholder(R.color.colorPrimaryDark)
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

    fun loadWithAnimationAndProgressBar(
        imageView: ImageView,
        progressBar: ProgressBar,
        url: String,
        milliseconds: Int) {
        imageView.load(url) {
            placeholder(R.color.colorPrimaryDark)
            crossfade(true)
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
}
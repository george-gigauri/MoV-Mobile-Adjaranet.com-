package ge.mov.mobile.util

import android.content.Context
import android.view.View
import android.widget.Toast

fun Context.toast(msg: String)
{
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
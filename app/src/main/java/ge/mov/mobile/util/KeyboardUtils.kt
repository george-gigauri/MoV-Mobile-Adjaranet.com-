package ge.mov.mobile.util

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager


object KeyboardUtils {
    fun open(context: Context) {
        val inputMethodManager: InputMethodManager? =
            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    fun close(context: Context) {
        val inputMethodManager: InputMethodManager? =
            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.toggleSoftInput(
            InputMethodManager.HIDE_IMPLICIT_ONLY,
            0
        )
    }
}
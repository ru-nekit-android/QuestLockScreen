package ru.nekit.android.utils

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardHost {

    fun hideKeyboard(context: Context, input: View, delay: Long) = hideKeyboard(context, input, delay, {})

    fun hideKeyboard(context: Context, input: View, delay: Long, body: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            val token = input.windowToken
            imm.hideSoftInputFromWindow(token, 0)
            body()
        }, delay)
    }

    fun showKeyboard(context: Context, input: View, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            input.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(input, InputMethodManager.SHOW_FORCED)
        }, delay)
    }
}

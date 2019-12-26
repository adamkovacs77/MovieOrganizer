package com.example.movieorganizer

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.inputmethod.InputMethodManager
import java.lang.Exception

class AppUtils {

    companion object {
        fun hideSoftKeyboard(activity: Activity) {
            val inputMethodManager =
                activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE
                ) as InputMethodManager

            try {
                inputMethodManager?.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
            catch (e: Exception) {

            }
        }

        fun checkNetworkState(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
            return false
        }
    }
}
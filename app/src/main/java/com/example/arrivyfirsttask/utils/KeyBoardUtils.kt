package com.example.arrivyfirsttask.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

object KeyBoardUtils {
    fun Activity.hideKeyboard() {
        // Get the current focus view
        val view = currentFocus
        // If no view currently has focus, create a new one
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

}
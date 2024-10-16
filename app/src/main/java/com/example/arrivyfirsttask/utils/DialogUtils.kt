package com.example.arrivyfirsttask.utils

import android.app.AlertDialog
import android.content.Context

object DialogUtils {
    fun showErrorDialog(context: Context, errorMessage: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(errorMessage)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // Close the dialog when "OK" is pressed
        }
        builder.setCancelable(false) // Prevent user from dismissing the dialog by touching outside
        val dialog = builder.create()
        dialog.show()
    }
}
package com.vk.vsvans.BlogShop.view.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import com.vk.vsvans.BlogShop.databinding.ProgressDialogLayoutBinding
import com.vk.vsvans.BlogShop.view.MainActivity

object ProgressDialog {

    fun createProgressDialog(activity: Activity):AlertDialog{
        val builder= AlertDialog.Builder(activity)
        val rootDialogElement= ProgressDialogLayoutBinding.inflate(activity.layoutInflater)
        // view= constraintlayout из sign_dialog
        val view=rootDialogElement.root

        builder.setView(view)

        val dialog =builder.create()

        dialog.setCancelable(false)

        dialog.show()
        return dialog
    }


}
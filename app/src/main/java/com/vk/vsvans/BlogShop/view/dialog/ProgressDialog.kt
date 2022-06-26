package com.vk.vsvans.BlogShop.view.dialog

import android.app.Activity
import android.app.AlertDialog
import com.vk.vsvans.BlogShop.databinding.ProgressDialogLayoutBinding

object ProgressDialog {

    fun createProgressDialog(act: Activity):AlertDialog{
        val builder= AlertDialog.Builder(act)
        val rootDialogElement= ProgressDialogLayoutBinding.inflate(act.layoutInflater)
        // view= constraintlayout из sign_dialog
        val view=rootDialogElement.root

        builder.setView(view)

        val dialog =builder.create()

        dialog.setCancelable(false)

        dialog.show()
        return dialog
    }


}
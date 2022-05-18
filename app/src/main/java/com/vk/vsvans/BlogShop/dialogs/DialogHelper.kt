package com.vk.vsvans.BlogShop.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.vk.vsvans.BlogShop.activity.EditPurchaseActivity
import com.vk.vsvans.BlogShop.interfaces.IUpdatePurchaseItemList
import com.vk.vsvans.BlogShop.mainActivity
import com.vk.vsvans.BlogShop.model.DbManager
import com.vk.vsvans.BlogShop.model.PurchaseItem
import com.vk.vsvans.BlogShop.R as R1

object DialogHelper {
    fun showPurchaseDeleteItemDialog(db: DbManager, id:Int) {
        val alertDialog = AlertDialog.Builder(db.context)

        alertDialog.apply {
            setIcon(R1.drawable.ic_delete_purchase)
            setTitle(R1.string.delete_item)
            setMessage(R1.string.delete_item_message)

            setNegativeButton(R1.string.cancel_dialog) { _, _ ->
            }

            setPositiveButton(R1.string.confirm_dialog) { _, _ ->
                db.removePurchaseFromDb(id)
                if(mainActivity!=null) mainActivity!!.fillAdapter("")
            }
//            setNeutralButton("Neutral") { _, _ ->
//                toast("clicked neutral button")
//            }
        }.create()//.show()
        val negative=alertDialog.show().getButton(AlertDialog.BUTTON_NEGATIVE)
        negative.apply {
            setFocusable(true)
            setFocusableInTouchMode(true)
            requestFocus()
        }

            //alertDialog.show().getButton(DialogInterface.BUTTON_NEGATIVE).requestFocus()
    }

    fun showPurchaseItemInputDialog(context: Context, pit:PurchaseItem,iupdatePurchaseItemList: IUpdatePurchaseItemList) {
        val customDialog = AlertDialog.Builder(context, 0).create()
        val inflater: LayoutInflater =(context as EditPurchaseActivity).layoutInflater

        val view: View = inflater.inflate(R1.layout.input_purchase_item, null)
        customDialog.setView(view)
        val rootView=view.rootView

        val btnOk=rootView.findViewById<Button>(R1.id.btnOk)
        btnOk.setOnClickListener {
            pit.price=(rootView.findViewById<EditText>(R1.id.edPriceItem).text.toString()).toDouble()
            pit.quantity=(rootView.findViewById<EditText>(R1.id.edQuantityItem).text.toString()).toDouble()
            pit.summa=(rootView.findViewById<EditText>(R1.id.edSummaItem).text.toString()).toDouble()
           // (context as EditPurchaseActivity).up
            if(iupdatePurchaseItemList!=null)iupdatePurchaseItemList.onUpdatePurchaseItemList(pit)
            customDialog.dismiss()
        }

        val btnCancel=view.rootView.findViewById<Button>(R1.id.btnCancel)
        btnCancel.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()


    }
}
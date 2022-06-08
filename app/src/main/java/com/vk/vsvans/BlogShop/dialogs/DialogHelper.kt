package com.vk.vsvans.BlogShop.dialogs

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.vk.vsvans.BlogShop.activity.EditPurchaseActivity
import com.vk.vsvans.BlogShop.activity.ProductActivity
import com.vk.vsvans.BlogShop.helper.SpinnerHelper
import com.vk.vsvans.BlogShop.interfaces.IDeleteItem
import com.vk.vsvans.BlogShop.interfaces.IDialogListener
import com.vk.vsvans.BlogShop.interfaces.IUpdateProductItemList
import com.vk.vsvans.BlogShop.interfaces.IUpdatePurchaseItemList
import com.vk.vsvans.BlogShop.model.Product
import com.vk.vsvans.BlogShop.model.PurchaseItem

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import com.vk.vsvans.BlogShop.R as R1


object DialogHelper {
    var job: Job? = null
    fun showPurchaseDeleteItemDialog(context: Context, id:Int, ideleteItem:IDeleteItem) {
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.apply {
            setIcon(R1.drawable.ic_delete_purchase)
            setTitle(R1.string.delete_item)
            setMessage(R1.string.delete_item_message)

            setNegativeButton(R1.string.no_dialog) { _, _ ->
            }

            setPositiveButton(R1.string.yes_dialog) { _, _ ->
                //db.removePurchase(id)
                if(ideleteItem!=null)ideleteItem.onDeleteItem(id)
                //if(mainActivity!=null) mainActivity!!.fillAdapter("")
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

        val edPrice=rootView.findViewById<EditText>(R1.id.edPriceItem)
        val edQuantity=rootView.findViewById<EditText>(R1.id.edQuantityItem)
        val edSumma=rootView.findViewById<EditText>(R1.id.edSummaItem)
        edPrice.setText(pit.price.toString())
        edQuantity.setText(pit.quantity.toString())
        edSumma.setText(pit.summa.toString())

        val tvProduct=rootView.findViewById<TextView>(R1.id.tvProduct)
        if(!pit.productName.isEmpty()){
            tvProduct.setText(pit.productName)
        }
        tvProduct.setOnClickListener {
            val dialog=DialogSpinnerHelper()
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch {
                val listProduct = SpinnerHelper.getAllProduct(context)
                dialog.showSpinnerProductDialog(context, listProduct, tvProduct)
            }
        }

        val btnOk=rootView.findViewById<Button>(R1.id.btnOk)
        btnOk.setOnClickListener {
            pit.price=edPrice.text.toString().toDouble()
            pit.quantity=edQuantity.text.toString().toDouble()
            pit.summa=edSumma.text.toString().toDouble()

            if(tvProduct.tag!=null) {
                val product=tvProduct.tag as Product
                pit.idProduct=product.id
                pit.productName=product.name
            }
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

    fun showProductInputDialog(context: Context, product: Product, iupdateProductItemList: IUpdateProductItemList) {
        val customDialog = AlertDialog.Builder(context, 0).create()
        val inflater: LayoutInflater =(context as ProductActivity).layoutInflater

        val view: View = inflater.inflate(R1.layout.input_product_item, null)
        customDialog.setView(view)
        val rootView=view.rootView

        val edTitle=rootView.findViewById<EditText>(R1.id.edTitleProduct)
        edTitle.setText(product.name.toString())

        val btnOk=rootView.findViewById<Button>(R1.id.btnOk)
        btnOk.setOnClickListener {
            // редактируем name, title не трогаем
            product.name=edTitle.text.toString()
            //product.title=edTitle.text.toString()//title синоним для поиска продуктов с чека фнс
            // (context as EditPurchaseActivity).up
            if(iupdateProductItemList!=null)iupdateProductItemList.onUpdateProductItemList(product)
            customDialog.dismiss()
        }

        val btnCancel=view.rootView.findViewById<Button>(R1.id.btnCancel)
        btnCancel.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()


    }
//
fun showSelectParentProductDialog(title: String?, activity: Activity,
//                                     tree: Product
//    excludeNode: Product?,
                                  eventsListener: IDialogListener?
) {
    val builder = AlertDialog.Builder(activity)
    val nodes: List<Product> =(activity as ProductActivity).adapter.productArray //tree.getFlatChildrenListWOChildrenOfGivenOne(excludeNode)
    builder.setTitle(title)
        .setNegativeButton(
            ( activity).resources.getString(R.string.cancel)
        ) { dialog, which -> dialog.dismiss() }
        .setAdapter(
            createTreeArrayAdapter(activity, nodes)
        ) { dialog, which ->
            if (eventsListener != null) {
                // which==0 this mean node is root
                eventsListener.onOkClick(
                    if(which==0)
                        null
                    else
                        nodes[which-1]) //nodes[which-1].position])
            }
        }
        .show()
}

private fun createTreeArrayAdapter(
    activity: Activity,
    nodes: List<Product>
): ArrayAdapter<String> {
    val arrayAdapter = ArrayAdapter<String>(activity, R.layout.simple_list_item_1)
    var i=0
    arrayAdapter.add("" + "─ "+"Root")//ascii 196
    for (node in nodes) {
        // исключим переносы из фнс для них поле title не пусто

 //       if(node.title.isEmpty()) {
            var prefix = "\t"
            for (i in 0 until node.level) {
                prefix = prefix + "\t\t"
            }
            if (!prefix.isEmpty()) {
                prefix = if (node.count == 0) {
                    "$prefix└ "
                } else {
                    "$prefix├ "
                }
            }
            arrayAdapter.add(prefix + node.name)
//        }//exclude not empt

        node.position=i++
    }
    return arrayAdapter
}
}
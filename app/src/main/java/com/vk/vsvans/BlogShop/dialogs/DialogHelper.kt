package com.vk.vsvans.BlogShop.dialogs

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.vk.vsvans.BlogShop.MainActivity
import com.vk.vsvans.BlogShop.activity.EditPurchaseActivity
import com.vk.vsvans.BlogShop.activity.ProductActivity
import com.vk.vsvans.BlogShop.adapters.PurchaseRcAdapter
import com.vk.vsvans.BlogShop.calendar.CalendarAlertDialog
import com.vk.vsvans.BlogShop.calendar.CalendarDialogAdapter
import com.vk.vsvans.BlogShop.helper.SpinnerHelper
import com.vk.vsvans.BlogShop.interfaces.*
import com.vk.vsvans.BlogShop.model.Product
import com.vk.vsvans.BlogShop.model.PurchaseItem
import com.vk.vsvans.BlogShop.utils.UtilsHelper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.String.valueOf
import java.util.*
import kotlin.collections.ArrayList
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
    val nodes: List<Product> =(activity as ProductActivity).adapter.productArray
    val filterNodes=ArrayList<Product>()
    builder.setTitle(title)
        .setNegativeButton(
            ( activity).resources.getString(R.string.cancel)
        ) { dialog, which -> dialog.dismiss() }
        .setAdapter(
            createTreeArrayAdapter(activity, nodes,filterNodes)
        ) { dialog, which ->
            if (eventsListener != null) {
                // which==0 this mean node is root
                eventsListener.onOkClick(
                    if(which==0)
                        null
                    else
                        filterNodes[which-1].id) //-1 because arrayAdapter[0] is "Root"
            }
        }
        .show()
}

private fun createTreeArrayAdapter(
    activity: Activity,
    nodes: List<Product>,
    filterNodes: ArrayList<Product>,
): ArrayAdapter<String> {
    val arrayAdapter = ArrayAdapter<String>(activity, R.layout.simple_list_item_1)
    var i=0
    arrayAdapter.add("" + "─ "+"Root")//ascii 196
    for (node in nodes) {
        // исключим переносы из фнс для них поле title не пусто
        if(node.title.isEmpty()) {
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
            filterNodes.add(node)
        }//exclude not empt

    }
    return arrayAdapter
}

    fun getCalendarDialog(activity: Activity) {
        val mainActivity: MainActivity = activity as MainActivity
        val adapter: PurchaseRcAdapter = mainActivity.adapter
        val selected_date = HashMap<String, Date?>()
        val mCalendar = CalendarAlertDialog(
            mainActivity,
            object : CalendarDialogAdapter.onItemClickListener {
                override fun onClick(date: Date) {
                    //to do
                    val key: String = UtilsHelper.getDate(date.time)
                    if (selected_date[key] == null) {
                        selected_date[key] = date
                    } else {
                        selected_date.remove(key)
                    }
                    Log.d("DATECLICK", date.toString())
                }
            }, 1
        )
        mCalendar.setOnClickOkListener(object : CalendarAlertDialog.onClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick() {
                if (selected_date.size != 0) {
                    val dates = ArrayList(selected_date.values)
                    Collections.sort(dates, object : Comparator<Date?> {

                        override fun compare(o1: Date?, o2: Date?): Int {
                            if (o1 != null) {
                                return o1.compareTo(o2)
                            }else return 0
                        }
                    })
                    val dates_begin = ArrayList<String>()
                    val dates_end = ArrayList<String>()
                    var str: String?
                    for (i in 0 until selected_date.size) {
                        str = valueOf(UtilsHelper.correct_date_begin(dates[i]!!.time))
                        dates_begin.add(str)
                        str = valueOf(UtilsHelper.correct_date_end(dates[i]!!.time))
                        dates_end.add(str)
                    }
                    val list=mainActivity.dbManager.getSelectedPurchases(dates_begin,dates_end)
                    mainActivity.adapter.updateAdapter(list)
                   // adapter.searchNote(dates_begin, dates_end)
                } else {
                   mainActivity.fillAdapter("")
                }
                mCalendar.dismiss()
            }
        })
        mCalendar.setOnClickCancelListener(object : CalendarAlertDialog.onClickListener {
            override fun onClick() {
                selected_date.clear()
                mCalendar.dismiss()
            }
        })
        mCalendar.show()
    }

}
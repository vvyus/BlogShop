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
import com.vk.vsvans.BlogShop.activity.EditPurchaseActivity
import com.vk.vsvans.BlogShop.activity.ProductActivity
import com.vk.vsvans.BlogShop.activity.SellerActivity
import com.vk.vsvans.BlogShop.calendar.CalendarAlertDialog
import com.vk.vsvans.BlogShop.calendar.CalendarDialogAdapter
import com.vk.vsvans.BlogShop.helper.SpinnerHelper
import com.vk.vsvans.BlogShop.interfaces.*
import com.vk.vsvans.BlogShop.model.BaseList
import com.vk.vsvans.BlogShop.model.Product
import com.vk.vsvans.BlogShop.model.PurchaseItem
import com.vk.vsvans.BlogShop.utils.FilterForActivity
import com.vk.vsvans.BlogShop.utils.UtilsHelper
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

    fun showBaseListInputDialog(context: Context, product: BaseList, iupdateBaseListItemList: IUpdateBaseListItemList) {
        val customDialog = AlertDialog.Builder(context, 0).create()
        var inflater: LayoutInflater?=null
        if(context is ProductActivity)
            inflater =(context as ProductActivity).layoutInflater
        else  inflater =(context as SellerActivity).layoutInflater
        val view: View = inflater.inflate(R1.layout.input_baselist_item, null)
        customDialog.setView(view)
        val rootView=view.rootView

        val edTitle=rootView.findViewById<EditText>(R1.id.edTitleBaselist)
        edTitle.setText(product.name.toString())

        val btnOk=rootView.findViewById<Button>(R1.id.btnOk)
        btnOk.setOnClickListener {
            // редактируем name, title не трогаем
            product.name=edTitle.text.toString()
            //product.title=edTitle.text.toString()//title синоним для поиска продуктов с чека фнс
            // (context as EditPurchaseActivity).up
            if(iupdateBaseListItemList!=null)iupdateBaseListItemList.onUpdateBaseListItemList(product)
            customDialog.dismiss()
        }

        val btnCancel=view.rootView.findViewById<Button>(R1.id.btnCancel)
        btnCancel.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()


    }
//
fun showSelectParentBaseListDialog(title: String?, activity: Activity,
//                                     tree: Product
//    excludeNode: Product?,
                                   eventsListener: IDialogListener?
) {
    val builder = AlertDialog.Builder(activity)
    var nodes: List<BaseList>? =null
    if(activity is ProductActivity)
        nodes =(activity as ProductActivity).adapter.BaseListArray
    else
        nodes =(activity as SellerActivity).adapter.BaseListArray
    val filterNodes=ArrayList<BaseList>()
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
    nodes: List<BaseList>,
    filterNodes: ArrayList<BaseList>,
): ArrayAdapter<String> {
    val arrayAdapter = ArrayAdapter<String>(activity, R.layout.simple_list_item_1)
    var i=0
    arrayAdapter.add("" + "─ "+"Root")//ascii 196
    for (node in nodes) {
        // исключим переносы из фнс для них поле title не пусто
        if(node.id_fns.isEmpty()) {
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

    fun getCalendarDialog(activity: Activity,iFilter:IDialogDateFiterCallback,filter:FilterForActivity,time:Long) {
       // val mainActivity: MainActivity = activity as MainActivity
        val selected_date = HashMap<String, Date?>()
        val mCalendar = CalendarAlertDialog(
            activity,//mainActivity,
            object : CalendarDialogAdapter.onItemClickListener {
                override fun onClick(date: Date) {
                    //to do
                    val key: String = UtilsHelper.getDate(date.time)
                    if (selected_date[key] == null) {
                        selected_date[key] = date
                    } else {
                        selected_date.remove(key)
                        Log.d("DATECLICK", date.toString())
                    }
                    //Log.d("DATECLICK", date.toString())
                }
            }, 1,Date(time)
        )
        mCalendar.setOnClickOkListener(object : CalendarAlertDialog.onClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick() {

                if (selected_date.size != 0) {
                    iFilter.confirmFilter(selected_date)
                } else {
                    // если нет выбранных дат то сброс фильтра
                    if(iFilter!=null) iFilter.cancelFilter()
                }
                mCalendar.dismiss()
            }
        })
        mCalendar.setOnClickCancelListener(object : CalendarAlertDialog.onClickListener {
            override fun onClick() {
                selected_date.clear()
                if(iFilter!=null) iFilter.cancelFilter()
                mCalendar.dismiss()
            }
        })
        mCalendar.show()
        // set prev selected stored date from filter
        if(filter.dates_begin!=null && filter.dates_begin!!.size>0){
            var key=""
            selected_date.clear()
            for(i in 0 until filter.dates_begin!!.size){
                key=filter.dates_begin!![i]
            //println("Key is :"+key) 1653782400000
                val date= key.toLong()
                mCalendar.setSelectedDate(date)
                if (selected_date[key] == null) {
                    selected_date[key] = Date(date)
                } else {
                    selected_date.remove(key)
                }
            }
        // no filter set
        } else {
            // current clicked date on list purchases is selected
            mCalendar.setSelectedDate(time)
            val key: String = UtilsHelper.getDate(time)
            if (selected_date[key] == null) {
                selected_date[key] = Date(time)
            } else {
                selected_date.remove(key)
            }
        }

    }

}
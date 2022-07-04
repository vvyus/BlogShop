package com.vk.vsvans.BlogShop.view.dialog

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vk.vsvans.BlogShop.calendar.CalendarAlertDialog
import com.vk.vsvans.BlogShop.calendar.CalendarDialogAdapter
import com.vk.vsvans.BlogShop.model.data.BaseList
import com.vk.vsvans.BlogShop.model.data.Product
import com.vk.vsvans.BlogShop.model.data.PurchaseItem
import com.vk.vsvans.BlogShop.util.FilterForActivity
import com.vk.vsvans.BlogShop.util.UtilsHelper
import com.vk.vsvans.BlogShop.view.EditPurchaseActivity
import com.vk.vsvans.BlogShop.view.MainActivity
import com.vk.vsvans.BlogShop.view.ProductActivity
import com.vk.vsvans.BlogShop.view.SellerActivity
import com.vk.vsvans.BlogShop.view.`interface`.*
import com.vk.vsvans.BlogShop.view.adapter.StringRcAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.String.valueOf
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
            }
        }.create()//.show()
        val negative=alertDialog.show().getButton(AlertDialog.BUTTON_NEGATIVE)
        negative.apply {
            setFocusable(true)
            setFocusableInTouchMode(true)
            requestFocus()
        }

    }

    fun showPurchaseItemInputDialog(context: Context, pit: PurchaseItem,listProduct:ArrayList<BaseList> ,iupdatePurchaseItemList: IUpdatePurchaseItemList) {
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
            val dialog= DialogSpinnerHelper()
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch {
                //val listProduct = dialog.getAllProduct(context)

                var idProduct=0
                if(tvProduct.tag==null || (tvProduct.tag as Product)==null) idProduct=pit!!.idProduct
                else idProduct=(tvProduct.tag as Product).id
                tvProduct.setTag(idProduct)
                dialog.showSpinnerDialog(context, listProduct, tvProduct)
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

    fun getCalendarDialog(activity: Activity, iFilter:IDialogDateFiterCallback, filter_fact:FilterForActivity, time:Long) {
        val mainActivity: MainActivity = activity as MainActivity
        val selected_date = HashMap<String, Date?>()
        val mCalendar = CalendarAlertDialog(
            activity,//mainActivity,
            object : CalendarDialogAdapter.onItemClickListener {
                override fun onClick(date: Date) {
                    //to do
                    val key: String =UtilsHelper.getDate(date.time)
                    if (selected_date[key] == null) {
                        selected_date[key] = date
                    } else {
                        selected_date.remove(key)
                        Log.d("DATECLICK", date.toString())
                    }
                    //Log.d("DATECLICK", date.toString())
                }
            }, 1,Date(time), mainActivity.calendar_events
        )
        mCalendar.setOnClickOkListener(object : CalendarAlertDialog.onClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick() {

                if (selected_date.size != 0) {
                    iFilter.confirmFilter(selected_date)
                } else {
                    // если нет выбранных дат то сброс фильтра
                    iFilter.cancelFilter()
                }
                selected_date.clear()
                mCalendar.dismiss()
            }
        })
        mCalendar.setOnClickCancelListener(object : CalendarAlertDialog.onClickListener {
            override fun onClick() {
                selected_date.clear()
                iFilter.cancelFilter()
                mCalendar.dismiss()
            }
        })
        mCalendar.show()
        mCalendar.getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        selected_date.clear()
        mCalendar.clearSelectedDate()
        // set prev selected stored date from filter
        if(filter_fact.dates_begin!=null && filter_fact.dates_begin!!.size>0){
            var key=""

            for(i in 0 until filter_fact.dates_begin!!.size){
                val date=filter_fact.dates_begin!![i]
            //println("Key is :"+key) 1653782400000
                val key=UtilsHelper.getDate(date.toLong()) //key.toLong()
                mCalendar.setSelectedDate(date.toLong())
                if (selected_date[key] == null) {
                    selected_date[key] = Date(date.toLong())
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
println("Ok")
    }

    fun showLoadChecksDialog(context: Context,idialog_import:IDialogImportChecks) {
        //val arrayAdapter = RecyclerView.Adapter<String>(context as MainActivity, R.layout.simple_list_item_1)
        val adapter:StringRcAdapter=StringRcAdapter()
        val load_selected_date=HashMap<String, Date?>()
        val filter_fact=FilterForActivity("")

        val alertDialog = AlertDialog.Builder(context).create()
        val inflater: LayoutInflater =(context as MainActivity).layoutInflater
        val view: View = inflater.inflate(R1.layout.import_checks_dialog, null)
        alertDialog.setView(view)
        val rootView=view.rootView

        val btnCancelCheckDialog=rootView.findViewById<Button>(R1.id.btnCancelCheckDialog)
        btnCancelCheckDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        val btnOkCheckDialog=rootView.findViewById<Button>(R1.id.btnOkCheckDialog)
        btnOkCheckDialog.setOnClickListener {
            idialog_import.import_checks(load_selected_date)
            alertDialog.dismiss()
        }

        val fabCheckDialogAdd=rootView.findViewById<FloatingActionButton>(R1.id.fabCheckDialogAdd)
        fabCheckDialogAdd.setOnClickListener {
            DialogHelper.getCalendarDialog(context,object: IDialogDateFiterCallback {
                override fun confirmFilter(selected_date: HashMap<String, Date?>) {
                    if (selected_date.size != 0) {
                        // выгружаем хэш в другой хэш для передачи в import_checks
                        load_selected_date.putAll(selected_date)
                        val dates = ArrayList(selected_date.values)
                        Collections.sort(dates, object : Comparator<Date?> {

                            override fun compare(o1: Date?, o2: Date?): Int {
                                if (o1 != null) {
                                    return o1.compareTo(o2)
                                }else return 0
                            }
                        })
                        println("Dates size is :"+dates.size+" selected date size is "+selected_date.size)
                        // для показа в rc
                        adapter.clear()
                        //filter_fact.dates_begin используется для показа дат при повторном заходе в календарь
                        filter_fact.dates_begin = ArrayList<String>()
                        var str: String?
                        for (i in 0 until dates.size) {
                            println("To filter Date is :"+dates[i])
                            str = valueOf(UtilsHelper.correct_date_begin(dates[i]!!.time))
                            filter_fact.dates_begin!!.add(str)
                            //to do
                            str = UtilsHelper.getDate(dates[i]!!.time)
                            adapter.add(str)
                        }

                        //fillAdapter()
                    }
                }

                override fun cancelFilter() {
                    filter_fact.dates_begin=null
                    adapter.clear()
                }
            },filter_fact,UtilsHelper.getCurrentDate())
        }
        val rvCheckDialog=rootView.findViewById<RecyclerView>(R1.id.rvCheckDialog)
        rvCheckDialog.layoutManager= LinearLayoutManager(context)

        rvCheckDialog.adapter=adapter
        //adapter.add("test")

        alertDialog.show()
        alertDialog.getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

    }

}
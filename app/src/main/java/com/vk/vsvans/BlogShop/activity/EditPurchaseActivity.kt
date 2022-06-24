package com.vk.vsvans.BlogShop.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import com.vk.vsvans.BlogShop.MainActivity
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.adapters.CardItemPurchaseRcAdapter
import com.vk.vsvans.BlogShop.databinding.ActivityEditPurchaseBinding
import com.vk.vsvans.BlogShop.dialogs.DialogHelper
import com.vk.vsvans.BlogShop.fragments.PurchaseItemListFragment
import com.vk.vsvans.BlogShop.interfaces.IFragmentCallBack
import com.vk.vsvans.BlogShop.interfaces.IFragmentCloseInterface
import com.vk.vsvans.BlogShop.interfaces.IUpdatePurchaseItemList
import com.vk.vsvans.BlogShop.mainActivity
import com.vk.vsvans.BlogShop.model.*
import com.vk.vsvans.BlogShop.spinner.DialogSpinnerHelper
import com.vk.vsvans.BlogShop.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.*
import java.util.*


class EditPurchaseActivity : AppCompatActivity() {

    lateinit var rootElement: ActivityEditPurchaseBinding
    //private val dialog= DialogSpinnerHelper()
    val listResultArray=ArrayList<String>()
    val TAG="MyLog"

    private lateinit var cardItemPurchaseAdapter:CardItemPurchaseRcAdapter
//    var options = Options()
    private var purchaseItemFragment:PurchaseItemListFragment?=null

    private var job: Job?=null
    // для работы с firebase
    //private lateinit var dbManager:DbManager
    val dbManager= DbManager(this)

    var idPurchase =0
    private var purchase:Purchase? = null
    private var listDeletedPurchaseItems=ArrayList<PurchaseItem>()

    //var content_temp:SpannableString="".makeSpannableString()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement= ActivityEditPurchaseBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        val intent=getIntent()
        idPurchase=intent.getIntExtra(R.string.PURCHASE_ID.toString(), 0)
        initPurchase()
        init()
        //initToolbar()
    }

    override fun onResume() {
        super.onResume()
        dbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
        job?.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initPurchase(){
        if(idPurchase>0) {
            dbManager.openDb()
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch{
                purchase=dbManager.readOnePurchase(idPurchase)
                if(purchase!=null){
                    rootElement.apply {
                        //content_temp=Html.fromHtml(purchase!!.content_html,0).makeSpannableString()
                        //edTitle.setText(purchase!!.title)
                        tvSellerSelect.text=purchase!!.sellername
                        edSummaPurchase.setText(purchase!!.summa.toString())
                        initDateTime()
                    }
                    val purchaseItems=dbManager.readPurchaseItems(idPurchase)
                    (rootElement.vpPurchaseItems.adapter as CardItemPurchaseRcAdapter).update(purchaseItems)
                }
            }
        }else {
            //idPurchase==0
            purchase= Purchase()
            if(purchase!=null){
                purchase!!.time=UtilsHelper.getCurrentDate()
                initDateTime()
            }
        }
    }

    private fun init(){
        cardItemPurchaseAdapter= CardItemPurchaseRcAdapter()
        rootElement.vpPurchaseItems.adapter=cardItemPurchaseAdapter
        rootElement.tvSellerSelect.setOnClickListener {
            val dialog= DialogSpinnerHelper()
            DialogHelper.job?.cancel()
            DialogHelper.job = CoroutineScope(Dispatchers.Main).launch {
                val listSeller = dialog.getAllSeller(this@EditPurchaseActivity)
                dialog.showSpinnerSellerDialog(this@EditPurchaseActivity, listSeller, rootElement.tvSellerSelect)
            }
        }
        if(idPurchase==0) rootElement.purchaseItemButton.visibility=View.VISIBLE
        else rootElement.purchaseItemButton.visibility=View.GONE
    }

    private fun initDateTime(){
        initDateTimeButtons()
        rootElement.edDatePart.setOnClickListener{
            val calendar: Calendar = Calendar.getInstance()
            calendar.setTime(Date(purchase!!.time))
            DatePickerDialog(
                this,
                { datePicker: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val calendar1: Calendar = Calendar.getInstance()
                    calendar1.setTime(Date(purchase!!.time))
                    calendar1.set(year, monthOfYear, dayOfMonth)
                    purchase!!.time=calendar1.getTimeInMillis()
                    initDateTimeButtons()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }
        rootElement.edTimePart.setOnClickListener{
            val calendar = Calendar.getInstance()
            calendar.time = Date(purchase!!.time)
            TimePickerDialog(
                this,
                { timePicker: TimePicker?, hourOfDay: Int, minute: Int ->
                    val calendar1 = Calendar.getInstance()
                    calendar1.time = Date(purchase!!.time)
                    calendar1[calendar1[Calendar.YEAR], calendar1[Calendar.MONTH], calendar1[Calendar.DAY_OF_MONTH], hourOfDay] =
                        minute
                    purchase!!.time=calendar1.getTimeInMillis()
                    initDateTimeButtons()
                },
                calendar[Calendar.HOUR_OF_DAY],
                calendar[Calendar.MINUTE],
                true
            ).show()
        }
    }

    private fun initDateTimeButtons(){
        //val mDateMediumFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        //
        val mDateMediumFormat = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        rootElement.edDatePart.setText(mDateMediumFormat.format(Date(purchase!!.time)))
        val mDateShortFormat =  SimpleDateFormat("HH:mm", Locale.getDefault())
        rootElement.edTimePart.setText(mDateShortFormat.format(Date(purchase!!.time)))
    }

    fun clearResultArray(){
        listResultArray.clear()
    }

    fun onClickSelectSeller(view:View){
//        val listCategory=resources.getStringArray(R.array.Category).toMutableList() as ArrayList
//        dialog.showSpinnerDialog(this,listCategory,rootElement.tvCategory)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickSavePurchase(view: View){
        var remove=true
        rootElement.apply {
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch{
               if(dbManager!=null) {
                   //здесь то что редактируется а не пришло из фрагмента
                   purchase!!.summa= edSummaPurchase.value.toDouble();//.text.toString().toDouble()
                   //purchase!!.title=edTitle.text.toString()
                   if(tvSellerSelect.tag!=null) {
                       val seller=tvSellerSelect.tag as Seller
                       purchase!!.idseller=seller.id
                       purchase!!.sellername=seller.name
                   }
                   val old_time=purchase!!.time
                   purchase!!.time= DateTimeUtils.parseDateTimeString(rootElement.edDatePart.text.toString()+" "+rootElement.edTimePart.text.toString())!!
                   if(idPurchase>0){
                       //dbManager.updatePurchase(idPurchase,edTitle.text.toString(),edDescription.text.toString())
                       dbManager.updatePurchase(purchase!!)
                   }else{
                       idPurchase= dbManager.insertPurchase(purchase!!)!!
                       purchase!!.id=idPurchase
                   }

                   // callback to main activity temporary!!!
                   //setResult()
                   val data = Intent()
                   data.putExtra(getString(R.string.old_purchase_time), old_time)
                   data.putExtra(getString(R.string.new_purchase_time), purchase!!.time)
                   setResult(RESULT_OK, data)
                   //
//                   if(old_time!=purchase!!.time) mainActivity!!.removePurchaseEvent(old_time)
//                   mainActivity!!.addPurchaseEvent(purchase!!.time)

                   for(pit:PurchaseItem in (vpPurchaseItems.adapter as CardItemPurchaseRcAdapter).mainArray){
                       if(pit.id==0){
                           //set idPurchase when new Purchase
                           pit.idPurchase=idPurchase
                           dbManager.insertPurchaseItem(pit)
                       }else{
                           dbManager.updatePurchaseItem(pit)
                       }
                   }
                   for(pit:PurchaseItem in listDeletedPurchaseItems){
                       dbManager.removePurchaseItem(pit.id)
                   }
                   listDeletedPurchaseItems=ArrayList<PurchaseItem>()
               }//dbManager

            }//job
            onBackPressed()
        }

//        dbManager.publishAd(fillAd())
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickCancelPurchase(view: View) {
        listDeletedPurchaseItems=ArrayList<PurchaseItem>()
        onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBackPressed() {
        //listDeletedPurchaseItems=ArrayList<PurchaseItem>()
        //if(mainActivity!=null)mainActivity!!.fillAdapter()
        super.onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onClickGetPurchaseItems(view:View){
        val newList=(rootElement.vpPurchaseItems.adapter as CardItemPurchaseRcAdapter).mainArray

        purchaseItemFragment= PurchaseItemListFragment(
            object: IFragmentCloseInterface {
            // при закрытии фрагмента
            override fun onFragClose(list: ArrayList<PurchaseItem>) {
                rootElement.scrollViewMain.visibility=View.VISIBLE
                cardItemPurchaseAdapter.update(list)
                var summa=0.0
                val title_color=getColor(R.color.green_main)
                var content_temp="".makeSpannableString()
                for(pit:PurchaseItem in list){
                    content_temp+=pit.getContentShort(title_color)+"\n\n"
                    summa+=pit.summa
                }
                purchase!!.content= content_temp.toString()
                purchase!!.content_html=Html.toHtml(content_temp,0)
                rootElement.edSummaPurchase.setText(summa.toString())
            }
        },
            object: IFragmentCallBack {
                override fun onFragmentCallBack(pit: PurchaseItem) {
                    //dbManager.removePurchaseItem(pit)
                    listDeletedPurchaseItems.add(pit)
                }
                                      },
            newList) //PurchaseItemListFragment
       // purchaseItemFragment!!.updateAdapter(cardItemPurchaseAdapter.mainArray)
        openPurchaseItemFragment()
    }

    private fun openPurchaseItemFragment() {
        if(purchaseItemFragment!=null){
            rootElement.scrollViewMain.visibility=View.GONE
            val fm=supportFragmentManager.beginTransaction()
            fm.replace(R.id.place_holder, purchaseItemFragment!!)
            fm.commit()
        }else {
            // если выбран один элемент после на кнопку,не из адаптера, фрагмент не создавался

        }
    }
// добавить новую позицию покупки
    fun onClickAddPurchaseItem(view: View){
        if(purchaseItemFragment!=null){
            val pit=PurchaseItem()
            //новая запись
            pit.id=0
            pit.idPurchase=idPurchase
            DialogHelper.showPurchaseItemInputDialog(this@EditPurchaseActivity,pit,
                object: IUpdatePurchaseItemList {
                    override fun onUpdatePurchaseItemList(pit: PurchaseItem) {
                        purchaseItemFragment!!.adapter.updateAdapterInsert(pit)
                    }

                }
            )
        }
    }


}//activity
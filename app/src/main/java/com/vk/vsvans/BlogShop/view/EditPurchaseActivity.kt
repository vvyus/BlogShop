package com.vk.vsvans.BlogShop.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.view.adapter.CardItemPurchaseRcAdapter
import com.vk.vsvans.BlogShop.databinding.ActivityEditPurchaseBinding
import com.vk.vsvans.BlogShop.model.data.*
import com.vk.vsvans.BlogShop.view.dialog.DialogHelper
import com.vk.vsvans.BlogShop.view.fragment.PurchaseItemListFragment
import com.vk.vsvans.BlogShop.view.`interface`.IFragmentCallBack
import com.vk.vsvans.BlogShop.view.`interface`.IFragmentCloseInterface
import com.vk.vsvans.BlogShop.view.`interface`.IUpdatePurchaseItemList
import com.vk.vsvans.BlogShop.view.dialog.DialogSpinnerHelper
import com.vk.vsvans.BlogShop.util.*
import com.vk.vsvans.BlogShop.viewmodel.ActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.*
import java.util.*
import kotlin.collections.ArrayList


class EditPurchaseActivity : AppCompatActivity() {

    lateinit var rootElement: ActivityEditPurchaseBinding
    //private val dialog= DialogSpinnerHelper()
    val listResultArray=ArrayList<String>()
    val TAG="MyLog"

    private lateinit var cardItemPurchaseAdapter: CardItemPurchaseRcAdapter
//    var options = Options()
    private var purchaseItemFragment: PurchaseItemListFragment?=null

    private var job: Job?=null
    // для работы с firebase
    //val dbManager= DbManager(this)
    var viewModel: ActivityViewModel=ActivityViewModel()//?=null

    var idPurchase =0
    private var purchase: Purchase? = null
    private var listDeletedPurchaseItems=ArrayList<PurchaseItem>()
//    var listProducts=ArrayList<Product>()
//    private var listSellers=ArrayList<Seller>()
    //var content_temp:SpannableString="".makeSpannableString()
    lateinit var launcherSeller: ActivityResultLauncher<Intent>
    lateinit var launcherProduct: ActivityResultLauncher<Intent>
    lateinit var pitDialog:AlertDialog

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel= ActivityViewModel(application)
        rootElement= ActivityEditPurchaseBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        val intent=getIntent()
        idPurchase=intent.getIntExtra(R.string.PURCHASE_ID.toString(), 0)
        initPurchase()
        init()
        initViewModel()
        //initToolbar()
    }

    private fun initViewModel(){
        //{} это слушатель
        //если наше activity доступно не разрушено или ждет когда можно обновить слушателт сработает
        // теперь списки для спиннера нам не нужны
//        viewModel.liveSellerList.observe(this,{
//            listSellers.clear()
//            listSellers.addAll(it)
//        })
//        viewModel.liveProductList.observe(this,{
//            listProducts.clear()
//            listProducts.addAll(it)
//        })
        //
        viewModel.livePurchaseItemList.observe(this,{
            (rootElement.vpPurchaseItems.adapter as CardItemPurchaseRcAdapter).update(it)
        })
    }

    override fun onResume() {
        super.onResume()
        //viewModel.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        //viewModel.closeDb()
        job?.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initPurchase(){
        if(idPurchase>0) {
            //viewModel.openDb()
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch{
                purchase=viewModel.getPurchase(idPurchase)
                if(purchase!=null){
                    rootElement.apply {
                        //content_temp=Html.fromHtml(purchase!!.content_html,0).makeSpannableString()
                        //edTitle.setText(purchase!!.title)
                        tvSellerSelect.text=purchase!!.sellername
                        edSummaPurchase.setText(purchase!!.summa.toString())
                        initDateTime()
                    }
//                    val purchaseItems=viewModel!!.getPurchaseItems(idPurchase)
//                    (rootElement.vpPurchaseItems.adapter as CardItemPurchaseRcAdapter).update(purchaseItems)
                    viewModel.getPurchaseItems(idPurchase)
                }
            }
        }else {
            //idPurchase==0
            purchase= Purchase()
            if(purchase!=null){
                purchase!!.time=UtilsHelper.getCurrentDate()
                purchase!!.time_day=UtilsHelper.correct_date_begin(purchase!!.time)
                initDateTime()
            }
        }
    }

    private fun init(){
        cardItemPurchaseAdapter= CardItemPurchaseRcAdapter()
        rootElement.vpPurchaseItems.adapter=cardItemPurchaseAdapter
        rootElement.tvSellerSelect.setOnClickListener {
            var idseller=0
            //if(rootElement.tvSellerSelect.tag==null || (rootElement.tvSellerSelect.tag as Seller)==null) idseller=purchase!!.idseller
            if(rootElement.tvSellerSelect.tag==null ) idseller=purchase!!.idseller
            else idseller=(rootElement.tvSellerSelect.tag as Seller).id
           // rootElement.tvSellerSelect.setTag(idseller)
            launchSellerActivity(idseller)
        }
        if(idPurchase==0) rootElement.purchaseItemButton.visibility=View.VISIBLE
        else rootElement.purchaseItemButton.visibility=View.GONE

        // register launchers for baselist
        launcherSeller=getLauncher()

        launcherProduct=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result: ActivityResult ->
            if(result.resultCode== AppCompatActivity.RESULT_OK){
                if(result.data!=null){
                    //result.data is intent
                    val intent=result.data
                    val product= intent!!.getSerializableExtra(Product::class.java.getSimpleName()) as Product
                    if(pitDialog!=null) {
                        val tvProduct=pitDialog.findViewById<TextView>(R.id.tvProduct)
                        tvProduct.setTag(product)
                        tvProduct.text=product.name
                    }
                }
            }
        }

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
                    purchase!!.time_day=UtilsHelper.correct_date_begin(purchase!!.time)
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
                    purchase!!.time_day=UtilsHelper.correct_date_begin(purchase!!.time)
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

    fun launchSellerActivity(id:Int){
        val intent= Intent(this@EditPurchaseActivity, SellerActivity::class.java)
        intent.putExtra(R.string.SELLER_ID.toString(),id)
        launcherSeller.launch(intent) //getLauncher().launch(intent)
    }



    private fun getLauncher():ActivityResultLauncher<Intent>{
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result: ActivityResult ->
            if(result.resultCode==AppCompatActivity.RESULT_OK){
                if(result.data!=null){
                    //result.data is intent
                    val intent=result.data
                    //val new_purchase_time=intent!!.getLongExtra(getString(R.string.new_purchase_time),0L)
                    val seller= intent!!.getSerializableExtra(Seller::class.java.getSimpleName()) as Seller
                    rootElement.tvSellerSelect.setTag(seller)
                    rootElement.tvSellerSelect.text=seller.name

//                    tvSelection.text=selectItem.name
//                    tvSelection.setTag(selectItem)
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickSavePurchase(view: View){
        var remove=true
        //viewModel.openDb()
        rootElement.apply {
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch{
               //здесь то что редактируется а не пришло из фрагмента
                purchase!!.summa= edSummaPurchase.value.toDouble();//.text.toString().toDouble()
                //purchase!!.title=edTitle.text.toString()
                if(tvSellerSelect.tag!=null) {
                    val seller=tvSellerSelect.tag as Seller
                    purchase!!.idseller=seller.id
                    purchase!!.sellername=seller.name
                }
                var old_time=purchase!!.time
                purchase!!.time= ImportUtils.parseDateTimeString(rootElement.edDatePart.text.toString()+" "+rootElement.edTimePart.text.toString())!!
                purchase!!.time_day=UtilsHelper.correct_date_begin(purchase!!.time)
                if(idPurchase>0){
                    //dbManager.updatePurchase(idPurchase,edTitle.text.toString(),edDescription.text.toString())
                    viewModel.updatePurchase(purchase!!)
                }else{
                    idPurchase= viewModel.insertPurchase(purchase!!)!!
                    purchase!!.id=idPurchase
                }

                // callback to main activity temporary!!!
                //setResult()
                val data = Intent()
                //if(idPurchase==0) old_time=0L
                data.putExtra(getString(R.string.old_purchase_time), old_time)
                data.putExtra(getString(R.string.new_purchase_time), purchase!!.time)
                data.putExtra(getString(R.string.PURCHASE_ID), idPurchase)
                data.putExtra(Purchase::class.java.getSimpleName(), purchase)
                setResult(RESULT_OK, data)
                //
//                   if(old_time!=purchase!!.time) mainActivity!!.removePurchaseEvent(old_time)
//                   mainActivity!!.addPurchaseEvent(purchase!!.time)

                for(pit: PurchaseItem in (vpPurchaseItems.adapter as CardItemPurchaseRcAdapter).mainArray){
                    if(pit.id==0){
                        //set idPurchase when new Purchase
                        pit.idPurchase=idPurchase
                        viewModel.insertPurchaseItem(pit)
                    }else{
                        viewModel.updatePurchaseItem(pit)
                    }
                }
                for(pit: PurchaseItem in listDeletedPurchaseItems){
                    viewModel.removePurchaseItem(pit.id)
                }
                listDeletedPurchaseItems=ArrayList<PurchaseItem>()
                //dbManager

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
                for(pit: PurchaseItem in list){
                    content_temp+=pit.getContentShort(title_color)+"\n\n"
                    summa+=pit.summa
                }
                purchase!!.content= content_temp.toString()
                purchase!!.content_html=Html.toHtml(content_temp,0)
                rootElement.edSummaPurchase.setText(summa.toString())
            }

                override fun onFragClose() {
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
            //viewModel.getProducts("")
            val pit= PurchaseItem()
            //новая запись
            pit.id=0
            pit.idPurchase=idPurchase
            pitDialog=DialogHelper.showPurchaseItemInputDialog(this@EditPurchaseActivity,
                pit,
                launcherProduct,
                object: IUpdatePurchaseItemList {
                    override fun onUpdatePurchaseItemList(pit: PurchaseItem) {
                        if(pit.id==0)purchaseItemFragment!!.adapter.updateAdapterInsert(pit)
                        else purchaseItemFragment!!.adapter.updateAdapterEdit(pit)
                    }

                }
            )
        }
    }


}//activity
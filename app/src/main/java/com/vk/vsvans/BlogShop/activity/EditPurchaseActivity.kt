package com.vk.vsvans.BlogShop.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.adapters.CardItemPurchaseRcAdapter
import com.vk.vsvans.BlogShop.databinding.ActivityEditPurchaseBinding
import com.vk.vsvans.BlogShop.fragments.PurchaseItemListFragment
import com.vk.vsvans.BlogShop.interfaces.FragmentCloseInterface
import com.vk.vsvans.BlogShop.mainActivity
import com.vk.vsvans.BlogShop.model.DbManager
import com.vk.vsvans.BlogShop.model.Purchase
import com.vk.vsvans.BlogShop.model.PurchaseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditPurchaseActivity : AppCompatActivity(),FragmentCloseInterface {

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
//    private fun initToolbar(){
//        rootElement.apply {
//            val savePurchaseItem = tb.menu.findItem(R.id.id_save_purchase)
//            savePurchaseItem.setOnMenuItemClickListener {
//                job?.cancel()
//                job = CoroutineScope(Dispatchers.Main).launch{
//                    if(dbManager!=null) {
//                        if(idPurchase>0){
//                            dbManager.updatePurchaseItem(idPurchase,edTitle.text.toString(),edDescription.text.toString())
//                        }else{
//                            dbManager.insertPurchaseToDb(edTitle.text.toString(),edDescription.text.toString())
//                        }
//                    }
//                    onBackPressed()
//                }
//                true
//            }
//            tb.setNavigationOnClickListener {
//                onBackPressed()
//            }
//        }
//    }
    private fun initPurchase(){
        if(idPurchase>0) {
            dbManager.openDb()
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch{
                purchase=dbManager.readOnePurchase(idPurchase)
                if(purchase!=null){
                    rootElement.apply {
                        edDescription.setText(purchase!!.content)
                        edTitle.setText(purchase!!.title)
                        edSummaPurchase.setText(purchase!!.summa.toString())
                    }
                    val purchaseItems=dbManager.readPurchaseItems(idPurchase)
                    (rootElement.vpPurchaseItems.adapter as CardItemPurchaseRcAdapter).update(purchaseItems)
                }
            }
        }else {
            //idPurchase==0
            purchase= Purchase()
        }
    }

    private fun init(){
        cardItemPurchaseAdapter= CardItemPurchaseRcAdapter()
        rootElement.vpPurchaseItems.adapter=cardItemPurchaseAdapter

    }

    fun clearResultArray(){
        listResultArray.clear()
    }

    //onClick выбран в activity_edit_ads
    fun onClickSelectCountry(view:View){
//        val listCountry=CityHelper.getAllCountries(this)
//        dialog.showSpinnerDialog(this,listCountry,rootElement.tvCountry)
//        if(rootElement.tvCity.text.toString()!=getString(R.string.select_city)){
//            rootElement.tvCity.text=getString(R.string.select_city)
//        }
    }

    fun onClickSelectCity(view:View){
//        val selectedCountry=rootElement.tvCountry.text.toString()
//        if(selectedCountry!=getString(R.string.select_country)) {
//            val listCity = CityHelper.getAllCities(selectedCountry, this)
//            dialog.showSpinnerDialog(this, listCity, rootElement.tvCity)
//        }else{
//            Toast.makeText(this,getString(R.string.no_country_selected),Toast.LENGTH_LONG).show()
//        }
    }

    fun onClickSelectCategory(view:View){
//        val listCategory=resources.getStringArray(R.array.Category).toMutableList() as ArrayList
//        dialog.showSpinnerDialog(this,listCategory,rootElement.tvCategory)

    }

    fun onClickSavePurchase(view: View){
        rootElement.apply {
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch{
               if(dbManager!=null) {
//                   var content=""
//                   var summa=0.0
//                   for(pit:PurchaseItem in (vpPurchaseItems.adapter as CardItemPurchaseRcAdapter).mainArray){
//                       content+=pit.getContent()+"\n\n"
//                       summa+=pit.summa
//                   }
                   //if(purchase==null) purchase=Purchase()
                   purchase!!.content=edDescription.text.toString()
                   purchase!!.title=edTitle.text.toString()
                   purchase!!.summa=edSummaPurchase.text.toString().toDouble()
                   if(idPurchase>0){
                       //dbManager.updatePurchase(idPurchase,edTitle.text.toString(),edDescription.text.toString())
                       dbManager.updatePurchase(purchase!!)
                   }else{
                       idPurchase= dbManager.insertPurchase(purchase!!)!!
                       purchase!!.id=idPurchase
                   }
                   for(pit:PurchaseItem in (vpPurchaseItems.adapter as CardItemPurchaseRcAdapter).mainArray){
                       pit.idPurchase=idPurchase
                       if(pit.id==0){
                           //set idPurchase when new Purchase
                           dbManager.insertPurchaseItem(pit)
                       }else{
                           dbManager.updatePurchaseItem(pit)
                       }
                   }
               }
                onBackPressed()
            }
        }

//        dbManager.publishAd(fillAd())
    }

    fun onClickCancelPurchase(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        if(mainActivity!=null)mainActivity!!.fillAdapter("")
        super.onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onClickGetPurchaseItems(view:View){
        val newList=(rootElement.vpPurchaseItems.adapter as CardItemPurchaseRcAdapter).mainArray
//        val pit=PurchaseItem()
//        pit.price=1000.0
//        pit.quantity=1.234
//        pit.summa=3000.0
//        newList.add(pit)
        purchaseItemFragment= PurchaseItemListFragment(this,newList)
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

    override fun onFragClose(list: ArrayList<PurchaseItem>) {
        rootElement.scrollViewMain.visibility=View.VISIBLE
        cardItemPurchaseAdapter.update(list)
        var summa=0.0
        var content=""
        for(pit:PurchaseItem in list){
            summa+=pit.summa
            content+=pit.getContent()+"\n\n"
        }
        // store from fragment in edit form
        rootElement.edSummaPurchase.setText(summa.toString())
        rootElement.edDescription.setText(content)
    }


}
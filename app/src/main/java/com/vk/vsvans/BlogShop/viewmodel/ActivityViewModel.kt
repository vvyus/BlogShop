package com.vk.vsvans.BlogShop.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vk.vsvans.BlogShop.AppStart
import com.vk.vsvans.BlogShop.model.data.*
import com.vk.vsvans.BlogShop.model.repository.DbRepositoryImpl
import com.vk.vsvans.BlogShop.model.repository.IDbRepository
import com.vk.vsvans.BlogShop.util.FilterForActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ActivityViewModel(): ViewModel() {
//class ActivityViewModel(application: Application): AndroidViewModel(application) {
    //private val mDbRepositoryImpl:IDbRepository=DbRepositoryImpl(context)
    @SuppressLint("StaticFieldLeak")
    //val context:Context=application//application.applicationContext
    private val mDbRepositoryImpl:IDbRepository=AppStart.instance!!.getDatabase()!!//DbRepositoryImpl(context)
    //add
    val livePurchaseList= MutableLiveData<ArrayList<Purchase>>()
    val liveCalendarEvents= MutableLiveData<HashMap<String, Int>>()
    val liveAmount=MutableLiveData<Double>(0.0)

    fun getPurchases(filter: FilterForActivity){
        mDbRepositoryImpl.getAllPurchases(filter,object:IDbRepository.ReadDataCallback{
            override fun readData(list: ArrayList<Purchase>,hm:HashMap<String, Int>,amount:Double) {
                livePurchaseList.value=list
                liveCalendarEvents.value=hm
                liveAmount.value=amount//MutableLiveData<Double>(amount)
            }

        })
    }

    // for list productamount
    val liveProductAmountList= MutableLiveData<ArrayList<ProductAmount>>()

    fun getProductAmount(filter: String){
        mDbRepositoryImpl.getAllProductAmount(filter,object:IDbRepository.ReadProductAmountCallback{
            override fun readData(list: ArrayList<ProductAmount>) {
                liveProductAmountList.value=list
            }
        })
    }

    //for list product
    val liveProductList= MutableLiveData<ArrayList<Product>>()
    fun getProducts(filterString:String){
        mDbRepositoryImpl.getAllProducts(filterString,object:IDbRepository.ReadProductCallback{
            override fun readData(list: ArrayList<Product>) {
                liveProductList.value=list
             }

        })
    }
    // for list seller
    val liveSellerList= MutableLiveData<ArrayList<Seller>>()
    fun getSellers(filterString:String){
        mDbRepositoryImpl.getAllSellers(filterString,object:IDbRepository.ReadSellerCallback{
            override fun readData(list: ArrayList<Seller>) {
                liveSellerList.value=list
            }

        })
    }
    // for purchase item
    val livePurchaseItemList= MutableLiveData<ArrayList<PurchaseItem>>()
    fun getPurchaseItems(idPurchase: Int){
        mDbRepositoryImpl.getAllPurchaseItems(idPurchase,object:IDbRepository.ReadPurchaseItemCallback{
            override fun readData(list: ArrayList<PurchaseItem>) {
                livePurchaseItemList.value=list
            }

        })
    }
    //add
//    suspend fun getPurchaseItems(idPurchase: Int): ArrayList<PurchaseItem> = withContext(Dispatchers.IO) {
//        return@withContext mDbRepositoryImpl.getPurchaseItems(idPurchase)
//    }

    fun openDb(){
        mDbRepositoryImpl.openDb()
    }

    fun closeDb(){
        mDbRepositoryImpl.closeDb()
    }

//    suspend fun getPurchases(filter: FilterForActivity, purchaseList: ArrayList<Purchase>,
//                     calendar_events: HashMap<String, Int>): Double = withContext(Dispatchers.IO) {
//        return@withContext mDbRepositoryImpl.getPurchases(filter,purchaseList, calendar_events)
//    }
//
//    suspend fun getPurchases(filter: FilterForActivity, purchaseList: ArrayList<Purchase>): Double = withContext(Dispatchers.IO) {
//        return@withContext mDbRepositoryImpl.getPurchases(filter,purchaseList)
//    }

    fun removePurchase(id: Int){
        mDbRepositoryImpl.removePurchase(id)
    }

    suspend fun getPurchaseFns(idFns: String): Int= withContext(Dispatchers.IO) {
        return@withContext mDbRepositoryImpl.getPurchaseFns(idFns)
    }

    suspend fun insertPurchase(purchase: Purchase): Int? = withContext(Dispatchers.IO){
        return@withContext mDbRepositoryImpl.insertPurchase(purchase)
    }

    suspend fun updatePurchase(purchase: Purchase) = withContext(Dispatchers.IO) {
        mDbRepositoryImpl.updatePurchase(purchase)
    }

    suspend fun getSellersFns(key: String): ArrayList<Seller> = withContext(Dispatchers.IO) {
        return@withContext mDbRepositoryImpl.getSellersFns(key)
    }

    suspend fun removePurchaseItems(idPurchase: Int) = withContext(Dispatchers.IO) {
        mDbRepositoryImpl.removePurchaseItems(idPurchase)
    }

    suspend fun insertPurchaseItem(pit: PurchaseItem) = withContext(Dispatchers.IO) {
        return@withContext mDbRepositoryImpl.insertPurchaseItem(pit)
    }

    suspend fun getPurchase(idPurchase: Int): Purchase? = withContext(Dispatchers.IO) {
        return@withContext mDbRepositoryImpl.getPurchase(idPurchase)
    }

    suspend fun getProductsFns(key: String): ArrayList<Product> = withContext(Dispatchers.IO) {
        return@withContext mDbRepositoryImpl.getProductsFns(key)
    }

    fun insertProduct(product: Product): Int? {
        return mDbRepositoryImpl.insertProduct(product)
    }

    fun updateProduct(product: Product) {
        mDbRepositoryImpl.updateProduct(product)
    }

    fun removeProduct(id: Int){
        mDbRepositoryImpl.removeProduct(id)
    }

//    suspend fun insertSeller(seller: Seller): Int?= withContext(Dispatchers.IO) {
//        return@withContext mDbRepositoryImpl.insertSeller(seller)
//    }
//
//    suspend fun updateSeller(seller: Seller)= withContext(Dispatchers.IO)  {
//        mDbRepositoryImpl.updateSeller(seller)
//    }

    fun insertSeller(seller: Seller): Int? {
        return mDbRepositoryImpl.insertSeller(seller)
    }

    fun updateSeller(seller: Seller) {
        mDbRepositoryImpl.updateSeller(seller)
    }

    fun removeSeller(id: Int){
        mDbRepositoryImpl.removeSeller(id)
    }

// PURCAHSE items
    suspend fun removePurchaseItem(id: Int) = withContext(Dispatchers.IO) {
        mDbRepositoryImpl.removePurchaseItem(id)
    }

    suspend fun updatePurchaseItem(pit: PurchaseItem) = withContext(Dispatchers.IO) {
        mDbRepositoryImpl.updatePurchaseItem(pit)
    }


}
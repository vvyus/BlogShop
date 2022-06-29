package com.vk.vsvans.BlogShop.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.BaseColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.vk.vsvans.BlogShop.model.data.Product
import com.vk.vsvans.BlogShop.model.data.Purchase
import com.vk.vsvans.BlogShop.model.data.PurchaseItem
import com.vk.vsvans.BlogShop.model.data.Seller
import com.vk.vsvans.BlogShop.model.repository.DbName
import com.vk.vsvans.BlogShop.model.repository.DbRepositoryImpl
import com.vk.vsvans.BlogShop.model.repository.IDbRepository
import com.vk.vsvans.BlogShop.util.FilterForActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//class ActivityViewModel(context: Context): ViewModel() {
class ActivityViewModel(application: Application): AndroidViewModel(application) {
    //private val mDbRepositoryImpl:IDbRepository=DbRepositoryImpl(context)
    @SuppressLint("StaticFieldLeak")
    val context:Context=application.applicationContext
    private val mDbRepositoryImpl:IDbRepository=DbRepositoryImpl(context)
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
    //for list product
    val liveProductList= MutableLiveData<ArrayList<Product>>()
    fun getProducts(filterString:String){
        mDbRepositoryImpl.getAllProducts(filterString,object:IDbRepository.ReadProductCallback{
            override fun readData(list: ArrayList<Product>) {
                liveProductList.value=list
             }

        })
    }
    //add

    fun openDb(){
        mDbRepositoryImpl.openDb()
    }

    fun closeDb(){
        mDbRepositoryImpl.closeDb()
    }

    suspend fun getPurchases(filter: FilterForActivity, purchaseList: ArrayList<Purchase>,
                     calendar_events: HashMap<String, Int>): Double = withContext(Dispatchers.IO) {
        return@withContext mDbRepositoryImpl.getPurchases(filter,purchaseList, calendar_events)
    }

    suspend fun getPurchases(filter: FilterForActivity, purchaseList: ArrayList<Purchase>): Double = withContext(Dispatchers.IO) {
        return@withContext mDbRepositoryImpl.getPurchases(filter,purchaseList)
    }

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

    suspend fun insertSeller(seller: Seller): Int?= withContext(Dispatchers.IO) {
        return@withContext mDbRepositoryImpl.insertSeller(seller)
    }

    suspend fun updateSeller(seller: Seller)= withContext(Dispatchers.IO)  {
        mDbRepositoryImpl.updateSeller(seller)
    }

    suspend fun removePurchaseItems(idPurchase: Int) = withContext(Dispatchers.IO) {
        mDbRepositoryImpl.removePurchaseItems(idPurchase)
    }

    suspend fun insertPurchaseItem(pit: PurchaseItem) = withContext(Dispatchers.IO) {
        return@withContext mDbRepositoryImpl.insertPurchaseItem(pit)
    }

    suspend fun getProductsFns(key: String): ArrayList<Product> = withContext(Dispatchers.IO) {
        return@withContext mDbRepositoryImpl.getProductsFns(key)
    }

    //suspend fun insertProduct(product: Product): Int?= withContext(Dispatchers.IO) {
    fun insertProduct(product: Product): Int? {
        return mDbRepositoryImpl.insertProduct(product)
    }

    //suspend fun updateProduct(product: Product)= withContext(Dispatchers.IO) {
    fun updateProduct(product: Product) {
        mDbRepositoryImpl.updateProduct(product)
    }

    fun removeProduct(id: Int){
        mDbRepositoryImpl.removeProduct(id)
    }
}
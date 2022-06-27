package com.vk.vsvans.BlogShop.viewmodel

import android.content.Context
import com.vk.vsvans.BlogShop.model.data.Product
import com.vk.vsvans.BlogShop.model.data.Purchase
import com.vk.vsvans.BlogShop.model.data.PurchaseItem
import com.vk.vsvans.BlogShop.model.data.Seller
import com.vk.vsvans.BlogShop.model.repository.DbRepositoryImpl
import com.vk.vsvans.BlogShop.model.repository.IDbRepository
import com.vk.vsvans.BlogShop.util.FilterForActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ActivityViewModel(context: Context) {
    private val mDbRepositoryImpl:IDbRepository=DbRepositoryImpl(context)
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

    suspend fun insertProduct(product: Product): Int?= withContext(Dispatchers.IO) {
        return@withContext mDbRepositoryImpl.insertProduct(product)
    }

    suspend fun updateProduct(product: Product)= withContext(Dispatchers.IO) {
        mDbRepositoryImpl.updateProduct(product)
    }
}
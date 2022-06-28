package com.vk.vsvans.BlogShop.model.repository

import androidx.lifecycle.MutableLiveData
import com.vk.vsvans.BlogShop.model.data.Product
import com.vk.vsvans.BlogShop.model.data.Purchase
import com.vk.vsvans.BlogShop.model.data.PurchaseItem
import com.vk.vsvans.BlogShop.model.data.Seller
import com.vk.vsvans.BlogShop.util.FilterForActivity

interface IDbRepository {
    fun openDb()
    fun closeDb()
    // PRODUCT
    fun getProducts(searchText:String): ArrayList<Product>
    fun getProductsFns(searchText:String): ArrayList<Product>
    fun insertProduct( product: Product):Int?
    fun updateProduct(product: Product)
    fun removeProduct(id: Int)
    // SELLER
    fun getSellers(searchText: String): ArrayList<Seller>
    fun getSellersFns(searchText:String): ArrayList<Seller>
    fun updateSeller(seller: Seller)
    fun insertSeller( seller: Seller) :Int?
    fun removeSeller(id: Int)
    // PURCAHSE
    fun getPurchases(filter: FilterForActivity, purchaseList: ArrayList<Purchase>, calendar_events:HashMap<String, Int>):Double
    fun getPurchases(filter:FilterForActivity, purchaseList: ArrayList<Purchase>):Double
    fun getPurchase(id:Int): Purchase?
    fun getPurchaseFns(idFns:String): Int
    fun insertPurchase(purchase: Purchase) :Int?
    fun updatePurchase(purchase: Purchase)
    fun removePurchase(id: Int)
    // PURCAHSE ITEM
    fun getPurchaseItems(id:Int): ArrayList<PurchaseItem>
    fun updatePurchaseItem(purchaseItem: PurchaseItem)
    fun insertPurchaseItem(purchaseItem: PurchaseItem):Int?
    fun removePurchaseItem(id: Int)
    fun removePurchaseItems(idpurchase: Int)
    //ADD
    fun getAllPurchases(filter: FilterForActivity,readDataCallback: ReadDataCallback?)
    interface ReadDataCallback {
        fun readData(list: ArrayList<Purchase>,hm:HashMap<String, Int>,amount: Double)
    }
}
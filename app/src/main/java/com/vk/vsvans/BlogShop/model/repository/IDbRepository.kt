package com.vk.vsvans.BlogShop.model.repository

import androidx.lifecycle.MutableLiveData
import com.vk.vsvans.BlogShop.model.data.*
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
    //
    // PRODUCT
    fun getProductAmount(searchText:String,time:Long): ArrayList<ProductAmount>
    fun getSellerAmount(searchText:String,time:Long): ArrayList<SellerAmount>
    //ADD
    fun getAllPurchases(filter: FilterForActivity,readDataCallback: ReadDataCallback?)
    interface ReadDataCallback {
        fun readData(list: ArrayList<Purchase>,hm:HashMap<String, Int>,amount: Double)
    }
    fun getAllProducts(filterString:String,readProductCallback: ReadProductCallback?)
    interface ReadProductCallback {
        fun readData(list: ArrayList<Product>)
    }

    fun getAllSellers(filterString:String,readSellerCallback: ReadSellerCallback?)
    interface ReadSellerCallback {
        fun readData(list: ArrayList<Seller>)
    }

    fun getAllPurchaseItems(idPurchase:Int,readPurchaseItemCallback: ReadPurchaseItemCallback?)
    interface ReadPurchaseItemCallback {
        fun readData(list: ArrayList<PurchaseItem>)
    }

    fun getAllProductAmount(filterString:String,readProductCallback: ReadProductAmountCallback?,time:Long)
    interface ReadProductAmountCallback {
        fun readData(list: ArrayList<ProductAmount>)
    }

    fun getAllSellerAmount(filterString:String,readSellerCallback: ReadSellerAmountCallback?,time:Long)
    interface ReadSellerAmountCallback {
        fun readData(list: ArrayList<SellerAmount>)
    }

}
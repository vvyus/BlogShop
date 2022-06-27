package com.vk.vsvans.BlogShop.viewmodel

import android.content.Context
import com.vk.vsvans.BlogShop.model.data.Purchase
import com.vk.vsvans.BlogShop.model.repository.DbRepositoryImpl
import com.vk.vsvans.BlogShop.model.repository.IDbRepository
import com.vk.vsvans.BlogShop.util.FilterForActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivityViewModel(context: Context) {
    val mDbRepositoryImpl:IDbRepository=DbRepositoryImpl(context)
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
}
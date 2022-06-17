package com.vk.vsvans.BlogShop.helper

import android.content.Context
import com.vk.vsvans.BlogShop.activity.EditPurchaseActivity
import com.vk.vsvans.BlogShop.model.Product
import com.vk.vsvans.BlogShop.model.Seller

object SpinnerHelper {
    suspend fun getAllSeller(context: Context):ArrayList<Seller>{
        //var tempArray=ArrayList<Product>()
        val tempArray=(context as EditPurchaseActivity).dbManager.readSellers("")
        return tempArray
    }
    fun filterListDataSeller(list:ArrayList<Seller>, searchText:String?):ArrayList<Seller>{
        val tempList=ArrayList<Seller>()
        tempList.clear()
        if(searchText==null){
            //tempList.add("No Result")
            return tempList
        }
        for(selection:Seller in list){
            if(selection.name.lowercase().startsWith(searchText.lowercase())){
                tempList.add(selection)
            }
        }
        //if(tempList.size==0) tempList.add("No Result")//R.string.no_result_in_list)
        return tempList
    }

    suspend fun getAllProduct(context: Context):ArrayList<Product>{
        //var tempArray=ArrayList<Product>()
        val tempArray=(context as EditPurchaseActivity).dbManager.readProducts("")
        return tempArray
    }
    fun filterListData(list:ArrayList<Product>, searchText:String?):ArrayList<Product>{
        val tempList=ArrayList<Product>()
        tempList.clear()
        if(searchText==null){
            //tempList.add("No Result")
            return tempList
        }
        for(selection:Product in list){
            if(selection.name.lowercase().startsWith(searchText.lowercase())){
                tempList.add(selection)
            }
        }
        //if(tempList.size==0) tempList.add("No Result")//R.string.no_result_in_list)
        return tempList
    }
}
package com.vk.vsvans.BlogShop.helper

import android.content.Context
import com.vk.vsvans.BlogShop.activity.EditPurchaseActivity
import com.vk.vsvans.BlogShop.model.Product

object SpinnerHelper {
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
            if(selection.title.lowercase().startsWith(searchText.lowercase())){
                tempList.add(selection)
            }
        }
        //if(tempList.size==0) tempList.add("No Result")//R.string.no_result_in_list)
        return tempList
    }
}
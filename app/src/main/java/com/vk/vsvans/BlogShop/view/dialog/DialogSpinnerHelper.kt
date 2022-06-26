package com.vk.vsvans.BlogShop.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.SearchView
import android.widget.TextView

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.view.EditPurchaseActivity
import com.vk.vsvans.BlogShop.model.data.BaseList
import com.vk.vsvans.BlogShop.view.adapter.DialogSpinnerBaselistAdapter

class DialogSpinnerHelper {
    fun showSpinnerDialog(context: Context, list: ArrayList<BaseList>, tvSelection: TextView){
        val builder=AlertDialog.Builder(context)
        val dialog=builder.create()
        //надуваем в диалоге нет инфлайтора поэтому берем его из контекста .from
        val rootView=LayoutInflater.from(context).inflate(R.layout.spinner_layout,null)
        var id_baselist=tvSelection.tag as Int
        if(id_baselist==null) id_baselist=0
        val adapter= DialogSpinnerBaselistAdapter(tvSelection,dialog)
        val rcView=rootView.findViewById<RecyclerView>(R.id.rcSellerView)
        val sv=rootView.findViewById<SearchView>(R.id.svSeller)
        // разметека в spinner_layout выбрана ->LinearLayout
        rcView.layoutManager=LinearLayoutManager(context)
        rcView.adapter=adapter
        // builder нужен уже готовый view
        dialog.setView(rootView)
        adapter.updateAdapter(list)
        adapter.setSelectionPosById(id_baselist)

        setSearchViewBaseList(adapter,list,sv)

        dialog.show()
        dialog.getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)

    }

    private fun setSearchViewBaseList(adapter: DialogSpinnerBaselistAdapter, list: ArrayList<BaseList>, sv: SearchView) {
        if (sv != null) {
            sv.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //adapter.updateAdapter(list)
                    return false//true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val tempList= filterListDataBaseList(list,newText)
                    adapter.updateAdapter(tempList)

                    return true
                }
            })
        }

    }

    fun filterListDataBaseList(list:ArrayList<BaseList>, searchText:String?):ArrayList<BaseList>{
        val tempList=ArrayList<BaseList>()
        tempList.clear()
        if(searchText==null || searchText.isEmpty()){
            //tempList.add("No Result")
            return list //tempList
        }
        for(selection: BaseList in list){
            if(selection.name.lowercase().startsWith(searchText.lowercase())){
                tempList.add(selection)
            }
        }
        return tempList
    }
    suspend fun getAllSeller(context: Context):ArrayList<BaseList> {
        val sellerArray=(context as EditPurchaseActivity).dbManager.readSellers("")
        val baselistArray=ArrayList<BaseList>()
        baselistArray.addAll(sellerArray)
        return baselistArray
    }

    suspend fun getAllProduct(context: Context): ArrayList<BaseList> {
        val productArray=(context as EditPurchaseActivity).dbManager.readProducts("")
        val baselistArray=ArrayList<BaseList>()
        baselistArray.addAll(productArray)
        return baselistArray
    }

}
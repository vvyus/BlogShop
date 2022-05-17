package com.vk.vsvans.BlogShop.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.SearchView
import android.widget.TextView

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.adapters.DialogSpinnerRcAdapter

class DialogSpinnerHelper {
    fun showSpinnerDialog(context: Context, list: ArrayList<String>, tvSelection: TextView){
        val builder=AlertDialog.Builder(context)
        val dialog=builder.create()
        //надуваем в диалоге нет инфлайтора поэтому берем его из контекста .from
        val rootView=LayoutInflater.from(context).inflate(R.layout.spinner_layout,null)
        val adapter=DialogSpinnerRcAdapter(tvSelection,dialog)
        val rcView=rootView.findViewById<RecyclerView>(R.id.rcSellerView)
        val sv=rootView.findViewById<SearchView>(R.id.svSeller)
        // разметека в spinner_layout выбрана ->LinearLayout
        rcView.layoutManager=LinearLayoutManager(context)
        rcView.adapter=adapter
        // builder нужен уже готовый view
        dialog.setView(rootView)
        adapter.updateAdapter(list)
        setSearchView(adapter,list,sv)

        dialog.show()
    }

    private fun setSearchView(adapter: DialogSpinnerRcAdapter, list: ArrayList<String>, sv: SearchView) {
        if (sv != null) {
            sv.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
//                    val tempList=CityHelper.filterListData(list,newText)
//                    adapter.updateAdapter(tempList)
                    return true
                }
            })
        }
    }

}
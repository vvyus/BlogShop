package com.vk.vsvans.BlogShop.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.model.Seller
class DialogSpinnerSellerAdapter(var tvSelection: TextView, var dialog: AlertDialog) : RecyclerView.Adapter<DialogSpinnerSellerAdapter.SpViewHolder>() {

    private val mainList=ArrayList<Seller>()
    //private val context=context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.spinner_list_item,parent,false)
        return SpViewHolder(view,tvSelection,dialog)
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainList[position])
    }

    override fun getItemCount(): Int {

        return mainList.size
    }
    // столько viewholder сколько элементов
    //var context для доступности в функциях класса аналог переменой класса контекст
    class SpViewHolder(itemView: View, var tvSelection: TextView, var dialog: AlertDialog) :RecyclerView.ViewHolder(itemView),View.OnClickListener {
        private var itemText=""
        private var itemSeller:Seller?=null
        fun setData(seller:Seller){
            val text=seller.name
            val tvSpItem=itemView.findViewById<TextView>(R.id.tvSpItem)
            tvSpItem.text=text
            itemText=text
            itemSeller=seller
            // присваиваем onClick выбранному элементу
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            //(tvSelection as EditAdsAct).rootElement.tvCountry.setText(itemText)
            tvSelection.text=itemText
            tvSelection.setTag(itemSeller)
            dialog.dismiss()
        }
    }

    fun updateAdapter(list:ArrayList<Seller>){
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }
}
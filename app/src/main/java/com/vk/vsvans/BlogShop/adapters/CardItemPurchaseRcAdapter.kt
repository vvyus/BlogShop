package com.vk.vsvans.BlogShop.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.model.PurchaseItem

class CardItemPurchaseRcAdapter: RecyclerView.Adapter<CardItemPurchaseRcAdapter.ImageHolder>() {
    val mainArray= ArrayList<PurchaseItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.card_purchase_adapter_item,parent,false)
        return ImageHolder(view)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
       holder.setDate(mainArray[position].getContent())
    }

    override fun getItemCount(): Int {
       return mainArray.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(newList:ArrayList<PurchaseItem>){
        mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }

    class ImageHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        lateinit var tvItem: TextView

        fun setDate(text:String) {
            tvItem=itemView.findViewById(R.id.tvItem)
            tvItem.text =text
        }
    }
}
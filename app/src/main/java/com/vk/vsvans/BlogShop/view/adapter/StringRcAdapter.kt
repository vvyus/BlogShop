package com.vk.vsvans.BlogShop.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.model.data.PurchaseItem

class StringRcAdapter: RecyclerView.Adapter<StringRcAdapter.StringHolder>() {
    val mainArray= ArrayList<String>()
    var context: Context? =null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringHolder {
        context=parent.context
        val view= LayoutInflater.from(parent.context).inflate(R.layout.string_adapter_item,parent,false)
        return StringHolder(view)
    }

    override fun onBindViewHolder(holder: StringHolder, position: Int) {
        holder.setDate(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun add(str:String){
        mainArray.add(str)
        notifyDataSetChanged()
    }
    fun clear(){
        mainArray.clear()
        notifyDataSetChanged()
    }
    class StringHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var tvItem: TextView

        fun setDate(text: String) {
            tvItem=itemView.findViewById(R.id.tvItemCheckDialog)
            tvItem.text =text
        }
    }
}
package com.vk.vsvans.BlogShop.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R

class DialogSpinnerRcAdapter(var tvSelection: TextView, var dialog: AlertDialog) : RecyclerView.Adapter<DialogSpinnerRcAdapter.SpViewHolder>() {

    private val mainList=ArrayList<String>()
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

        fun setData(text:String){
            val tvSpItem=itemView.findViewById<TextView>(R.id.tvSpItem)
            tvSpItem.text=text
            itemText=text
            // присваиваем onClick выбранному элементу
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            //(tvSelection as EditAdsAct).rootElement.tvCountry.setText(itemText)
            tvSelection.text=itemText
            dialog.dismiss()
        }
    }

    fun updateAdapter(list:ArrayList<String>){
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }
}
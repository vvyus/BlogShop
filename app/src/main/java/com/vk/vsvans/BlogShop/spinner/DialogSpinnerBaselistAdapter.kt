package com.vk.vsvans.BlogShop.spinner

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.model.BaseList
import com.vk.vsvans.BlogShop.utils.UtilsString

class DialogSpinnerBaselistAdapter(var tvSelection: TextView, var dialog: AlertDialog) : RecyclerView.Adapter<DialogSpinnerBaselistAdapter.SpViewHolder>() {

    private val mainList=ArrayList<BaseList>()
    //private val context=context
    var offset16=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {
        offset16 = UtilsString.dpToPx(16, parent.context)
        val view=
            LayoutInflater.from(parent.context).inflate(R.layout.spinner_list_item,parent,false)
        return SpViewHolder(view,tvSelection,dialog)
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainList[position])
        val itemList=mainList[position]
        val leftMargin: Int = offset16 * itemList.level
        val parentid=itemList.idparent
        if(parentid==itemList.id || parentid>0){
            holder.itemView.visibility= View.VISIBLE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            val lp=holder.itemView.getLayoutParams()
            var p: ViewGroup.MarginLayoutParams=lp as ViewGroup.MarginLayoutParams//
            p.setMargins(leftMargin, 0, 0, 0)

        } else {
            holder.itemView.visibility= View.GONE
            holder.itemView.setLayoutParams(RecyclerView.LayoutParams(0, 0))
        }
        holder.itemView.requestLayout()
    }

    override fun getItemCount(): Int {

        return mainList.size
    }
    // столько viewholder сколько элементов
    //var context для доступности в функциях класса аналог переменой класса контекст
    class SpViewHolder(itemView: View, var tvSelection: TextView, var dialog: AlertDialog) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var itemText=""
        private var itemBaseList: BaseList?=null

        fun setData(baseList: BaseList){
            val text=baseList.name
            val tvSpItem=itemView.findViewById<TextView>(R.id.tvSpItem)
            tvSpItem.text=text
            itemText=text
            itemBaseList=baseList
            // присваиваем onClick выбранному элементу
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            //(tvSelection as EditAdsAct).rootElement.tvCountry.setText(itemText)
            tvSelection.text=itemText
            tvSelection.setTag(itemBaseList)
            dialog.dismiss()
        }
    }

    fun updateAdapter(list:ArrayList<BaseList>){
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }
}
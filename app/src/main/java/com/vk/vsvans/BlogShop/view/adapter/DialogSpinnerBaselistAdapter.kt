package com.vk.vsvans.BlogShop.view.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.model.data.BaseList
import com.vk.vsvans.BlogShop.model.data.Purchase
import com.vk.vsvans.BlogShop.model.data.Seller
import com.vk.vsvans.BlogShop.util.UtilsString

class DialogSpinnerBaselistAdapter(var tvSelection: TextView, var dialog: AlertDialog) : RecyclerView.Adapter<DialogSpinnerBaselistAdapter.SpViewHolder>() {

    private val mainList=ArrayList<BaseList>()
    //private val context=context
    var offset16=0
    var selected_position = RecyclerView.NO_POSITION
    var selected_color =0
//    private var selected_id=id_selected
//    var set_selected_id=false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {

        selected_color=parent.context.resources.getColor(R.color.gray_light,parent.context.theme)
        offset16 = UtilsString.dpToPx(16, parent.context)
        val view=
            LayoutInflater.from(parent.context).inflate(R.layout.spinner_list_item,parent,false)
        return SpViewHolder(view,tvSelection,dialog)
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainList[position])
        holder.itemView.setOnClickListener{

            notifyItemChanged(selected_position)
            if (selected_position != holder.getAdapterPosition()) {
                selectItem(holder.adapterPosition)
            }else{
                unSelectItem()

            }
            notifyItemChanged(selected_position)

        }
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

//        if(selected_id!=0 && mainList[position].id==selected_id && set_selected_id==false) {
//            selected_position=position
//            set_selected_id=true
//        }

        holder.itemView.setBackgroundColor(if (selected_position == position) selected_color else Color.TRANSPARENT)
    }

    fun selectItem(position:Int){
        selected_position=position
        //if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getBaseListId())
    }

    fun unSelectItem(){

        val selectItem=getBaseList()
        if(selectItem!=null){
            tvSelection.text=selectItem.name
            tvSelection.setTag(selectItem)
            dialog.dismiss()
        }else selected_position= RecyclerView.NO_POSITION
        //if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getBaseListId())
    }

    fun setSelectionPosById(id:Int):Int{
        if(id>0){
            for(i in 0 until mainList.size){
                if(mainList[i].id==id){
                    selected_position=i
                    //break
                    return i
                }
            }
        }
        return -1
    }

    fun getBaseList(): BaseList?{
        if(mainList.size!=0 && selected_position!= RecyclerView.NO_POSITION && selected_position<mainList.size){
            return mainList[selected_position]
        }else{
            return null
        }
    }
    override fun getItemCount(): Int {

        return mainList.size
    }
    // ?????????????? viewholder ?????????????? ??????????????????
    //var context ?????? ?????????????????????? ?? ???????????????? ???????????? ???????????? ?????????????????? ???????????? ????????????????
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
            // ?????????????????????? onClick ???????????????????? ????????????????
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            //(tvSelection as EditAdsAct).rootElement.tvCountry.setText(itemText)
            tvSelection.text=itemText
            tvSelection.setTag(itemBaseList)
            dialog.dismiss()
        }
    }

    fun updateAdapter(list: ArrayList<BaseList>){
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }
}
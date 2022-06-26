package com.vk.vsvans.BlogShop.view.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.databinding.ItemPurchaseListFragBinding
import com.vk.vsvans.BlogShop.interfaces.ItemTouchMoveCallBack
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.data.PurchaseItem

class PurchaseItemRcAdapter(val clickItemCallback: OnClickItemCallback?): RecyclerView.Adapter<PurchaseItemRcAdapter.PurchaseItemHolder>() ,
    ItemTouchMoveCallBack.ItemTouchAdapter{
// для доступа из ImageListFrag
    val mainArray=ArrayList<PurchaseItem>()
    var selected_position =RecyclerView.NO_POSITION;
    var selected_color =0
var title_color=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseItemHolder {
        selected_color=ContextCompat.getColor(parent.context,R.color.color_red)
        title_color=ContextCompat.getColor(parent.context,R.color.green_main)

//        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_list_frag,parent,false)
//        return PurchaseItemHolder(view,clickItemCallback)
        val viewBinding= ItemPurchaseListFragBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PurchaseItemHolder(viewBinding,clickItemCallback,title_color)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: PurchaseItemHolder, position: Int) {
        holder.setData(mainArray[position])
        holder.itemView.setOnClickListener{

            notifyItemChanged(selected_position)
            if (selected_position != holder.getAdapterPosition()) {
                selectItem(holder.adapterPosition)
            }else{
                unSelectItem()
            }
            notifyItemChanged(selected_position)
        }
        holder.itemView.setBackgroundColor(if (selected_position == position) selected_color else Color.TRANSPARENT)
        val llButtons=holder.itemView.findViewById<LinearLayout>(R.id.llButtonsPurchaseItem)
        llButtons.visibility=if (selected_position == position) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    fun getPurchaseItem(): PurchaseItem?{
        if(mainArray.size!=0 && selected_position!=RecyclerView.NO_POSITION && selected_position<mainArray.size){
            return mainArray[selected_position]
        }else{
            return null
        }
    }

    fun getPurchaseItemId():Int{
        if(mainArray.size!=0 && selected_position!=RecyclerView.NO_POSITION && selected_position<mainArray.size){
            return mainArray[selected_position].id
        }else{
            return 0
        }
    }

    fun updateAdapter(newList:List<PurchaseItem>){
        mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateAdapterInsert(pit: PurchaseItem){
        mainArray.add(pit)
        notifyDataSetChanged()
    }

    fun updateAdapterEdit(pit: PurchaseItem){
        if(selected_position!=RecyclerView.NO_POSITION) {
            mainArray[selected_position]=pit
            notifyDataSetChanged()
        }
    }

    fun deletePurchaseItem(){
        if(selected_position!=RecyclerView.NO_POSITION) {
            mainArray.removeAt(selected_position)
            notifyItemRemoved(selected_position)
            //reset selected position and selectedId in activity
            unSelectItem()
        }
    }

    fun selectItem(position:Int){
        selected_position=position
        if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getPurchaseItemId())
    }

    fun unSelectItem(){
        selected_position=RecyclerView.NO_POSITION
        if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getPurchaseItemId())
    }

        class PurchaseItemHolder(val viewBinding: ItemPurchaseListFragBinding, val clickItemCallback: OnClickItemCallback?,val title_color:Int) : RecyclerView.ViewHolder(viewBinding.root) {
//    class PurchaseItemHolder(val view: View,val clickItemCallback: OnClickItemCallback?) : RecyclerView.ViewHolder(view) {


        @RequiresApi(Build.VERSION_CODES.Q)
        fun setData(purchaseItem: PurchaseItem){
            viewBinding.apply {
                //val tvContent = view.findViewById<TextView>(R.id.tvContentPurchaseItem)
                tvContentPurchaseItem.text = purchaseItem.getContent(title_color)
                tvTitlePurchaseItem.text = "Позиция- ${adapterPosition + 1}"
                imEditPurchaseItem.setOnClickListener {
                    if (clickItemCallback != null) clickItemCallback!!.onEditItem()
                }
                imDeletePurchaseItem.setOnClickListener {
                    if (clickItemCallback != null) clickItemCallback!!.onDeleteItem()
                }
            }

        }

    }

    override fun onMove(startPos: Int, targetPos: Int) {
        val targetItem=mainArray[targetPos]
        mainArray[targetPos]=mainArray[startPos]
        mainArray[startPos]=targetItem
        notifyItemMoved(startPos,targetPos)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClear() {
        // когда конец опускания картинки при перетаскивании
        notifyDataSetChanged()
    }

}
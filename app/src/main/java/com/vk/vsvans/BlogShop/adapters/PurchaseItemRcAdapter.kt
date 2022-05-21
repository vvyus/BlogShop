package com.vk.vsvans.BlogShop.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.interfaces.AdapterCallback
import com.vk.vsvans.BlogShop.interfaces.ItemTouchMoveCallBack
import com.vk.vsvans.BlogShop.model.PurchaseItem

class PurchaseItemRcAdapter(val adapterCallback: AdapterCallback): RecyclerView.Adapter<PurchaseItemRcAdapter.PurchaseItemHolder>() ,
    ItemTouchMoveCallBack.ItemTouchAdapter{
// для доступа из ImageListFrag
    val mainArray=ArrayList<PurchaseItem>()
    var selected_position =RecyclerView.NO_POSITION;
    var selected_color =0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseItemHolder {
        selected_color=parent.context.resources.getColor(R.color.color_red)
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_list_frag,parent,false)
//        val viewBinding=
//            ItemListProductFragBinding.inflate(LayoutInflater.from(parent.context),parent,false)
//        return PurchaseItemHolder(viewBinding,parent.context,this)
        return PurchaseItemHolder(view,parent.context,this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: PurchaseItemHolder, position: Int) {
        holder.setData(mainArray[position])
        holder.itemView.setOnClickListener{

            if (selected_position != holder.getAdapterPosition()) {
                notifyItemChanged(selected_position)
            }else{
                //if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getPurchaseId())
            }
            selected_position = holder.adapterPosition
            notifyItemChanged(selected_position)
        }
        holder.itemView.setBackgroundColor(if (selected_position == position) selected_color else Color.TRANSPARENT)
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    fun getItem():PurchaseItem{
        if(selected_position ==RecyclerView.NO_POSITION) return PurchaseItem() else return mainArray[selected_position]
    }

//    class PurchaseItemHolder(val viewBinding: ItemListProductFragBinding, val context: Context, val adapter: PurchaseItemRcAdapter) : RecyclerView.ViewHolder(viewBinding.root) {
class PurchaseItemHolder(val view: View, val context: Context, val adapter:PurchaseItemRcAdapter) : RecyclerView.ViewHolder(view) {


        @RequiresApi(Build.VERSION_CODES.Q)
        fun setData(purchaseItem:PurchaseItem){
            val tvContent=view.findViewById<TextView>(R.id.tvContent)
            tvContent.text =purchaseItem.getContent()
            val tvTitle=view.findViewById<TextView>(R.id.tvTitle)
            tvTitle.text="Позиция- ${adapterPosition+1}"
//            viewBinding.tvContent.text=purchaseItem.getContent()

//            viewBinding.imEditImage.setOnClickListener{
//            }
//
//            val imDelete=itemView.findViewById<ImageButton>(R.id.imDelete)
//            imDelete.setOnClickListener{
//                //(context as EditPurchaseActivity).listResultArray.removeAt(adapterPosition)
//
//                adapter.mainArray.removeAt(adapterPosition)
//                adapter.notifyItemRemoved(adapterPosition)
//                adapter.adapterCallback.onItemDelete()
//                for(i in 0 until adapter.mainArray.size) adapter.notifyItemChanged(i)
//
//            }
//            viewBinding.tvTitle.text=context.resources.getStringArray(R.array.title_array)[adapterPosition]

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(list:ArrayList<PurchaseItem>, needClear:Boolean){
        if(needClear){
            mainArray.clear()
        }
        mainArray.addAll(list)
        notifyDataSetChanged()
        //adapterCallback.onItemDelete()
    }
    fun getPosition():Int{
        if(selected_position!=RecyclerView.NO_POSITION) return selected_position
        else return -1
    }
    fun deletePurchaseItem():Boolean{
        if(selected_position!=RecyclerView.NO_POSITION) {
            val pit=getItem()
            mainArray.removeAt(selected_position)
            notifyItemRemoved(selected_position)
            adapterCallback.onItemDelete(pit)
            return true
        }else{
            return false
           // Toast.makeText()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setPurchaseItem(purchaseItem:PurchaseItem, pos:Int){
        mainArray[pos]=purchaseItem;
        notifyDataSetChanged()
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
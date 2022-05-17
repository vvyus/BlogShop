package com.vk.vsvans.BlogShop.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.activity.EditPurchaseActivity
import com.vk.vsvans.BlogShop.databinding.ItemListProductFragBinding
import com.vk.vsvans.BlogShop.interfaces.AdapterCallback
import com.vk.vsvans.BlogShop.interfaces.ItemTouchMoveCallBack
import com.vk.vsvans.BlogShop.model.PurchaseItem

class PurchaseItemRcAdapter(val adapterCallback: AdapterCallback): RecyclerView.Adapter<PurchaseItemRcAdapter.PurchaseItemHolder>() ,
    ItemTouchMoveCallBack.ItemTouchAdapter{
// для доступа из ImageListFrag
    val mainArray=ArrayList<PurchaseItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseItemHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_list_product_frag,parent,false)
//        val viewBinding=
//            ItemListProductFragBinding.inflate(LayoutInflater.from(parent.context),parent,false)
//        return PurchaseItemHolder(viewBinding,parent.context,this)
        return PurchaseItemHolder(view,parent.context,this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: PurchaseItemHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

//    class PurchaseItemHolder(val viewBinding: ItemListProductFragBinding, val context: Context, val adapter: PurchaseItemRcAdapter) : RecyclerView.ViewHolder(viewBinding.root) {
class PurchaseItemHolder(val view: View, val context: Context, val adapter:PurchaseItemRcAdapter) : RecyclerView.ViewHolder(view) {


        @RequiresApi(Build.VERSION_CODES.Q)
        fun setData(purchaseItem:PurchaseItem){
            val tvContent=view.findViewById<TextView>(R.id.tvContent)
            tvContent.text =purchaseItem.getContent()
            val tvTitle=view.findViewById<TextView>(R.id.tvTitle)
            tvTitle.text="Строка- ${adapterPosition+1}"
//            viewBinding.tvContent.text=purchaseItem.getContent()

//            viewBinding.imEditImage.setOnClickListener{
//            }
//
            val imDelete=itemView.findViewById<ImageButton>(R.id.imDelete)
            imDelete.setOnClickListener{
                //(context as EditPurchaseActivity).listResultArray.removeAt(adapterPosition)

                adapter.mainArray.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
                adapter.adapterCallback.onItemDelete()
                for(i in 0 until adapter.mainArray.size) adapter.notifyItemChanged(i)

            }
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
        adapterCallback.onItemDelete()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSingleImage(purchaseItem:PurchaseItem, pos:Int){
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
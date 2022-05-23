package com.vk.vsvans.BlogShop.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.interfaces.ItemTouchMoveCallBack
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.PurchaseItem

class PurchaseItemRcAdapter(val clickItemCallback: OnClickItemCallback?): RecyclerView.Adapter<PurchaseItemRcAdapter.PurchaseItemHolder>() ,
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
        return PurchaseItemHolder(view,clickItemCallback)
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

    fun getPurchaseItem():PurchaseItem?{
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

    fun deleteProductItem(){
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
//    class PurchaseItemHolder(val viewBinding: ItemListProductFragBinding, val context: Context, val adapter: PurchaseItemRcAdapter) : RecyclerView.ViewHolder(viewBinding.root) {
class PurchaseItemHolder(val view: View,val clickItemCallback: OnClickItemCallback?) : RecyclerView.ViewHolder(view) {


        @RequiresApi(Build.VERSION_CODES.Q)
        fun setData(purchaseItem:PurchaseItem){
            val tvContent=view.findViewById<TextView>(R.id.tvContentPurchaseItem)
            tvContent.text =purchaseItem.getContent()
            val tvTitle=view.findViewById<TextView>(R.id.tvTitlePurchaseItem)
            tvTitle.text="Позиция- ${adapterPosition+1}"
            val imEditPurchaseItem=view.findViewById<ImageButton>(R.id.imEditPurchaseItem)
            imEditPurchaseItem.setOnClickListener{
                if(clickItemCallback!=null) clickItemCallback!!.onEditItem()
            }
            val imDeletePurchaseItem=view.findViewById<ImageButton>(R.id.imDeletePurchaseItem)
            imDeletePurchaseItem.setOnClickListener{
                if(clickItemCallback!=null) clickItemCallback!!.onDeleteItem()
            }
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
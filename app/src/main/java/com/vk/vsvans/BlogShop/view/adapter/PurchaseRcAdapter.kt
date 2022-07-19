package com.vk.vsvans.BlogShop.view.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.databinding.ItemPurchaseListBinding
import com.vk.vsvans.BlogShop.view.`interface`.IFilterCallBack
import com.vk.vsvans.BlogShop.view.`interface`.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.data.Purchase
import com.vk.vsvans.BlogShop.util.UtilsHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PurchaseRcAdapter(val clickItemCallback: OnClickItemCallback?): RecyclerView.Adapter<PurchaseRcAdapter.PurchaseHolder>() {

    val purchaseArray=ArrayList<Purchase>()
    var focused_position =RecyclerView.NO_POSITION;
    var selected_color =0
    private var filterCallback:IFilterCallBack?=null
    private var marked_image: Drawable?=null
    val marked_position=HashMap<Int,Purchase>()
    lateinit var binding:ItemPurchaseListBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseHolder {

        selected_color=parent.context.resources.getColor(R.color.color_red)
        marked_image=parent.resources.getDrawable(R.drawable.ic_check)
        binding= ItemPurchaseListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PurchaseHolder(binding,clickItemCallback,filterCallback)
    }
    //fill and show holder in position
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: PurchaseHolder, position: Int) {
        holder.setData(purchaseArray[position],marked_position,marked_image!!)
        holder.itemView.setOnClickListener{
            if (focused_position != holder.getAdapterPosition()) {
                notifyItemChanged(focused_position)
            }else{
                if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getPurchaseId())
            }
            focused_position = holder.adapterPosition
            notifyItemChanged(focused_position)

        }

//        if(marked_position.get(purchaseArray[position].id)==null)
//            binding.btnSelectPurchase.setImageDrawable(null)
//        else
//            binding.btnSelectPurchase.setImageDrawable(marked_image)
        //binding.btnSelectPurchase.refreshDrawableState()

        holder.itemView.setBackgroundColor(if (focused_position == position) selected_color else Color.TRANSPARENT)

    }

    override fun getItemCount(): Int {
        return purchaseArray.size
    }

    fun getPurchaseId():Int{
        if(purchaseArray.size!=0 && focused_position!=RecyclerView.NO_POSITION && focused_position<purchaseArray.size){
            return purchaseArray[focused_position].id
        }else{
            return 0
        }
    }

    fun getPurchase(): Purchase?{
        if(purchaseArray.size!=0 && focused_position!=RecyclerView.NO_POSITION && focused_position<purchaseArray.size){
            return purchaseArray[focused_position]
        }else{
            return null
        }
    }

    fun setPurchase(purchase: Purchase){
        if(purchaseArray.size!=0 && focused_position!=RecyclerView.NO_POSITION && focused_position<purchaseArray.size){
            purchaseArray[focused_position]=purchase
        }
    }

    fun addPurchase(purchase: Purchase){
        purchaseArray.add(purchase)
        Collections.sort(purchaseArray)
    }

    fun updateAdapter(newList:List<Purchase>){
        purchaseArray.clear()
        purchaseArray.addAll(newList)
        notifyDataSetChanged()
    }

    fun setFilterCallback(filterCallback:IFilterCallBack){
        this.filterCallback=filterCallback
    }

    fun getMarkedIds():ArrayList<Int>{
        return ArrayList(marked_position.keys)
    }

    fun getMarkedObjects():ArrayList<Purchase>{
        return ArrayList(marked_position.values)
    }

    fun resetMarkedPosition(){
        //binding.btnSelectPurchase.setImageDrawable(null)
        marked_position.clear()
    }

    class PurchaseHolder(val binding:ItemPurchaseListBinding,val clickItemCallback: OnClickItemCallback?,
                         val filterCallback: IFilterCallBack?): RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        fun setData(purchase: Purchase,marked_position:HashMap<Int,Purchase>,selected_image:Drawable){
            binding.apply {
                tvDescription.text= Html.fromHtml(purchase.content_html,0)
                tvSummaPuchase.text= purchase.summa.toString()
                tvSeller.text=purchase.sellername
                tvPurchaseTime.setText(UtilsHelper.getDate(purchase.time))
                tvPurchaseTime.setOnClickListener{
                    if(filterCallback!=null) filterCallback.onTimeClick(purchase.time)
                }
                tvSeller.setOnClickListener{
                    if(filterCallback!=null) filterCallback.onSellerClick(purchase)
                }

                btnSelectPurchase.setOnClickListener{
                    if(btnSelectPurchase.drawable==null){
                        btnSelectPurchase.setImageDrawable(selected_image)
                        if(marked_position.get(purchase.id)==null)marked_position.put(purchase.id,purchase)
                    }else{
                        btnSelectPurchase.setImageDrawable(null)
                        if(marked_position.get(purchase.id)!=null)marked_position.remove(purchase.id)
                    }
                }
                //tvTitle.tag= com.vk.vsvans.BlogShop.helper.Tag(purchase.id,se)
                //btnSelectPurchase.refreshDrawableState()
                if(marked_position.get(purchase.id)==null) btnSelectPurchase.setImageDrawable(null)
            }
            showEditPanel(true)
        }


        private fun showEditPanel(isOwner:Boolean){
            if(isOwner) {
                binding.editPanel.visibility= View.VISIBLE
            }else{
                binding.editPanel.visibility= View.GONE
            }
        }


    }



}
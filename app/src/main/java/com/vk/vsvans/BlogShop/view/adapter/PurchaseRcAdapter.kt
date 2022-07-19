package com.vk.vsvans.BlogShop.view.adapter

import android.graphics.Color
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

class PurchaseRcAdapter(val clickItemCallback: OnClickItemCallback?): RecyclerView.Adapter<PurchaseRcAdapter.PurchaseHolder>() {

    val purchaseArray=ArrayList<Purchase>()
    var focused_position =RecyclerView.NO_POSITION;
    var selected_color =0
    private var filterCallback:IFilterCallBack?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseHolder {

        selected_color=parent.context.resources.getColor(R.color.color_red)

        val binding=
            ItemPurchaseListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PurchaseHolder(binding,clickItemCallback,filterCallback)
    }
    //fill and show holder in position
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: PurchaseHolder, position: Int) {
        holder.setData(purchaseArray[position])
        holder.itemView.setOnClickListener{
            if (focused_position != holder.getAdapterPosition()) {
                notifyItemChanged(focused_position)
            }else{
                if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getPurchaseId())
            }
            focused_position = holder.adapterPosition
            notifyItemChanged(focused_position)

        }
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

    class PurchaseHolder(val binding:ItemPurchaseListBinding,val clickItemCallback: OnClickItemCallback?,val filterCallback: IFilterCallBack?): RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        fun setData(purchase: Purchase){
            binding.apply {
                tvDescription.text= Html.fromHtml(purchase.content_html,0)
//                tvDescription.setMovementMethod(LinkMovementMethod.getInstance())
//                Linkify.addLinks(tvDescription, Linkify.ALL);
                tvSummaPuchase.text= purchase.summa.toString()
                tvSeller.text=purchase.sellername
                tvPurchaseTime.setText(UtilsHelper.getDate(purchase.time))
                tvPurchaseTime.setOnClickListener{
                    //println(tvPurchaseTime.text)
                    //if(clickItemCallback!=null) clickItemCallback!!.onTimeClick()
                    if(filterCallback!=null) filterCallback.onTimeClick(purchase.time)
                }
                tvSeller.setOnClickListener{
                    //mainActivity!!.onSellerClick(purchase)
                    if(filterCallback!=null) filterCallback.onSellerClick(purchase)
                }
                //tvTitle.tag= com.vk.vsvans.BlogShop.helper.Tag(purchase.id,se)
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
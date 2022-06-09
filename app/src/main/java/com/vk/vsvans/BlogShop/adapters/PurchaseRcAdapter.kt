package com.vk.vsvans.BlogShop.adapters

import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.databinding.PurchaseListItemBinding
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.Purchase
import com.vk.vsvans.BlogShop.utils.UtilsHelper
import com.vk.vsvans.BlogShop.utils.makeSpannableString

class PurchaseRcAdapter(val clickItemCallback: OnClickItemCallback?): RecyclerView.Adapter<PurchaseRcAdapter.PurchaseHolder>() {

    val purchaseArray=ArrayList<Purchase>()
    var selected_position =RecyclerView.NO_POSITION;
    var selected_color =0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseHolder {

        selected_color=parent.context.resources.getColor(R.color.color_red)

        val binding=
            PurchaseListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PurchaseHolder(binding)
    }
    //fill and show holder in position
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: PurchaseHolder, position: Int) {
        holder.setData(purchaseArray[position])
        holder.itemView.setOnClickListener{
            if (selected_position != holder.getAdapterPosition()) {
                notifyItemChanged(selected_position)
            }else{
                if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getPurchaseId())
            }
            selected_position = holder.adapterPosition
            notifyItemChanged(selected_position)

        }
        holder.itemView.setBackgroundColor(if (selected_position == position) selected_color else Color.TRANSPARENT)

    }

    override fun getItemCount(): Int {
        return purchaseArray.size
    }
    fun getPurchaseId():Int{
        if(purchaseArray.size!=0 && selected_position!=RecyclerView.NO_POSITION && selected_position<purchaseArray.size){
            return purchaseArray[selected_position].id
        }else{
            return 0
        }
    }
    fun updateAdapter(newList:List<Purchase>){
        purchaseArray.clear()
        purchaseArray.addAll(newList)
        notifyDataSetChanged()
    }

    class PurchaseHolder(val binding:PurchaseListItemBinding): RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        fun setData(purchase:Purchase){
            binding.apply {
                tvDescription.text= Html.fromHtml(purchase.content_html,0)
                tvSummaPuchase.text= purchase.summa.toString()
                tvTitle.text=purchase.title
                tvPurchaseTime.setText(UtilsHelper.getDate(purchase.time))
                tvPurchaseTime.setOnClickListener{
                    println(tvPurchaseTime.text)
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
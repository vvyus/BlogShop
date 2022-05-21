package com.vk.vsvans.BlogShop.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.databinding.ItemProductListBinding
import com.vk.vsvans.BlogShop.databinding.PurchaseListItemBinding
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.Product
import com.vk.vsvans.BlogShop.model.Purchase

class ProductRcAdapter(val clickItemCallback: OnClickItemCallback?): RecyclerView.Adapter<ProductRcAdapter.ProductHolder>() {
    val productArray=ArrayList<Product>()
    var selected_position =RecyclerView.NO_POSITION;
    var selected_color =0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        selected_color=parent.context.resources.getColor(R.color.color_red)

        val binding= ItemProductListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductRcAdapter.ProductHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.setData(productArray[position])
        holder.itemView.setOnClickListener{
            if (selected_position != holder.getAdapterPosition()) {
                notifyItemChanged(selected_position)
//            }else{
//                if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getProductId())
            }

            selected_position = holder.adapterPosition
            notifyItemChanged(selected_position)
            if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getProductId())
        }
        holder.itemView.setBackgroundColor(if (selected_position == position) selected_color else Color.TRANSPARENT)
    }

    override fun getItemCount(): Int {
        return productArray.size
    }

    fun getProductId():Int{
        if(productArray.size!=0 && selected_position!=RecyclerView.NO_POSITION && selected_position<productArray.size){
            return productArray[selected_position].id
        }else{
            return 0
        }
    }

    fun updateAdapter(newList:List<Product>){
        productArray.clear()
        productArray.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateAdapter(product:Product){
        productArray.add(product)
        notifyDataSetChanged()
    }

    fun deleteProductItem(){
        if(selected_position!=RecyclerView.NO_POSITION) {
             productArray.removeAt(selected_position)
            notifyItemRemoved(selected_position)
            //reset selected position and selectedId in activity
            selected_position=RecyclerView.NO_POSITION
            if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getProductId())
            //adapterCallback.onItemDelete(pit)
        }
    }

    class ProductHolder(val binding: ItemProductListBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(product:Product){
            binding.apply {
                tvTitle.text=product.title
             }
        }
    }
}
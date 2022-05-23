package com.vk.vsvans.BlogShop.adapters

import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.databinding.ItemProductListBinding
import com.vk.vsvans.BlogShop.databinding.PurchaseListItemBinding
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.Product
import com.vk.vsvans.BlogShop.model.Purchase
import kotlinx.coroutines.NonDisposableHandle.parent

class ProductRcAdapter(val clickItemCallback: OnClickItemCallback?): RecyclerView.Adapter<ProductRcAdapter.ProductHolder>() {
    val productArray=ArrayList<Product>()
    var selected_position =RecyclerView.NO_POSITION;
    var selected_color =0
    //lateinit var binding:ItemProductListBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        selected_color=parent.context.resources.getColor(R.color.color_red)

        val binding= ItemProductListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductRcAdapter.ProductHolder(binding,clickItemCallback)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.setData(productArray[position])
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
        val llButtons=holder.itemView.findViewById<LinearLayout>(R.id.llButtons)
        llButtons.visibility=if (selected_position == position) View.VISIBLE else View.GONE
        //binding.llButtons.visibility=if (selected_position == position) View.VISIBLE else View.GONE
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

    fun getProduct():Product?{
        if(productArray.size!=0 && selected_position!=RecyclerView.NO_POSITION && selected_position<productArray.size){
            return productArray[selected_position]
        }else{
            return null
        }
    }

    fun updateAdapter(newList:List<Product>){
        productArray.clear()
        productArray.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateAdapterInsert(product:Product){
        productArray.add(product)
        notifyDataSetChanged()
    }

    fun updateAdapterEdit(product:Product){
        if(selected_position!=RecyclerView.NO_POSITION) {
            productArray[selected_position]=product
            notifyDataSetChanged()
        }
    }

    fun deleteProductItem(){
        if(selected_position!=RecyclerView.NO_POSITION) {
             productArray.removeAt(selected_position)
            notifyItemRemoved(selected_position)
            //reset selected position and selectedId in activity
            unSelectItem()
        }
    }

    fun selectItem(position:Int){
        selected_position=position
        if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getProductId())
    }

    fun unSelectItem(){
        selected_position=RecyclerView.NO_POSITION
        if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getProductId())
    }

    class ProductHolder(val binding: ItemProductListBinding,val clickItemCallback: OnClickItemCallback?): RecyclerView.ViewHolder(binding.root) {
        fun setData(product:Product){
            binding.apply {
                tvProductName.text=product.title
                imEditProduct.setOnClickListener{
                    if(clickItemCallback!=null) clickItemCallback!!.onEditItem()
                }
                imDeleteProduct.setOnClickListener{
                    if(clickItemCallback!=null) clickItemCallback!!.onDeleteItem()
                }

            }
        }
    }
}
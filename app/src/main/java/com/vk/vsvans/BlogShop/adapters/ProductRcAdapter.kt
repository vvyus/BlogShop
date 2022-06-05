package com.vk.vsvans.BlogShop.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.databinding.ItemProductListBinding
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.Product


class ProductRcAdapter(val clickItemCallback: OnClickItemCallback?): RecyclerView.Adapter<ProductRcAdapter.ProductHolder>() {
    val productArray=ArrayList<Product>()
    // indexed product
    val nodeList=HashMap<Int, Product>()
    var selected_position =RecyclerView.NO_POSITION;
    var selected_color =0
    var offset16=0
    var ic_expanded_less=0
    lateinit var binding:ItemProductListBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        selected_color=parent.context.resources.getColor(R.color.color_red)
        offset16 = dpToPx(16, parent.context);
        binding= ItemProductListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductRcAdapter.ProductHolder(binding,clickItemCallback)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.setData(productArray[position],holder)
        holder.itemView.setOnClickListener{

            notifyItemChanged(selected_position)
            if (selected_position != holder.getAdapterPosition()) {
                selectItem(holder.adapterPosition)
            }else{
                unSelectItem()
            }
            notifyItemChanged(selected_position)

        }
        //
        val leftMargin: Int = offset16 * productArray[position].level
        var p:ViewGroup.MarginLayoutParams=holder.itemView.getLayoutParams() as ViewGroup.MarginLayoutParams//
        p.setMargins(leftMargin, 0, 0, 0)

        //
        holder.itemView.setBackgroundColor(if (selected_position == position) selected_color else Color.TRANSPARENT)
        val llButtons=holder.itemView.findViewById<LinearLayout>(R.id.llButtons)
        llButtons.visibility=if (selected_position == position) View.VISIBLE else View.GONE
        //!
        val product=productArray[position]
        val parentid=product.idparent
        if(parentid==0 || parentid>0 && (nodeList.get(parentid) as Product).expanded) {
            holder.itemView.visibility=View.VISIBLE
        } else {
            holder.itemView.visibility=View.GONE
        }
        val count=product.count
        val expi=holder.itemView.findViewById<ImageView>(R.id.expandableIndicator)
        expi.visibility=if(count>0) View.VISIBLE else View.GONE
        if(product.expanded){
            expi.setImageResource(R.drawable.ic_expand_less)
        } else {
            expi.setImageResource(R.drawable.ic_expand_more)
        }
        //
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

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(newList:List<Product>){
        productArray.clear()
        productArray.addAll(newList)
        nodeList.clear()
        for(product in productArray){
            nodeList.put(product.id,product)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterInsert(product:Product){
        productArray.add(product)
        nodeList.put(product.id,product)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
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

    fun addProductItem(product:Product) {}

    class ProductHolder(val binding: ItemProductListBinding,val clickItemCallback: OnClickItemCallback?): RecyclerView.ViewHolder(binding.root) {
        fun setData(product:Product,holder: ProductHolder){
            binding.apply {
                tvProductName.text=product.name
                imEditProduct.setOnClickListener{
                    if(clickItemCallback!=null) clickItemCallback!!.onEditItem()
                }
                imDeleteProduct.setOnClickListener{
                    if(clickItemCallback!=null) clickItemCallback!!.onDeleteItem()
                }
                imNewProduct.setOnClickListener{
                    if(clickItemCallback!=null) clickItemCallback!!.onNewItem(product)
                }
                expandableIndicator.setOnClickListener{
                    product.expanded=!product.expanded
//                    if(product.expanded){
//                        binding.expandableIndicator.setImageResource(R.drawable.ic_expand_less)
//
//                    } else {
//                        binding.expandableIndicator.setImageResource(R.drawable.ic_expand_more)
//                    }
                    if(clickItemCallback!=null) clickItemCallback!!.refreshItem()
                }
            }
        }
    }

    fun dpToPx(dp: Int, context: Context): Int {
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshItem() {
        notifyDataSetChanged()
    }
}
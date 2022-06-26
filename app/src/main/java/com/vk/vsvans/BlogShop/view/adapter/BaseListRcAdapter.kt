package com.vk.vsvans.BlogShop.view.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.databinding.ItemBaseListListBinding
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.data.BaseList
import com.vk.vsvans.BlogShop.utils.UtilsString

class BaseListRcAdapter(val clickItemCallback: OnClickItemCallback?): RecyclerView.Adapter<BaseListRcAdapter.BaseListHolder>() {
    var BaseListArray=ArrayList<BaseList>()
    var childArray=ArrayList<BaseList>()
    // indexed BaseList
    val nodeList=HashMap<Int, BaseList>()
    var selected_position = RecyclerView.NO_POSITION;
    var selected_color =0
    var offset16=0
    var ic_expanded_less=0
    lateinit var binding: ItemBaseListListBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseListHolder {
        selected_color=parent.context.resources.getColor(R.color.color_red)
        offset16 = UtilsString.dpToPx(16, parent.context)
        binding= ItemBaseListListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BaseListHolder(binding,clickItemCallback)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseListHolder, position: Int) {

        //       if(BaseList.id==1){
        holder.setData(BaseListArray[position],holder)
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
        val BaseList=BaseListArray[position]
        val leftMargin: Int = offset16 * BaseList.level

        //
        holder.itemView.setBackgroundColor(if (selected_position == position) selected_color else Color.TRANSPARENT)
        val llButtons=holder.itemView.findViewById<LinearLayout>(R.id.llButtons)
        llButtons.visibility=if (selected_position == position) View.VISIBLE else View.GONE
        //!

        val parentid=BaseList.idparent
        //(nodeList.get(parentid) as BaseList).expanded
        if(parentid==BaseList.id || parentid>0 && isParentExpanded(parentid)) {
            holder.itemView.visibility= View.VISIBLE
            //
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            val lp=holder.itemView.getLayoutParams()
            var p: ViewGroup.MarginLayoutParams=lp as ViewGroup.MarginLayoutParams//
            p.setMargins(leftMargin, 0, 0, 0)

        } else {
            holder.itemView.visibility= View.GONE
            holder.itemView.setLayoutParams(RecyclerView.LayoutParams(0, 0))
        }

        holder.itemView.requestLayout()
        val count=BaseList.count
        val expi=holder.itemView.findViewById<ImageView>(R.id.expandableIndicator)
        expi.visibility=if(count>0) View.VISIBLE else View.GONE
        if(BaseList.expanded){
            expi.setImageResource(R.drawable.ic_expand_less)
        } else {
            expi.setImageResource(R.drawable.ic_expand_more)
        }
        expi.setOnClickListener {
            BaseList.expanded = !BaseList.expanded
            this@BaseListRcAdapter.notifyDataSetChanged()
        }
        //
//        }//if
    }

    override fun getItemCount(): Int {
        return BaseListArray.size
    }
    fun isParentExpanded(parentId:Int):Boolean{
        var id=parentId
        while(true){
            if(nodeList.get(id)!=null) {
                val BaseList = nodeList.get(id) as BaseList
                if (BaseList != null) {
                    if (!BaseList.expanded) return false
                    if (BaseList.id == BaseList.idparent) break
                    id = BaseList.idparent
                } else break
            } else break
        }
        return true
    }

    fun getBaseListId():Int{
        if(BaseListArray.size!=0 && selected_position!= RecyclerView.NO_POSITION && selected_position<BaseListArray.size){
            return BaseListArray[selected_position].id
        }else{
            return 0
        }
    }

    fun getBaseList(): BaseList?{
        if(BaseListArray.size!=0 && selected_position!= RecyclerView.NO_POSITION && selected_position<BaseListArray.size){
            return BaseListArray[selected_position]
        }else{
            return null
        }
    }
    fun getParent(): BaseList?{
        if(BaseListArray.size!=0 && selected_position!= RecyclerView.NO_POSITION && selected_position<BaseListArray.size){
            val BaseList=BaseListArray[selected_position]
            val parent=if(BaseList.id==BaseList.idparent) null else nodeList.get(BaseList.idparent)
            return parent
        }else{
            return null
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(newList:List<BaseList>){
        BaseListArray.clear()
        BaseListArray.addAll(newList)
        nodeList.clear()
        for(BaseList in BaseListArray){
            nodeList.put(BaseList.id,BaseList)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterInsert(BaseList: BaseList){
        BaseListArray.add(BaseList)
        val list=BaseListArray.sorted()
        BaseListArray.clear()
        BaseListArray.addAll(list)
        nodeList.put(BaseList.id,BaseList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterEdit(BaseList: BaseList){
        if(selected_position!= RecyclerView.NO_POSITION) {
            BaseListArray[selected_position]=BaseList
            nodeList.put(BaseList.id,BaseList)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterParent(oldparent: BaseList?, parent: BaseList?, BaseList: BaseList){
        val list=BaseListArray.sorted()
        BaseListArray.clear()
        BaseListArray.addAll(list)
        nodeList.put(BaseList.id,BaseList)
        if(oldparent!=null){
            nodeList.put(oldparent.id,oldparent)
        }
        if(parent!=null){
            nodeList.put(parent.id,parent)
        }
        notifyDataSetChanged()
    }

    fun setChildren(parentid:Int,newfullPath:String,newlevel:Int){

        for(i in 0 until BaseListArray.size){
            if(BaseListArray[i].idparent==parentid && BaseListArray[i].idparent!=BaseListArray[i].id) {
                childArray.add(BaseListArray[i])
                BaseListArray[i].fullpath=newfullPath+BaseListArray[i].id
                BaseListArray[i].level=newlevel+1
                if(BaseListArray[i].count>0)setChildren(BaseListArray[i].id,BaseListArray[i].fullpath,BaseListArray[i].level)
            }
        }
    }
    //    @SuppressLint("NotifyDataSetChanged")
//    fun updateAdapterEdit(oldparent:BaseList,parent:BaseList,BaseList:BaseList){
//        if(selected_position!=RecyclerView.NO_POSITION) {
//            BaseListArray[selected_position]=BaseList
//            nodeList.put(BaseList.id,BaseList)
//            notifyDataSetChanged()
//        }
//    }
    fun deleteBaseListItem(){
        if(selected_position!= RecyclerView.NO_POSITION) {
            val BaseList=getBaseList()
            val parent=nodeList.get(BaseList!!.idparent)
            if(parent!=null)parent!!.count--
            nodeList.remove(BaseList.id)
            BaseListArray.removeAt(selected_position)
            notifyItemRemoved(selected_position)
            //reset selected position and selectedId in activity
            unSelectItem()
        }
    }

    fun selectItem(position:Int){
        selected_position=position
        if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getBaseListId())
    }

    fun unSelectItem(){
        selected_position= RecyclerView.NO_POSITION
        if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getBaseListId())
    }

    fun addBaseListItem(BaseList: BaseList) {}

    class BaseListHolder(val binding: ItemBaseListListBinding, val clickItemCallback: OnClickItemCallback?): RecyclerView.ViewHolder(binding.root) {
        fun setData(BaseList: BaseList, holder: BaseListHolder){
            binding.apply {
                tvBaseListName.text=BaseList.name
                imEditBaseList.setOnClickListener{
                    if(clickItemCallback!=null) clickItemCallback!!.onEditItem()
                }
                imDeleteBaseList.setOnClickListener{
                    if(clickItemCallback!=null) clickItemCallback!!.onDeleteItem()
                }
                imNewBaseList.setOnClickListener{
                    if(clickItemCallback!=null) clickItemCallback!!.onNewItem(BaseList)
                }
                imParent.setOnClickListener{
                    if(clickItemCallback!=null) clickItemCallback!!.onParentItem()
                }
//                expandableIndicator.setOnClickListener{
//                    BaseList.expanded=!BaseList.expanded
//                    if(clickItemCallback!=null) clickItemCallback!!.refreshItem()
//                }
            }
        }
    }

//    fun dpToPx(dp: Int, context: Context): Int {
//        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
//        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
//    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshItem() {
        notifyDataSetChanged()
    }
}
package com.vk.vsvans.BlogShop.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.model.data.BaseAmount
import com.vk.vsvans.BlogShop.model.data.BaseAmountType
import com.vk.vsvans.BlogShop.model.data.ProductAmount
import com.vk.vsvans.BlogShop.util.FilterForActivity
import com.vk.vsvans.BlogShop.util.UtilsString
import com.vk.vsvans.BlogShop.view.`interface`.ICallBackAmountAdapter

class BaseAmountRcAdapter(val callBack:ICallBackAmountAdapter, val filterForActivity: FilterForActivity,val baseAmountType: BaseAmountType) : RecyclerView.Adapter<BaseAmountRcAdapter.SpViewHolder>() {

    private val mainList=ArrayList<BaseAmount>()
    //private val context=context
    var offset16=0
    var selected_position = RecyclerView.NO_POSITION
    var selected_color =0
//    private var selected_id=id_selected
//    var set_selected_id=false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {

        selected_color=parent.context.resources.getColor(R.color.gray_light,parent.context.theme)
        offset16 = UtilsString.dpToPx(16, parent.context)
        val view=
            LayoutInflater.from(parent.context).inflate(R.layout.item_product_amount_list,parent,false)
        return SpViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainList[position])
        holder.itemView.setOnClickListener{

            notifyItemChanged(selected_position)
            if (selected_position != holder.getAdapterPosition()) {
                selectItem(holder.adapterPosition)
            }else{

                if(baseAmountType==BaseAmountType.PRODUCT) {
                    val tvProdNameAmount=holder.itemView.findViewById<TextView>(R.id.tvProdNameAmount)
                    filterForActivity.content = tvProdNameAmount!!.text.toString()
                    filterForActivity.dates_begin = null
                    filterForActivity.dates_end = null
                    filterForActivity.idSeller = null
                }else if(baseAmountType==BaseAmountType.SELLER) {
                    filterForActivity.content = null
                    filterForActivity.dates_begin = null
                    filterForActivity.dates_end = null
                    filterForActivity.idSeller = mainList[position].id

                }
                unSelectItem()

            }
            notifyItemChanged(selected_position)

        }
        val itemList=mainList[position]
        val leftMargin: Int = offset16 * itemList.level
        val parentid=itemList.idparent
        if(parentid==itemList.id || parentid>0){
            holder.itemView.visibility= View.VISIBLE
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

//        if(selected_id!=0 && mainList[position].id==selected_id && set_selected_id==false) {
//            selected_position=position
//            set_selected_id=true
//        }

        holder.itemView.setBackgroundColor(if (selected_position == position) selected_color else Color.TRANSPARENT)
    }

    fun selectItem(position:Int){
        selected_position=position
        //if(clickItemCallback!=null) clickItemCallback!!.onClickItem(getBaseListId())
    }

    fun unSelectItem(){

        val selectItem=getBaseAmount()
        if(selectItem!=null){
        }else selected_position= RecyclerView.NO_POSITION
        callBack.onClickItem()
    }

    fun setSelectionPosById(id:Int):Int{
        if(id>0){
            for(i in 0 until mainList.size){
                if(mainList[i].id==id){
                    selected_position=i
                    //break
                    return i
                }
            }
        }
        return -1
    }

    fun getBaseAmount(): BaseAmount?{
        if(mainList.size!=0 && selected_position!= RecyclerView.NO_POSITION && selected_position<mainList.size){
            return mainList[selected_position]
        }else{
            return null
        }
    }
    override fun getItemCount(): Int {

        return mainList.size
    }
    // ?????????????? viewholder ?????????????? ??????????????????
    //var context ?????? ?????????????????????? ?? ???????????????? ???????????? ???????????? ?????????????????? ???????????? ????????????????
    class SpViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        fun setData(baseAmount: BaseAmount){
            val tvProdNameAmount=itemView.findViewById<TextView>(R.id.tvProdNameAmount)
            val tvYearAmount=itemView.findViewById<TextView>(R.id.tvYearAmount)
            val tvMonthAmount=itemView.findViewById<TextView>(R.id.tvMonthAmount)
            val tvWeekAmount=itemView.findViewById<TextView>(R.id.tvWeekAmount)
            tvProdNameAmount.text=baseAmount.name
            tvYearAmount.text= UtilsString.format_string(baseAmount.yearAmount.toString())//baseAmount.yearAmount.toString()
            tvMonthAmount.text= UtilsString.format_string(baseAmount.monthAmount.toString())
            tvWeekAmount.text= UtilsString.format_string(baseAmount.weekAmount.toString())
            // ?????????????????????? onClick ???????????????????? ????????????????
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
        }
    }

    fun setSelectedPositionById(id:Int?):Int{
        for(i in 0 until mainList.size){
            if(mainList[i].id==id){
                selected_position=i
                return i
            }
        }
        return -1
    }

    fun setSelectedPositionByContent(content:String?):Int{
        for(i in 0 until mainList.size){
            if(mainList[i].name.lowercase().startsWith(content!!.lowercase())){
                selected_position=i
                return i
            }
        }
        return -1
    }

    fun updateAdapter(list: ArrayList<BaseAmount>){

        mainList.clear()
        mainList.addAll(list)
        //if(baseAmountType==BaseAmountType.PRODUCT)
        fillNodesAmount()
        notifyDataSetChanged()
    }

    fun fillNodesAmount(){
        if(mainList.size==0) return
        var i=mainList.size-1
        val nodesHeap=HashMap<Int,Array<Double>>()
        var idparent=0
        idparent=mainList[i].idparent
        while(i>0){
            while(mainList[i].id!=mainList[i].idparent && mainList[i].count==0){
                // ???????????????? ?????????? ?????????? ???????????????? leaves
                if(idparent!=mainList[i].idparent && mainList[i].count==0){
                    idparent=mainList[i].idparent
                }
                if(i>0) --i else break
            }

            // its super(root) node for leaves and simplenodes
            if(mainList[i].id==mainList[i].idparent && mainList[i].count>0){
                if(nodesHeap.get(mainList[i].id)!=null){
                    mainList[i].yearAmount+= nodesHeap.get(mainList[i].id)!![0]
                    mainList[i].monthAmount+= nodesHeap.get(mainList[i].id)!![1]
                    mainList[i].weekAmount+= nodesHeap.get(mainList[i].id)!![2]
                }
                idparent=mainList[i].idparent

                // its simple(in root node) node for leaves
            }else if(mainList[i].id!=mainList[i].idparent && mainList[i].count>0 && mainList[i].id==idparent) {
                //!mainList[i].yearAmount fill in query for simple and root node
                val id=mainList[i].id
                if(nodesHeap.get(id)!=null) {
                    mainList[i].yearAmount += nodesHeap.get(id)!![0]
                    mainList[i].monthAmount += nodesHeap.get(id)!![1]
                    mainList[i].weekAmount += nodesHeap.get(id)!![2]
                }
                idparent = mainList[i].idparent
                val arrayNode=nodesHeap.get(idparent)
                if(arrayNode==null) {
                    nodesHeap.put(idparent, arrayOf(mainList[i].yearAmount,mainList[i].monthAmount,mainList[i].weekAmount))//yAmount,mAmount,wAmount))
                    //println("${mainList[i].name} ${yAmount}")
                }
                else {
                    nodesHeap.put(idparent, arrayOf(arrayNode[0]+mainList[i].yearAmount,arrayNode[1]+mainList[i].monthAmount,arrayNode[2]+mainList[i].weekAmount))
                }
            }
            if(i>0) --i else break
        }
        if(mainList[i].id==mainList[i].idparent && mainList[i].count>0){
            if(nodesHeap.get(mainList[i].id)!=null){
                mainList[i].yearAmount+= nodesHeap.get(mainList[i].id)!![0]
                mainList[i].monthAmount+= nodesHeap.get(mainList[i].id)!![1]
                mainList[i].weekAmount+= nodesHeap.get(mainList[i].id)!![2]
            }
        }

    }
}
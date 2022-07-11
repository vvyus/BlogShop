package com.vk.vsvans.BlogShop.view.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.model.data.BaseList
import com.vk.vsvans.BlogShop.model.data.ProductAmount
import com.vk.vsvans.BlogShop.util.FilterForActivity
import com.vk.vsvans.BlogShop.util.UtilsString
import com.vk.vsvans.BlogShop.view.`interface`.ICallBackAmountAdapter

class ProductAmountRcAdapter(val callBack:ICallBackAmountAdapter,val filterForActivity: FilterForActivity) : RecyclerView.Adapter<ProductAmountRcAdapter.SpViewHolder>() {

    private val mainList=ArrayList<ProductAmount>()
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
                val tvProdNameAmount=holder.itemView.findViewById<TextView>(R.id.tvProdNameAmount)
                filterForActivity.content=tvProdNameAmount!!.text.toString()
                filterForActivity.dates_begin=null
                filterForActivity.dates_end=null
                filterForActivity.idSeller=null

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

        val selectItem=getProductAmount()
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

    fun getProductAmount(): ProductAmount?{
        if(mainList.size!=0 && selected_position!= RecyclerView.NO_POSITION && selected_position<mainList.size){
            return mainList[selected_position]
        }else{
            return null
        }
    }
    override fun getItemCount(): Int {

        return mainList.size
    }
    // столько viewholder сколько элементов
    //var context для доступности в функциях класса аналог переменой класса контекст
    class SpViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        fun setData(productAmount: ProductAmount){
            val tvProdNameAmount=itemView.findViewById<TextView>(R.id.tvProdNameAmount)
            val tvYearAmount=itemView.findViewById<TextView>(R.id.tvYearAmount)
            val tvMonthAmount=itemView.findViewById<TextView>(R.id.tvMonthAmount)
            val tvWeekAmount=itemView.findViewById<TextView>(R.id.tvWeekAmount)
            tvProdNameAmount.text=productAmount.name
            tvYearAmount.text= UtilsString.format_string(productAmount.yearAmount.toString())//productAmount.yearAmount.toString()
            tvMonthAmount.text= UtilsString.format_string(productAmount.monthAmount.toString())
            tvWeekAmount.text= UtilsString.format_string(productAmount.weekAmount.toString())
            // присваиваем onClick выбранному элементу
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
        }
    }

    fun updateAdapter(list: ArrayList<ProductAmount>){

        mainList.clear()
        mainList.addAll(list)
        fillParentAmount()
        notifyDataSetChanged()
    }

    fun fillParentAmount(){
        var i=mainList.size-1
        var yAmount=0.0;
        var y_Amount=0.0;//копилка для simpleparent + листья in superparent
        var idparent=0
        idparent=mainList[i].idparent
        while(i>0){
            //idparent=mainList[i].idparent
            // оработка leaves count==0
            while(mainList[i].id!=mainList[i].idparent && mainList[i].count==0){
                // сменился вдруг новый владелец leaves
                if(idparent!=mainList[i].idparent && mainList[i].count==0){
                    idparent=mainList[i].idparent
                    // пошло в копилку superparent
                    y_Amount+=yAmount
                    yAmount=0.0
                }
                yAmount+=mainList[i].yearAmount
                --i
            }
            // its super parent for leaves and simpleparent
            if(mainList[i].id==mainList[i].idparent && mainList[i].count>0){
                mainList[i].yearAmount=y_Amount+yAmount
                idparent=mainList[i].idparent
                yAmount=0.0
                y_Amount=0.0
                // its simple parent for leaves
            }else if(mainList[i].id!=mainList[i].idparent && mainList[i].count>0 && mainList[i].id==idparent) {
                //mainList[i].yearAmount fill in query for simple parent
                idparent = mainList[i].idparent
                y_Amount += yAmount
                yAmount = 0.0
            }
            --i
        }
    }
}
package com.vk.vsvans.BlogShop.interfaces

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchMoveCallBack(val adapter:ItemTouchAdapter): ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlag=ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlag,0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onMove(viewHolder.adapterPosition,target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        //делаем полупрозрачным при начале выбора итема перетаскивания
        if(actionState!=ItemTouchHelper.ACTION_STATE_IDLE){
            viewHolder?.itemView?.alpha=0.5f
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        // уже без nul то есть без ?
        viewHolder.itemView.alpha=1.0f
        // перерисуем адаптер
        adapter.onClear()
        super.clearView(recyclerView, viewHolder)
    }

    interface ItemTouchAdapter{
        fun onMove(startPos:Int,targetPos:Int)
        fun onClear()
    }
}
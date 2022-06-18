package com.vk.vsvans.BlogShop.interfaces

import com.vk.vsvans.BlogShop.model.BaseList
import com.vk.vsvans.BlogShop.model.Product
import com.vk.vsvans.BlogShop.model.Purchase

interface OnClickItemCallback {
    fun onClickItem(id:Int)
    fun onEditItem()
    fun onDeleteItem()
    fun onNewItem(parent: BaseList)
    fun refreshItem()
    fun onParentItem()
    fun onTimeClick()
}
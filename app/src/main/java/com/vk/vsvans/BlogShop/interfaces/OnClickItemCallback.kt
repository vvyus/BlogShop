package com.vk.vsvans.BlogShop.interfaces

import com.vk.vsvans.BlogShop.model.Product

interface OnClickItemCallback {
    fun onClickItem(id:Int)
    fun onEditItem()
    fun onDeleteItem()
    fun onNewItem(parent:Product)
    fun refreshItem()
}
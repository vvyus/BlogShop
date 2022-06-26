package com.vk.vsvans.BlogShop.view.`interface`

import com.vk.vsvans.BlogShop.model.data.BaseList

interface OnClickItemCallback {
    fun onClickItem(id:Int)
    fun onEditItem()
    fun onDeleteItem()
    fun onNewItem(parent: BaseList)
    fun refreshItem()
    fun onParentItem()
}
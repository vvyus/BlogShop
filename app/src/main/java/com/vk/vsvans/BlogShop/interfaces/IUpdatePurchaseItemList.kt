package com.vk.vsvans.BlogShop.interfaces

import com.vk.vsvans.BlogShop.model.data.PurchaseItem

interface IUpdatePurchaseItemList {
    fun onUpdatePurchaseItemList(pi: PurchaseItem)
}
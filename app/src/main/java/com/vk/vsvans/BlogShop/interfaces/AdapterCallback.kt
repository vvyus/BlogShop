package com.vk.vsvans.BlogShop.interfaces

import com.vk.vsvans.BlogShop.model.PurchaseItem

interface AdapterCallback {
    fun onItemDelete(pit:PurchaseItem)
}
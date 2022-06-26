package com.vk.vsvans.BlogShop.interfaces

import com.vk.vsvans.BlogShop.model.data.PurchaseItem

interface AdapterCallback {
    fun onItemDelete(pit: PurchaseItem)
}
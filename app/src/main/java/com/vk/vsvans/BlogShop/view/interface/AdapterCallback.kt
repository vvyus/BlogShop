package com.vk.vsvans.BlogShop.view.`interface`

import com.vk.vsvans.BlogShop.model.data.PurchaseItem

interface AdapterCallback {
    fun onItemDelete(pit: PurchaseItem)
}
package com.vk.vsvans.BlogShop.interfaces

import com.vk.vsvans.BlogShop.model.Product
import com.vk.vsvans.BlogShop.model.PurchaseItem

interface IUpdateProductItemList {
    fun onUpdateProductItemList(product: Product)
}
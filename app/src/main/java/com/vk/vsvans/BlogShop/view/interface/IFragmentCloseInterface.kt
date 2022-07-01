package com.vk.vsvans.BlogShop.view.`interface`

import com.vk.vsvans.BlogShop.model.data.PurchaseItem

interface IFragmentCloseInterface {
    fun onFragClose(list:ArrayList<PurchaseItem>)
}
package com.vk.vsvans.BlogShop.view.`interface`

import com.vk.vsvans.BlogShop.model.data.PurchaseItem
import com.vk.vsvans.BlogShop.util.FilterForActivity

interface IFragmentCloseInterface {
    fun onFragClose(list:ArrayList<PurchaseItem>)
    fun onFragClose()
}
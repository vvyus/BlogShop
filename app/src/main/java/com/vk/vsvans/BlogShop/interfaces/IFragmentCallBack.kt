package com.vk.vsvans.BlogShop.interfaces

import com.vk.vsvans.BlogShop.model.data.PurchaseItem

interface IFragmentCallBack {
    fun onFragmentCallBack(pit: PurchaseItem)
}
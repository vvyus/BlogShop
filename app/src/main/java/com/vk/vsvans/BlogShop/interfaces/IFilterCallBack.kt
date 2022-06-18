package com.vk.vsvans.BlogShop.interfaces

import com.vk.vsvans.BlogShop.model.Purchase

interface IFilterCallBack {
    fun onTimeClick()
    fun onSellerClick(purchase: Purchase)
}
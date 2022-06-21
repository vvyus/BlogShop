package com.vk.vsvans.BlogShop.interfaces

import com.vk.vsvans.BlogShop.model.Purchase

interface IFilterCallBack {
    fun onTimeClick(time:Long)
    fun onSellerClick(purchase: Purchase)
}
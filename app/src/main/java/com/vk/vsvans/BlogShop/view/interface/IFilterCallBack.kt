package com.vk.vsvans.BlogShop.view.`interface`

import com.vk.vsvans.BlogShop.model.data.Purchase

interface IFilterCallBack {
    fun onTimeClick(time:Long)
    fun onSellerClick(purchase: Purchase)
}
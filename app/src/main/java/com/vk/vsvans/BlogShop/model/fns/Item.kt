package com.vk.vsvans.BlogShop.model.fns

data class Item(
    val name: String,
    val nds: Int,
    val ndsSum: Int,
    val paymentType: Int,
    val price: Double,
    val productType: Int,
    val quantity: Double,
    val sum: Double
)
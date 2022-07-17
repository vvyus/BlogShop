package com.vk.vsvans.BlogShop.model.fns

object RetrofitCommon {
    private val BASE_URL ="https://vvyus.github.io/"//"https://github.com/vvyus/vvyus.github.io/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}
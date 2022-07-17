package com.vk.vsvans.BlogShop.model.fns

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitServices {
    @GET("checksmodelitem.json")

    fun getChecksModelItem(): Call<MutableList<ChecksModelItem>>?
}
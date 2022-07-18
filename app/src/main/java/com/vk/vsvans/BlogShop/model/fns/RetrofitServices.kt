package com.vk.vsvans.BlogShop.model.fns

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitServices {
    //@GET("checksmodelitem.json")
    @GET("/{name}")
    fun getChecksModelItem(@Path("name") name:String): Call<MutableList<ChecksModelItem>>?
}
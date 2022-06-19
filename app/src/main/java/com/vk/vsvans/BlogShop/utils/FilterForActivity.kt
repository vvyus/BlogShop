package com.vk.vsvans.BlogShop.utils

data class FilterForActivity(val activity:String){
    var idSeller:Int?=null
    var dates_begin :ArrayList<String>?=null
    var dates_end :ArrayList<String>?=null
    var content:String?=null
}

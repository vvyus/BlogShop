package com.vk.vsvans.BlogShop.model.data

import java.io.Serializable

class Purchase: Serializable,Comparable<Purchase> {
    var title = ""
    var content = ""
    var summa:Double= 0.0
    var time:Long = 0
    var id = 0
    var content_html=""
    var idfns=""
    var idseller=0
    var sellername = ""
    override fun compareTo(other: Purchase): Int {
        return time.compareTo(other.time)
    }
}
package com.vk.vsvans.BlogShop.model

class Seller : Comparable<Seller> {
    var id = 0
    var name = ""
    var title = ""
    var description = ""
    var idparent=0
    var fullpath=""
    var level=0
    var expanded=false
    var count=0
    override fun compareTo(other: Seller): Int {
        return fullpath.compareTo(other.fullpath)
    }
}
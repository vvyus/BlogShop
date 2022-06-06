package com.vk.vsvans.BlogShop.model

class Product : Comparable<Product>{
    var id=0
    var name=""
    var title=""
    var bcolor=0
    var idparent=0
    var fullpath=""
    var level=0
    var expanded=false
    var count=0
    override fun compareTo(other: Product): Int {
        return idparent - other.idparent
    }
}
package com.vk.vsvans.BlogShop.model.data

open class BaseList : Comparable<BaseList> {
    var id=0
    var name=""
    var id_fns=""
    var bcolor=0
    var idparent=0
    var fullpath=""
    var level=0
    var expanded=false
    var count=0
    var description=""
    override fun compareTo(other: BaseList): Int {
        return fullpath.compareTo(other.fullpath)
    }
}

package com.vk.vsvans.BlogShop.model

class PurchaseItem {
    var id=0
    var idPurchase=0
    var idProduct=0
    var price:Double=0.0
    var quantity:Double=0.000
    var summa:Double=0.00
    var productName=""

    fun getContent():String {
        val str="Цена ${this.price}\nКол ${this.quantity}\nСумма ${this.summa}"
        return str
    }
}
package com.vk.vsvans.BlogShop.model

import android.app.Application
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.Log
import com.vk.vsvans.BlogShop.utils.*

class PurchaseItem {
    var id=0
    var idPurchase=0
    var idProduct=0
    var price:Double=0.0
    var quantity:Double=0.000
    var summa:Double=0.00
    var productName=""

    fun getContent(title_color:Int): SpannableString {
        val delim="\n"
        val delim_t="\t"
        val indent=" ".repeat(4)//4==приерно==1поз в строке
        val product_title= getSpannableTitle("Товар",title_color)
        val price_title=   getSpannableTitle("Цена",title_color)
        val quan_title=    getSpannableTitle("Кол",title_color)
        val summa_title=   getSpannableTitle("Сумма",title_color)

        val product_value=getSpannableValue("${this.productName}")
        val price_value=getSpannableValue("${this.price}".format("%12.2f"))
        val quan_value=getSpannableValue("${this.quantity}".format("%12.2f"))
        val summa_value=getSpannableValue("${this.summa}".format("%12.2f"))

        val str=product_title+ indent.repeat(2)+ product_value+delim+
                price_title+ indent.repeat(3)+price_value+delim+
                quan_title+ indent.repeat(4)+quan_value+delim+
                summa_title+ indent.repeat(2)+summa_value
        return str
    }

    fun getContentShort(title_color:Int): SpannableString {
        val delim="\n"

        val indent=" ".repeat(4)//4==приерно==1поз в строке
        val delim_t=indent
        val product_title= getSpannableTitle("Товар",title_color)
        val price_title=   getSpannableTitle("Цена",title_color)
        val quan_title=    getSpannableTitle("Кол",title_color)
        val summa_title=   getSpannableTitle("Сумма",title_color)

        val product_value=getSpannableValue("${this.productName}")
        val price_value=getSpannableValue("${this.price}".format("%12.2f"))
        val quan_value=getSpannableValue("${this.quantity}".format("%12.2f"))
        val summa_value=getSpannableValue("${this.summa}".format("%12.2f"))

        val str=product_title+ indent.repeat(2)+ product_value+delim+
                price_title+ indent.repeat(3)+price_value+delim_t+
                quan_title+ indent.repeat(4)+quan_value+delim_t+
                summa_title+ indent.repeat(2)+summa_value
        return str
    }
    private fun getSpannableTitle(str:String,title_color:Int): SpannableString {
        val title=spannable{
            str.makeSpannableString()
                .makeBold()
                .makeUnderline()
                .makeSize(1.2f)
                .makeColor(title_color)
               // .makeBackground(Color.WHITE)
        }
        return title
    }
    private fun getSpannableValue(str:String): SpannableString {
        val title=spannable{
            str.makeSpannableString()
                .makeBold()
                .makeSize(1.2f)
                .makeColor(Color.BLACK)
               // .makeBackground(Color.WHITE)
        }
        return title
    }

}
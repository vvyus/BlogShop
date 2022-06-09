package com.vk.vsvans.BlogShop.model

import android.content.res.Resources
import android.graphics.Color
import android.text.SpannableString
import com.vk.vsvans.BlogShop.utils.*


class PurchaseItem {
    var id=0
    var idPurchase=0
    var idProduct=0
    var price:Double=0.0
    var quantity:Double=0.000
    var summa:Double=0.00
    var productName=""
    val size=1.2f // размер шрифта

    fun getContent(title_color:Int): SpannableString {
        val delim="\n"
       // val delim_t="\t"
        //"Цен".length==3
        val width=getWidth(1)-3*(size*10).toInt()
      //  val product_title= getSpannableTitle("Товар",title_color)
        val price_title=   getSpannableTitle("Цен",title_color)
        val quan_title=    getSpannableTitle("Кол",title_color)
        val summa_title=   getSpannableTitle("Сум",title_color)

        val product_value=getSpannableTitle("${this.productName}"+delim,title_color)//getSpannableValue("${this.productName}")
        val str_price="${this.price}".format("%12.2f").padStart(width,' ')+delim
        val price_value=getSpannableValue(str_price)
        val str_quantity="${this.quantity}".format("%12.3f").padStart(width,' ')+delim
        val quan_value=getSpannableValue(str_quantity)
        val str_summa="${this.summa}".format("%12.2f").padStart(width,' ')
        val summa_value=getSpannableValue(str_summa)

        val str=product_value+
                price_title+ price_value+
                quan_title+ quan_value+
                summa_title+ summa_value
        return str
    }

    fun getContentShort(title_color:Int): SpannableString {
        val delim="\n"
        var width=getWidth(3)//-(size*10).toInt()
       val indent=" ".repeat(4)//4==приерно==1 симв в строке
       // val delim_t=indent
        val product_title= getSpannableTitle("Товар",title_color)
        val price_title=   getSpannableTitle("Цена",title_color)
        val quan_title=    getSpannableTitle("Кол",title_color)
        val summa_title=   getSpannableTitle("Сумма",title_color)

        val product_value=getSpannableTitle("${this.productName}"+delim,title_color)
        val str_price="${this.price}".format("%12.2f")
        val price_value=getSpannableValue(str_price.padStart(width-price_title.length-str_price.length,' ')+indent)
        val str_quantity="${this.quantity}".format("%7.3f")
        val quan_value=getSpannableValue(str_quantity.padStart(width-quan_title.length-str_quantity.length,' ')+indent)
        val str_summa="${this.summa}".format("%12.2f")
        val summa_value=getSpannableValue(str_summa.padStart(width-summa_title.length-str_summa.length,' '))

        val str=product_value+
                price_title+ price_value+
                quan_title+quan_value+
                summa_title+summa_value
        return str
    }
    private fun getSpannableTitle(str:String,title_color:Int): SpannableString {
        val title=spannable{
            str.makeSpannableString()
                .makeBold()
                .makeUnderline()
                .makeSize(size)
                .makeColor(title_color)
               // .makeBackground(Color.WHITE)
        }
        return title
    }
    private fun getSpannableValue(str:String): SpannableString {
        val title=spannable{
            str.makeSpannableString()
                .makeBold()
                .makeSize(size)
                .makeColor(Color.BLACK)
               // .makeBackground(Color.WHITE)
        }
        return title
    }

    private fun getWidth(nColumns:Int) :Int{
        val width=Resources.getSystem().getDisplayMetrics().widthPixels/(size*10) //Resources.getSystem().getDisplayMetrics().density
        val widthDp=width/nColumns
        return widthDp.toInt()
    }
}
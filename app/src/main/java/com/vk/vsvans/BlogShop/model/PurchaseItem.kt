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
        val width=getWidth(2)
        val indent=0//size.toInt()*10*2
      //  val product_title= getSpannableTitle("Товар",title_color)
        val price_title=   getSpannableTitle("Цен",title_color)
        val quan_title=    getSpannableTitle("Кол",title_color)
        val summa_title=   getSpannableTitle("Сум",title_color)

        val product_value=getSpannableTitle("${this.productName}",title_color)//getSpannableValue("${this.productName}")
        val str_price="${this.price}".format("%12.2f").padStart(width,' ')
        val price_value=getSpannableValue(str_price)//price_title.length-str_price.length-indent,' '))
        val str_quantity="${this.quantity}".format("%12.3f").padStart(width,' ')
        val quan_value=getSpannableValue(str_quantity)//quan_title.length-str_quantity.length-indent,' '))
        val str_summa="${this.summa}".format("%12.2f").padStart(width,' ')
        val summa_value=getSpannableValue(str_summa)//summa_title.length-str_summa.length-indent,' '))

        val str=product_value+delim+//product_title+ indent+ product_value+delim+
                price_title+ price_value+delim+
                quan_title+ quan_value+delim+
                summa_title+ summa_value
        return str
    }

    fun getContentShort(title_color:Int): SpannableString {
        val delim="\n"
        var width=getWidth(3)
        val indent=" ".repeat(4)//4==приерно==1поз в строке
        val delim_t=indent
        val product_title= getSpannableTitle("Товар",title_color)
        val price_title=   getSpannableTitle("Цена",title_color)
        val quan_title=    getSpannableTitle("Кол",title_color)
        val summa_title=   getSpannableTitle("Сумма",title_color)

        val product_value=getSpannableTitle("${this.productName}",title_color)//getSpannableValue("${this.productName}")
        val str_price="${this.price}".format("%12.2f")
        val price_value=getSpannableValue(str_price.padStart(width-delim_t.length-price_title.length-str_price.length,' '))
        val str_quantity="${this.quantity}".format("%7.3f")
        val quan_value=getSpannableValue(str_quantity.padStart(width-delim_t.length-quan_title.length-str_quantity.length,' '))
        val str_summa="${this.summa}".format("%12.2f")
        val summa_value=getSpannableValue(str_summa.padStart(width-summa_title.length-str_summa.length,' '))

        val str=product_value+delim+//product_title+ indent.repeat(2)+ product_value+delim+
                price_title+ price_value+delim_t+//indent.repeat(3)+
                quan_title+quan_value+delim_t+//+ indent.repeat(4)
                summa_title+summa_value//+ indent.repeat(2)
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
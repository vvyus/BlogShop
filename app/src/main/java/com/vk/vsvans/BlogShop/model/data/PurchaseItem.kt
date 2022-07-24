package com.vk.vsvans.BlogShop.model.data

import android.content.res.Resources
import android.graphics.Color
import android.text.SpannableString
import com.vk.vsvans.BlogShop.util.*
import kotlin.math.roundToInt


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
        val width=getWidth(1)//-3*(size*10).toInt()
      //  val product_title= getSpannableTitle("Товар",title_color)
        val price_title=   getSpannableTitle("Цен",title_color)
        val quan_title=    getSpannableTitle("Кол",title_color)
        val summa_title=   getSpannableTitle("Сум",title_color)

        val product_value=getSpannableTitle("${this.productName}"+delim,title_color)//getSpannableValue("${this.productName}")
        var str_price=((price*100).roundToInt()/100.0).toString()//"${this.price}".format("%12.2f")
        str_price=str_price.padStart(width-3-str_price.length,' ')
        val price_value=getSpannableValue(str_price)
        var str_quantity=((quantity*1000).roundToInt()/1000.0).toString()//"${this.quantity}".format("%12.3f")
        str_quantity=str_quantity.padStart(width-3-str_quantity.length,' ')
        val quan_value=getSpannableValue(str_quantity)
        var str_summa=((summa*100).roundToInt()/100.0).toString()//"${this.summa}".format("%12.2f")
        str_summa=str_summa.padStart(width-3-str_summa.length,' ')
        val summa_value=getSpannableValue(str_summa)

        val str=product_value+
                price_title+ price_value+delim+
                quan_title+ quan_value+delim+
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
        val str_price=((price*100).roundToInt()/100.0).toString()//"${this.price}".format("%12.2f")
        val price_value=getSpannableValue(str_price.padStart(width-price_title.length-str_price.length,' ')+indent)
        val str_quantity=((quantity*1000).roundToInt()/1000.0).toString()//"${this.quantity}".format("%12.3f")
        val quan_value=getSpannableValue(str_quantity.padStart(width-quan_title.length-str_quantity.length,' ')+indent)
        val str_summa=((summa*100).roundToInt()/100.0).toString()//"${this.summa}".format("%12.2f")
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

//    private fun getSpannableTitleProduct(str:String,title_color:Int): SpannableString {
//        val title=spannable{
//            str.makeSpannableString()
//                .makeBold()
//                .makeUnderline()
//                .makeSize(size)
//                .makeColor(title_color)
//                .makeUrl("<a href='\"'http://mail.ru'\"'>${str}</a>")//+str)
//
//        }
//        return title
//    }

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
        val density=Resources.getSystem().getDisplayMetrics().density
        val width=Resources.getSystem().getDisplayMetrics().widthPixels/density/4.5//(size*10)
        val widthDp=width/nColumns
        return widthDp.toInt()
    }
}
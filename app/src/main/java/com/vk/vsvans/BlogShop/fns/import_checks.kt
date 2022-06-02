package com.vk.vsvans.BlogShop.fns

import android.content.Context
import android.os.Build
import android.os.Environment
import android.text.Html
import androidx.annotation.RequiresApi
import com.vk.vsvans.BlogShop.MainActivity
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.model.Purchase
import com.vk.vsvans.BlogShop.model.PurchaseItem
import com.vk.vsvans.BlogShop.utils.DateTimeUtils
import com.vk.vsvans.BlogShop.utils.makeSpannableString
import com.vk.vsvans.BlogShop.utils.plus
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.IOException

object import_checks {
   @RequiresApi(Build.VERSION_CODES.N)
   suspend fun doImport(context: Context){
       val separator=(context as MainActivity).resources.getString(R.string.SEPARATOR)
       val db=(context as MainActivity).dbManager
       val title_color=(context as MainActivity).getColor(R.color.green_main)
        val path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val files= File(path).listFiles()

        files?.forEach{ itf ->
            println(itf.name)
            try {
                if(itf.name.endsWith(".json")) {
                    val bufferedReader: BufferedReader = File(itf.absolutePath).bufferedReader()
                    val inputString = bufferedReader.readText()
                    //val post= JSONObject(inputString)
                    val post = JSONArray(inputString)
                    for (i in 0 until post.length()) {
//                  // get docs header
                        val jsonObject = JSONObject(post[i].toString())
                        if(jsonObject!=null) {
                            val ticket = jsonObject.getJSONObject("ticket")
                            if (ticket != null) {
                                val document = ticket.getJSONObject("document")
                                if (document != null) {
                                    val receipt = document.getJSONObject("receipt")
                                    if(receipt!=null) {

                                        val user = receipt.getString("user")
                                        val dateTime = receipt.getString("dateTime")

                                        val totalSum = receipt.getLong("totalSum") / 100.0
                                        val fn = receipt.getString("fiscalDriveNumber")//fn
                                        val fd = receipt.getString("fiscalDocumentNumber") //fd
                                        val fp = receipt.getString("fiscalSign")//fp
                                        println("Result is ${user} ${dateTime} ${totalSum} fd=${fd} fn=${fn} fp=${fp}")
                                        val idFns=fn+separator+fd+separator+fp+separator+dateTime
                                        val purchase=Purchase()
                                        var idPurchase=db.readPurchaseFns(idFns)
                                        if(idPurchase==0){

                                            purchase.idfns=idFns
                                            val dateTimeLong=DateTimeUtils.parseDateTimeQrString(dateTime)
                                            if (dateTimeLong != null) {
                                                purchase.time=dateTimeLong
                                            }
                                            purchase.summa=totalSum
                                            purchase.title=user
                                            idPurchase= db.insertPurchase(purchase)!!
                                        }
                                        // print chek items
                                        val items = receipt.getJSONArray("items")
                                        if(items!=null){
                                            var content_temp="".makeSpannableString()
                                            db.removePurchaseItems(idPurchase)
                                            for (j in 0 until items.length()) {
                                                val item = JSONObject(items[j].toString())
                                                if(item!=null){
                                                    var pit=PurchaseItem()
                                                    val name = item.getString("name")
                                                    val quantity = item.getLong("quantity")
                                                    val sum = item.getLong("sum") / 100.0
                                                    val price=item.getLong("price")/100.0
                                                    println("${name}  ${quantity}  ${sum}")
                                                    pit.idPurchase=idPurchase
                                                    pit.price=price
                                                    pit.quantity= quantity.toDouble()
                                                    pit.summa=sum
                                                    pit.productName=name
                                                    db.insertPurchaseItem(pit)
                                                    content_temp+=pit.getContent(title_color)+"\n\n"
                                                }
                                            }
                                            purchase!!.content= content_temp.toString()
                                            purchase!!.content_html= Html.toHtml(content_temp,0)
                                            db.updatePurchase(purchase)
                                        }
                                    }// if receipt
                                } // if ticket
                            }// if document
                        }//if jsonobj
                    }
                } // if json
            }catch (e: IOException){
            }
        }

    }
}
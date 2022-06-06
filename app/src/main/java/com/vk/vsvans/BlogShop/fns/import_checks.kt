package com.vk.vsvans.BlogShop.fns

import android.content.Context
import android.os.Build
import android.os.Environment
import android.text.Html
import androidx.annotation.RequiresApi
import com.vk.vsvans.BlogShop.MainActivity
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.model.Product
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
       var fn=""
       var fd=""
       var fp=""
       var totalSum =0.0
       var user=""
       var dateTime=""
       var idPurchase=0
       var purchase:Purchase?=null
       var pit:PurchaseItem?=null
       var idFns=""
       var dateTimeLong:Long?=0
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

                                        user = receipt.getString("user")
                                        dateTime = receipt.getString("dateTime")
                                        totalSum = receipt.getLong("totalSum") / 100.0
                                        fn = receipt.getString("fiscalDriveNumber")//fn
                                        fd = receipt.getString("fiscalDocumentNumber") //fd
                                        fp = receipt.getString("fiscalSign")//fp
                                        println("Result is ${user} ${dateTime} ${totalSum} fd=${fd} fn=${fn} fp=${fp}")
                                        idFns=fn+separator+fd+separator+fp+separator+dateTime
                                        purchase=Purchase()
                                        idPurchase=db.readPurchaseFns(idFns)
                                        if(idPurchase==0){
                                           idPurchase= db.insertPurchase(purchase!!)!!
                                        }
                                        purchase!!.id=idPurchase
                                        purchase!!.idfns=idFns
                                        dateTimeLong=DateTimeUtils.parseDateTimeQrString(dateTime)
                                        if (dateTimeLong != null) {
                                            purchase!!.time= dateTimeLong as Long
                                        }
                                        purchase!!.summa=totalSum
                                        purchase!!.title=user

                                        // print chek items
                                        var content_temp="".makeSpannableString()
                                        db.removePurchaseItems(idPurchase)
                                        val items = receipt.getJSONArray("items")
                                        if(items!=null){
                                            for (j in 0 until items.length()) {
                                                val item = JSONObject(items[j].toString())
                                                if(item!=null){
                                                    pit=PurchaseItem()
                                                    pit!!.idPurchase=idPurchase
                                                    pit!!.price=item.getLong("price")/100.0
                                                    pit!!.quantity= item.getLong("quantity").toDouble()
                                                    pit!!.summa=item.getLong("sum") / 100.0
                                                    pit!!.productName=item.getString("name")
                                                    content_temp+= pit!!.getContent(title_color)+"\n\n"
                                                    println("${pit!!.productName}  ${pit!!.quantity}  ${pit!!.summa}")
                                                    val product=Product()
                                                    val list=db.readProductsTitle(pit!!.productName)
                                                    var idproduct=0
                                                    if(list.size==0){
                                                        product.name=pit!!.productName
                                                        product.title=pit!!.productName
                                                        idproduct= db.insertProduct(product)!!
                                                        product.idparent=idproduct
                                                        product.fullpath=idproduct.toString()
                                                        db.updateProduct(product)
                                                        pit!!.idProduct=idproduct
                                                    }else{
                                                        idproduct=list[0].id
                                                        pit!!.idProduct=idproduct
                                                        product.idparent=idproduct
                                                        product.fullpath=idproduct.toString()
                                                        db.updateProduct(product)
                                                    }
                                                    db.insertPurchaseItem(pit!!)
                                                }
                                            }
                                        }
                                        purchase!!.content= content_temp.toString()
                                        purchase!!.content_html= Html.toHtml(content_temp,0)
                                        db.updatePurchase(purchase!!)

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
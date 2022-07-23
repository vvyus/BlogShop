package com.vk.vsvans.BlogShop.model.fns

import android.os.Build
import android.os.Environment
import android.text.Html
import androidx.annotation.RequiresApi
import com.vk.vsvans.BlogShop.model.data.Product
import com.vk.vsvans.BlogShop.model.data.Purchase
import com.vk.vsvans.BlogShop.model.data.PurchaseItem
import com.vk.vsvans.BlogShop.model.data.Seller
import com.vk.vsvans.BlogShop.util.ImportUtils
import com.vk.vsvans.BlogShop.util.UtilsHelper
import com.vk.vsvans.BlogShop.util.makeSpannableString
import com.vk.vsvans.BlogShop.util.plus
import com.vk.vsvans.BlogShop.viewmodel.ActivityViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FilenameFilter
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.math.roundToLong

object import_checks {
    //viewModel:ActivityViewModel,separator:String,title_color:Int,
   @RequiresApi(Build.VERSION_CODES.N)
   suspend fun getReceipt(selected_date: HashMap<String, Date?>, viewModel:ActivityViewModel, separator:String, title_color:Int, divider:Int){
       var fn=""
       var fd=0
       var fp=0.0
       var totalSum =0.0
       var user=""
       var dateTime=""
       var dateTimeLong:Long?=0
       var retailPlaceAddress=""
       val path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

       val files= File(path).listFiles(object : FilenameFilter {
           override fun accept(dir: File?, name: String): Boolean {
               return name.lowercase(Locale.getDefault()).endsWith(".json")
           }
       })

        files!!.forEach{ itf ->
            println(itf.name)

            try {
                //if(itf.name.endsWith(".json")) {
                val bufferedReader: BufferedReader = File(itf.absolutePath).bufferedReader()
                val inputString = bufferedReader.readText()
                //val post= JSONObject(inputString)
                if(ImportUtils.isJsonArray(inputString)){
                    val post = JSONArray(inputString)

                    for (i in 0 until post.length()) {
                        if(ImportUtils.isJsonObject(post[i].toString())) {
                            val jsonObject = JSONObject(post[i].toString())
                            if (ImportUtils.isJsonObject(jsonObject,"ticket")) {
                                val ticket = jsonObject.getJSONObject("ticket")
                                if (ImportUtils.isJsonObject(ticket,"document")) {
                                    val document = ticket.getJSONObject("document")
                                    if(ImportUtils.isJsonObject(document,"receipt")) {
                                        val receipt = document.getJSONObject("receipt")

                                        user = receipt.getString("user")

                                        dateTime = receipt.getString("dateTime")
                                        dateTimeLong=ImportUtils.parseDateTimeQrString(dateTime)

                                        totalSum = receipt.getLong("totalSum") / 100.0
                                        fn = receipt.getString("fiscalDriveNumber")//fn
                                        fd = receipt.getString("fiscalDocumentNumber").toInt() //fd
                                        fp = receipt.getString("fiscalSign").toDouble()//fp

                                        val items = receipt.getJSONArray("items")
                                        var itemList=ArrayList<Item>()//listOf<Item>()
                                        for (j in 0 until items.length()) {
                                            val item = JSONObject(items[j].toString())
                                            val price=item.getLong("price")/100.0
                                            val quantity= item.getDouble("quantity") //item.getLong("quantity").toDouble()//
                                            val sum=item.getLong("sum") / 100.0
                                            val name=item.getString("name")
                                            val item_instance=Item(name,0,0,0,price,0,quantity,sum)
                                            //itemList+=item_instance
                                            itemList.add(item_instance)
                                        }
                                        retailPlaceAddress=receipt.getString("retailPlaceAddress")
                                        val receipt_instance=Receipt(0,0,0,0,dateTime,0,0,
                                            fd,fn,fp,"", itemList,"",0,0,"","",0,
                                            0,0,"",retailPlaceAddress,0,0, totalSum,user,"")

                                        val key: String = UtilsHelper.getDate(dateTimeLong!!)
                                        if(selected_date.size>0 && selected_date.get(key)==null) continue

                                        receiptToDb(receipt_instance,viewModel,separator,title_color,divider)
                                     }// if receipt
                                } // if ticket
                            }// if document
                        }//if jsonobj
                    }
                } // if jsonarray
            }catch (e: IOException){
            }

        }//files

    }//func
// if from retrofit.demobase divider=100 else =1
    fun receiptToDb(receipt: Receipt, viewModel:ActivityViewModel, separator:String,title_color:Int,divider:Int){
        var fn=""
        var fd=0
        var fp=0.0
        var totalSum =0.0
        var user=""
        var dateTime=""
        var idPurchase=0
        var purchase: Purchase?=null
        var pit: PurchaseItem?=null
        var idFns=""
        var dateTimeLong:Long?=0

        user = receipt.user
        dateTime = receipt.dateTime
        dateTimeLong=ImportUtils.parseDateTimeQrString(dateTime)
        val key: String = UtilsHelper.getDate(dateTimeLong!!)
        //if(selected_date.size>0 && selected_date.get(key)==null) continue
        totalSum = (receipt.totalSum*1000).roundToInt() / 1000.0/divider
        fn = receipt.fiscalDriveNumber
        fd = receipt.fiscalDocumentNumber
        fp = receipt.fiscalSign
        println("Result is ${user} ${dateTime} ${totalSum} fd=${fd} fn=${fn} fp=${fp.roundToLong()}")
        idFns="${fn}${separator}${fd}${separator}${fp.roundToLong()}${separator}${dateTime}"
        purchase= Purchase()
        idPurchase=viewModel.getPurchaseFns(idFns)
        if(idPurchase==0){
            idPurchase= viewModel.insertPurchase(purchase!!)!!
        }
        purchase!!.id=idPurchase
        purchase!!.idfns=idFns

        if (dateTimeLong != null) {
            purchase!!.time= dateTimeLong!!// as Long
            purchase!!.time_day=UtilsHelper.correct_date_begin(purchase!!.time)
        }
        purchase!!.summa=totalSum

        var sellername=user
        var retailPlaceAddress=receipt.retailPlaceAddress

        purchase!!.address= retailPlaceAddress //user==sellername
//purchase!!.
        //!
        var seller: Seller?=null
        val list=viewModel.getSellersFns(user)
        var idseller=0
        if(list.size==0){
            seller= Seller()
            seller.name=sellername
            seller.description=retailPlaceAddress
            seller.id_fns=sellername+retailPlaceAddress // early was sellername
            idseller= viewModel.insertSeller(seller)!!
            seller.id=idseller
            seller.idparent=idseller
            seller.fullpath=idseller.toString()

        }else{
            seller= list[0] as Seller
            //seller.description=retailPlaceAddress // !!late remove this
            idseller=seller.id
            sellername=seller.name
        }

        viewModel.updateSeller(seller)

        purchase!!.sellername=sellername
        purchase!!.idseller=idseller
        //!
        // print chek items
        var content_temp="".makeSpannableString()
        viewModel.removePurchaseItems(idPurchase)
        val items = receipt.items
        if(items!=null){
            for (j in 0 until items.size) {
                val item = items[j] //JSONObject(items[j].toString())
                if(item!=null){
                    pit= PurchaseItem()
                    pit!!.idPurchase=idPurchase
                    pit!!.price=(item.price*1000).roundToInt()/1000.0/divider
                    pit!!.quantity= (item.quantity*1000).roundToInt()/1000.0
                    pit!!.summa=(item.sum*1000).roundToInt() / 1000.0/divider
                    pit!!.productName=item.name
                    content_temp+= pit!!.getContentShort(title_color)+"\n\n"
                    println("${pit!!.productName}  ${pit!!.quantity}  ${pit!!.summa}")
                    var product: Product?=null
                    val list=viewModel.getProductsFns(pit!!.productName)
                    var idproduct=0
                    if(list.size==0){
                        product= Product()
                        product.name=pit!!.productName
                        product.id_fns=pit!!.productName
                        idproduct= viewModel.insertProduct(product)!!
                        product.id=idproduct
                        product.idparent=idproduct
                        product.fullpath=idproduct.toString()

                    }else{
                        product= list[0] as Product
                        idproduct=product.id
                    }
                    viewModel.updateProduct(product)
                    pit!!.idProduct=idproduct
                    viewModel.insertPurchaseItem(pit!!)
                }
            }
        }
        purchase!!.content= content_temp.toString()
        purchase!!.content_html= Html.toHtml(content_temp,0)
        viewModel.updatePurchase(purchase!!)

    } // receipt to db
}
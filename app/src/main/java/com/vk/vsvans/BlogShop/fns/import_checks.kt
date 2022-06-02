package com.vk.vsvans.BlogShop.fns

import android.os.Environment
import com.vk.vsvans.BlogShop.utils.DateTimeFormatter
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.IOException

object import_checks {
   suspend fun doImport(){
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
                                        val date=DateTimeFormatter.parseDateTimeQrString(dateTime)
                                        val totalSum = receipt.getLong("totalSum") / 100.0
                                        val fiscalDocumentNumber =
                                            receipt.getString("fiscalDocumentNumber") //fd
                                        val fiscalDriveNumber =
                                            receipt.getString("fiscalDriveNumber")//fn
                                        val fiscalSign = receipt.getString("fiscalSign")//fp
                                        println("Result is ${user} ${dateTime} ${totalSum} fd=${fiscalDocumentNumber} fn=${fiscalDriveNumber} fp=${fiscalSign}")
                                        // print chek items
                                        val items = receipt.getJSONArray("items")
                                        for (j in 0 until items.length()) {
                                            val item = JSONObject(items[j].toString())
                                            val name = item.getString("name")
                                            val quantity = item.getLong("quantity")
                                            val sum = item.getLong("sum") / 100.0
                                            println("${name}  ${quantity}  ${sum}")
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
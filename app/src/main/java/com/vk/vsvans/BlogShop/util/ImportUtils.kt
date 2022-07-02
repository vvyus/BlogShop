package com.vk.vsvans.BlogShop.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object ImportUtils {
     // 20171219T155200

//    fun parseDateTimeQrString(s: String?): Date? {
//
//        var date: Date?
//        try {
//            if (s != null) {
//                //2022-05-29T08:33:00
//                date = mDateTimeQrFormat!!.parse(s.replace("T"," "))
//                //date.getTime()
//            } else date=Date()
//        } catch (e: ParseException) {
//            date = Date()
//        }
//        return date
//    }

    fun parseDateTimeQrString(s: String?): Long? {
        val mDateTimeQrFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var date: Long?=null
        try {
            if (s != null) {
                //2022-05-29T08:33:00
                date = mDateTimeQrFormat!!.parse(s.replace("T"," ")).time
                //date.getTime()
            }
        } catch (e: ParseException) {
            date = null
        }
        return date
    }

    fun parseDateTimeString(s: String?): Long? {
        val mDateTimeQrFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
        var date: Long?=null
        try {
            if (s != null) {
                //2022-05-29T08:33:00
                date = mDateTimeQrFormat!!.parse(s.replace("T"," ")).time
                //date.getTime()
            }
        } catch (e: ParseException) {
            date = null
        }
        return date
    }

    fun isJsonArray(json: String?): Boolean {
        try {
            JSONArray(json)
        } catch (ne: JSONException) {
            return false
        }
        return true
    }

    fun isJsonObject(json: String?): Boolean {
        try {
            JSONObject(json)
        } catch (e: JSONException) {

            return false
        }
        return true
    }

    fun isJsonObject(jsonObject:JSONObject,json: String?): Boolean {
        try {
            jsonObject.getJSONObject(json)
        } catch (e: JSONException) {

            return false
        }
        return true
    }

}
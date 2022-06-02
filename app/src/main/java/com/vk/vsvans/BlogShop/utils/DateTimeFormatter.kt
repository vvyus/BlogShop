package com.vk.vsvans.BlogShop.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeFormatter {
    private var mDateTimeQrFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // 20171219T155200

    fun parseDateTimeQrString(s: String?): Date? {

        var date: Date?
        try {
            if (s != null) {
                //2022-05-29T08:33:00
                date = mDateTimeQrFormat!!.parse(s.replace("T"," "))
                //date.getTime()
            } else date=Date()
        } catch (e: ParseException) {
            date = Date()
        }
        return date
    }
}
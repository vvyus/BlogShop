package com.vk.vsvans.BlogShop.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateTimeFormatter {
    private var mDateTimeQrFormat: DateFormat? = null // 20171219T155200

    fun parseDateTimeQrString(s: String?): Date? {
        mDateTimeQrFormat = SimpleDateFormat("yyyyMMddHHmmss");
        var date: Date?
        try {
            date = mDateTimeQrFormat!!.parse(s)
        } catch (e: ParseException) {
            date = Date()
        }
        return date
    }
}
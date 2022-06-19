package com.vk.vsvans.BlogShop.interfaces

import java.util.*

interface IDialogDateFiterCallback {
    fun confirmFilter(selected_date : HashMap<String, Date?>)
    fun cancelFilter()
}
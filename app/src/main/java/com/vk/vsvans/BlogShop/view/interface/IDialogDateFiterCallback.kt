package com.vk.vsvans.BlogShop.view.`interface`

import java.util.*

interface IDialogDateFiterCallback {
    fun confirmFilter(selected_date : HashMap<String, Date?>)
    fun cancelFilter()
}
package com.vk.vsvans.BlogShop.interfaces

import android.graphics.Bitmap
import com.vk.vsvans.BlogShop.model.PurchaseItem

interface FragmentCloseInterface {
    fun onFragClose(list:ArrayList<PurchaseItem>)
}
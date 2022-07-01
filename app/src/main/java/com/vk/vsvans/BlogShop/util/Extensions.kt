package com.vk.vsvans.BlogShop.util

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

fun AppCompatActivity.isPermissinGrant(p:String):Boolean {
    return ContextCompat.checkSelfPermission(
        this as AppCompatActivity,
        p
        )== PackageManager.PERMISSION_GRANTED
}
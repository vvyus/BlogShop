package com.vk.vsvans.BlogShop

import android.app.Application
import com.vk.vsvans.BlogShop.model.repository.DbRepositoryImpl

class AppStart : Application() {
    private var database: DbRepositoryImpl? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        database = DbRepositoryImpl(this)//Room.databaseBuilder(this, AppDatabase::class.java, "database").build()
        //database!!.openDb()
    }

    fun getDatabase(): DbRepositoryImpl? {
        return database
    }

    companion object {
        var instance: AppStart? = null
    }
}
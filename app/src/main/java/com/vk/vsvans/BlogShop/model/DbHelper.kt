package com.vk.vsvans.BlogShop.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDbHelper(context: Context) : SQLiteOpenHelper(context, DbName.DATABASE_NAME,
    null, DbName.DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DbName.CREAT_TABLE_SELLERS)
        db?.execSQL(DbName.CREAT_TABLE_PURCHASE_ITEMS)
        db?.execSQL(DbName.CREAT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DbName.SQL_DELETE_TABLE_SELLERS)
        db?.execSQL(DbName.SQL_DELETE_TABLE_PURCHASE_ITEMS)
        db?.execSQL(DbName.SQL_DELETE_TABLE)
        onCreate(db)
    }
}
package com.vk.vsvans.BlogShop.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.vk.vsvans.BlogShop.helper.UtilsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DbManager(context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null
    val context=context

    fun openDb(){
        db = myDbHelper.writableDatabase
    }
    //    SELLERS
    suspend fun insertSellerToDb( title: String, description: String) = withContext(Dispatchers.IO){
        val values = ContentValues().apply {

            put(DbName.COLUMN_NAME_TITLE_SELLERS, title)
            put(DbName.COLUMN_NAME_DESCRIPTION_SELLERS, description)

        }
        db?.insert(DbName.TABLE_NAME_SELLERS,null, values)
    }

    @SuppressLint("Range")
    suspend fun readSellersFromDb(searchText:String): ArrayList<Seller> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<Seller>()
        val selection = "${DbName.COLUMN_NAME_TITLE_SELLERS} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME_SELLERS, null, selection, arrayOf("%$searchText%"),
            null, null, null
        )

        while (cursor?.moveToNext()!!) {
            val dataId =
                cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_TITLE_SELLERS))
            val dataDescription =
                cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_DESCRIPTION_SELLERS))
            val item = Seller()
            item.title = dataTitle
            item.description = dataDescription
            item.id = dataId
            dataList.add(item)
        }

        //if(readDataCallback!=null)readDataCallback.readData(dataList)

        cursor.close()
        return@withContext dataList
    }

    //    PURCHASES
    suspend fun insertPurchaseToDb(title: String, content: String) = withContext(Dispatchers.IO){
        val values = ContentValues().apply {

            put(DbName.COLUMN_NAME_TITLE, title)
            put(DbName.COLUMN_NAME_CONTENT, content)
            val time= UtilsHelper.getCurrentDate()
            put(DbName.COLUMN_NAME_TIME, time)

        }
        db?.insert(DbName.TABLE_NAME,null, values)
    }

    suspend fun updatePurchaseItem(id:Int,title: String, content: String) = withContext(Dispatchers.IO){
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {

            put(DbName.COLUMN_NAME_TITLE, title)
            put(DbName.COLUMN_NAME_CONTENT, content)
            //put(DbName.COLUMN_NAME_TIME, time)
        }
        db?.update(DbName.TABLE_NAME, values, selection, null)
    }

    fun removePurchaseItemFromDb( id: Int){
        val selection = BaseColumns._ID + "=$id"
        db?.delete(DbName.TABLE_NAME,selection, null)
    }

    @SuppressLint("Range")
    suspend fun readPurchasesFromDb(searchText:String): ArrayList<Purchase> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<Purchase>()
        val selection = "${DbName.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME, null, selection, arrayOf("%$searchText%"),
            null, null, null
        )

        while (cursor?.moveToNext()!!) {
            val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_TITLE))
            val dataContent =
                cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_CONTENT))
            val dataId =
                cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val time =
                cursor.getLong(cursor.getColumnIndex(DbName.COLUMN_NAME_TIME))
            val item = Purchase()
            item.title = dataTitle
            item.description = dataContent
            item.id = dataId
            item.time = time
            dataList.add(item)
        }
        cursor.close()
        return@withContext dataList
    }

        @SuppressLint("Range")
        suspend fun readPurchasesItemFromDb(id:Int): ArrayList<Purchase> = withContext(Dispatchers.IO) {
     //  fun readPurchasesItemFromDb(id:Int): ArrayList<Purchase>  {
            val dataList = ArrayList<Purchase>()

            val selection = BaseColumns._ID + " =?"
            //arrayOf(id.toString())
            //arrayOf(BaseColumns._ID,DbName.COLUMN_NAME_TITLE,DbName.COLUMN_NAME_CONTENT,DbName.COLUMN_NAME_TIME
            val cursor = db?.query(
                DbName.TABLE_NAME,
                null,
                selection, arrayOf(id.toString()),
                null, null, null
            )

            while (cursor?.moveToNext()!!) {
                val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_TITLE))
                val dataContent =
                    cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_CONTENT))
                val dataId =
                    cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
                val time =
                    cursor.getLong(cursor.getColumnIndex(DbName.COLUMN_NAME_TIME))
                val item = Purchase()
                item.title = dataTitle
                item.description = dataContent
                item.id = dataId
                item.time = time
                dataList.add(item)
            }
            //if(readDataCallback!=null)readDataCallback.readData(dataList)

            cursor.close()
            return@withContext dataList
            //return dataList
        }

    fun closeDb(){
        myDbHelper.close()
    }

    interface ReadDataCallback {
        fun readData(list: ArrayList<Purchase>)
    }
}
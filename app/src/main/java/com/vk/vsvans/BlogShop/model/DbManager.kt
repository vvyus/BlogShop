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
    // PRODUCT
    // suspend fun insertProduct( product:Product) = withContext(Dispatchers.IO){
    fun insertProduct( product:Product){
        val values = ContentValues().apply {

            put(DbName.COLUMN_NAME_TITLE_PRODUCTS, product.title)
        }
        db?.insert(DbName.TABLE_NAME_PRODUCTS,null, values)
    }

    @SuppressLint("Range")
    suspend fun readProducts(searchText:String): ArrayList<Product> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<Product>()
        val selection = "${DbName.COLUMN_NAME_TITLE_PRODUCTS} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME_PRODUCTS, null, selection, arrayOf("%$searchText%"),
            null, null, null
        )

        while (cursor?.moveToNext()!!) {
            val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_TITLE_PRODUCTS))
            val product = Product()
            product.id = dataId
            product.title = dataTitle
            dataList.add(product)
        }

        //if(readDataCallback!=null)readDataCallback.readData(dataList)

        cursor.close()
        return@withContext dataList
    }

    //suspend fun updateProduct(product:Product) = withContext(Dispatchers.IO){
    fun updateProduct(product:Product){
    val id=product.id
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {

            put(DbName.COLUMN_NAME_TITLE_PRODUCTS, product.title)
        }
        db?.update(DbName.TABLE_NAME_PRODUCTS, values, selection, null)
    }

    fun removeProduct(id: Int){
        val selection = BaseColumns._ID + "=$id"
        db?.delete(DbName.TABLE_NAME_PRODUCTS,selection, null)
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
    suspend fun insertPurchase(purchase:Purchase) :Int?= withContext(Dispatchers.IO){
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_TITLE, purchase.title)
            put(DbName.COLUMN_NAME_CONTENT, purchase.content)
            put(DbName.COLUMN_NAME_SUMMA_PURCHASES, purchase.summa)
            val time= UtilsHelper.getCurrentDate()
            put(DbName.COLUMN_NAME_TIME, time)

        }
        val id=db?.insert(DbName.TABLE_NAME,null, values)
        return@withContext id?.toInt()
    }

    suspend fun updatePurchase(purchase:Purchase) = withContext(Dispatchers.IO){
        val id=purchase.id
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {

            put(DbName.COLUMN_NAME_TITLE, purchase.title)
            put(DbName.COLUMN_NAME_CONTENT, purchase.content)
            put(DbName.COLUMN_NAME_SUMMA_PURCHASES, purchase.summa)
            //put(DbName.COLUMN_NAME_TIME, time)
        }
        db?.update(DbName.TABLE_NAME, values, selection, null)
    }

    fun removePurchase(id: Int){
        val selection = BaseColumns._ID + "=$id"
        db?.delete(DbName.TABLE_NAME,selection, null)
    }

    @SuppressLint("Range")
    suspend fun readPurchases(searchText:String): ArrayList<Purchase> = withContext(Dispatchers.IO) {
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
            val dataSumma =
                cursor.getDouble(cursor.getColumnIndex(DbName.COLUMN_NAME_SUMMA_PURCHASES))
            val purchase = Purchase()
            purchase.title = dataTitle
            purchase.content = dataContent
            purchase.id = dataId
            purchase.time = time
            purchase.summa=dataSumma
            dataList.add(purchase)
        }
        cursor.close()
        return@withContext dataList
    }

        @SuppressLint("Range")
        suspend fun readOnePurchase(id:Int): Purchase = withContext(Dispatchers.IO) {
     //  fun readPurchasesItemFromDb(id:Int): ArrayList<Purchase>  {

            val selection = BaseColumns._ID + " =?"
            //arrayOf(id.toString())
            //arrayOf(BaseColumns._ID,DbName.COLUMN_NAME_TITLE,DbName.COLUMN_NAME_CONTENT,DbName.COLUMN_NAME_TIME
            val cursor = db?.query(
                DbName.TABLE_NAME,
                null,
                selection, arrayOf(id.toString()),
                null, null, null
            )
            val purchase = Purchase()
            while (cursor?.moveToNext()!!) {

                val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_TITLE))
                val dataContent =
                    cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_CONTENT))
                val dataId =
                    cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
                val time =
                    cursor.getLong(cursor.getColumnIndex(DbName.COLUMN_NAME_TIME))
                val dataSumma =
                    cursor.getDouble(cursor.getColumnIndex(DbName.COLUMN_NAME_SUMMA_PURCHASES))

                purchase.title = dataTitle
                purchase.content = dataContent
                purchase.id = dataId
                purchase.time = time
                purchase.summa=dataSumma

                break

            }
            //if(readDataCallback!=null)readDataCallback.readData(dataList)

            cursor.close()
            return@withContext purchase
            //return dataList
        }

    // PurchaseItem

    @SuppressLint("Range")
    suspend fun readPurchaseItems(id:Int): ArrayList<PurchaseItem> = withContext(Dispatchers.IO) {
        //  fun readPurchasesItemFromDb(id:Int): ArrayList<Purchase>  {
        val dataList = ArrayList<PurchaseItem>()

        val selection = DbName.COLUMN_NAME_PURCHASE_ID + " =?"
        //arrayOf(id.toString())
        //arrayOf(BaseColumns._ID,DbName.COLUMN_NAME_TITLE,DbName.COLUMN_NAME_CONTENT,DbName.COLUMN_NAME_TIME
        val cursor = db?.query(
            DbName.TABLE_NAME_PURCHASE_ITEMS,
            null,
            selection, arrayOf(id.toString()),
            null, null, null
        )

        while (cursor?.moveToNext()!!) {
            val dataPrice = cursor.getDouble(cursor.getColumnIndex(DbName.COLUMN_NAME_PRICE))
            val dataQuantity =
                cursor.getDouble(cursor.getColumnIndex(DbName.COLUMN_NAME_QUANTITY))
            val dataSumma =
                cursor.getDouble(cursor.getColumnIndex(DbName.COLUMN_NAME_SUMMA))
            val dataId =
                cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val item = PurchaseItem()
            item.price = dataPrice
            item.quantity = dataQuantity
            item.summa = dataSumma
            item.id = dataId
            item.idPurchase = id
            dataList.add(item)
        }
        //if(readDataCallback!=null)readDataCallback.readData(dataList)

        cursor.close()
        return@withContext dataList
        //return dataList
    }

    @SuppressLint("Range")
    suspend fun updatePurchaseItem(purchaseItem:PurchaseItem) = withContext(Dispatchers.IO){
        val id:Int=purchaseItem.id
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {

            put(DbName.COLUMN_NAME_PRICE, purchaseItem.price)
            put(DbName.COLUMN_NAME_QUANTITY, purchaseItem.quantity)
            put(DbName.COLUMN_NAME_SUMMA, purchaseItem.summa)
            put(DbName.COLUMN_NAME_PURCHASE_ID, purchaseItem.idPurchase)
            //put(DbName.COLUMN_NAME_TIME, time)
        }
        db?.update(DbName.TABLE_NAME_PURCHASE_ITEMS, values, selection, null)
    }
    @SuppressLint("Range")
    suspend fun insertPurchaseItem(purchaseItem:PurchaseItem) = withContext(Dispatchers.IO){
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_PRICE, purchaseItem.price)
            put(DbName.COLUMN_NAME_QUANTITY, purchaseItem.quantity)
            put(DbName.COLUMN_NAME_SUMMA, purchaseItem.summa)
            put(DbName.COLUMN_NAME_PURCHASE_ID, purchaseItem.idPurchase)

        }
        db?.insert(DbName.TABLE_NAME_PURCHASE_ITEMS,null, values)
    }

    fun removePurchaseItem(pit: PurchaseItem){
        val selection = BaseColumns._ID + "=${pit.id}"
        db?.delete(DbName.TABLE_NAME_PURCHASE_ITEMS,selection, null)
    }

    fun closeDb(){
        myDbHelper.close()
    }

    interface ReadDataCallback {
        fun readData(list: ArrayList<Purchase>)
    }
}
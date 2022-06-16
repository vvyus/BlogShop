package com.vk.vsvans.BlogShop.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.provider.BaseColumns
import androidx.annotation.RequiresApi
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
    fun insertProduct( product:Product):Int?{
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_NAME_PRODUCTS, product.name)
            put(DbName.COLUMN_NAME_ID_FNS_PRODUCTS, product.id_fns)
            put(DbName.COLUMN_NAME_IDPARENT_PRODUCTS, product.idparent)
            put(DbName.COLUMN_NAME_LEVEL_PRODUCTS, product.level)
            put(DbName.COLUMN_NAME_FULLPATH_PRODUCTS, product.fullpath)
            put(DbName.COLUMN_NAME_COUNT_PRODUCTS, product.count)
        }
        val id=db?.insert(DbName.TABLE_NAME_PRODUCTS,null, values)
        return id?.toInt()
    }

    @SuppressLint("Range")
    suspend fun readProducts(searchText:String): ArrayList<Product> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<Product>()
        val selection = "${DbName.COLUMN_NAME_NAME_PRODUCTS} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME_PRODUCTS, arrayOf("_id","idparent","name","title","level","count","fullpath"), selection, arrayOf("%$searchText%"),
            null, null, "fullpath ASC"
        )

        while (cursor?.moveToNext()!!) {
            val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_ID_FNS_PRODUCTS))
            val dataName = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_NAME_PRODUCTS))
            val idparent = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_IDPARENT_PRODUCTS))
            val level = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_LEVEL_PRODUCTS))
            val fullpath = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_FULLPATH_PRODUCTS))
            val count = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_COUNT_PRODUCTS))
            val product = Product()
            product.id = dataId
            product.name = dataName
            product.id_fns=dataTitle
            product.level = level?:0
            product.idparent = idparent?:0
            product.fullpath = fullpath?:""
            product.count = count?:0
            dataList.add(product)
        }
        cursor.close()
        return@withContext dataList
    }

    @SuppressLint("Range")
    // Используется при загрузке чеков поиск по вспом полю title для ручного ввода это пол пусто
    suspend fun readProductsTitle(searchText:String): ArrayList<Product> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<Product>()
        val selection = "${DbName.COLUMN_NAME_ID_FNS_PRODUCTS} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME_PRODUCTS, null, selection, arrayOf("%$searchText%"),
            null, null, null
        )

        while (cursor?.moveToNext()!!) {
            val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_ID_FNS_PRODUCTS))
            val dataName = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_NAME_PRODUCTS))
            val idparent = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_IDPARENT_PRODUCTS))
            val level = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_LEVEL_PRODUCTS))
            val fullpath = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_FULLPATH_PRODUCTS))
            val count = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_COUNT_PRODUCTS))
            val product = Product()
            product.id = dataId
            product.name = dataName
            product.id_fns=dataTitle
            product.level = level?:0
            product.idparent = idparent?:0
            product.fullpath = fullpath?:""
            product.count = count?:0
            dataList.add(product)
        }
        cursor.close()
        return@withContext dataList
    }

    //suspend fun updateProduct(product:Product) = withContext(Dispatchers.IO){
    fun updateProduct(product:Product){
    val id=product.id
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_NAME_PRODUCTS, product.name)
            put(DbName.COLUMN_NAME_ID_FNS_PRODUCTS, product.id_fns)
            put(DbName.COLUMN_NAME_IDPARENT_PRODUCTS, product.idparent)
            put(DbName.COLUMN_NAME_LEVEL_PRODUCTS, product.level)
            put(DbName.COLUMN_NAME_FULLPATH_PRODUCTS, product.fullpath)
            put(DbName.COLUMN_NAME_COUNT_PRODUCTS, product.count)
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

            put(DbName.COLUMN_NAME_ID_FNS_SELLERS, title)
            put(DbName.COLUMN_NAME_DESCRIPTION_SELLERS, description)

        }
        db?.insert(DbName.TABLE_NAME_SELLERS,null, values)
    }

    suspend fun updateSeller(seller: Seller) = withContext(Dispatchers.IO){
        val id=seller.id
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_NAME_SELLERS, seller.name)
            put(DbName.COLUMN_NAME_ID_FNS_SELLERS, seller.id_fns)
            put(DbName.COLUMN_NAME_IDPARENT_SELLERS, seller.idparent)
            put(DbName.COLUMN_NAME_LEVEL_SELLERS, seller.level)
            put(DbName.COLUMN_NAME_FULLPATH_SELLERS, seller.fullpath)
            put(DbName.COLUMN_NAME_COUNT_SELLERS, seller.count)
        }
        db?.update(DbName.TABLE_NAME_SELLERS, values, selection, null)
    }

//    suspend fun insertSeller( seller: Seller) :Int?= withContext(Dispatchers.IO){
fun insertSeller( seller: Seller) :Int?{
    val values = ContentValues().apply {
        put(DbName.COLUMN_NAME_NAME_SELLERS, seller.name)
        put(DbName.COLUMN_NAME_ID_FNS_SELLERS, seller.id_fns)
        put(DbName.COLUMN_NAME_DESCRIPTION_SELLERS, seller.description)
        put(DbName.COLUMN_NAME_IDPARENT_SELLERS, seller.idparent)
        put(DbName.COLUMN_NAME_LEVEL_SELLERS, seller.level)
        put(DbName.COLUMN_NAME_FULLPATH_SELLERS, seller.fullpath)
        put(DbName.COLUMN_NAME_COUNT_SELLERS, seller.count)
    }
    val id=db?.insert(DbName.TABLE_NAME_SELLERS,null, values)
    return id?.toInt()
}

    @SuppressLint("Range")
    // Используется при загрузке чеков поиск по вспом полю title для ручного ввода это пол пусто
    suspend fun readSellersTitle(searchText:String): ArrayList<Seller> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<Seller>()
        val selection = "${DbName.COLUMN_NAME_ID_FNS_SELLERS} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME_SELLERS, null, selection, arrayOf("%$searchText%"),
            null, null, null
        )

        while (cursor?.moveToNext()!!) {
            val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_ID_FNS_SELLERS))
            val dataName = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_NAME_SELLERS))
            val idparent = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_IDPARENT_SELLERS))
            val level = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_LEVEL_SELLERS))
            val fullpath = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_FULLPATH_SELLERS))
            val count = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_COUNT_SELLERS))
            val seller = Seller()
            seller.id = dataId
            seller.name = dataName
            seller.id_fns=dataTitle
            seller.level = level?:0
            seller.idparent = idparent?:0
            seller.fullpath = fullpath?:""
            seller.count = count?:0
            dataList.add(seller)
        }
        cursor.close()
        return@withContext dataList
    }

    @SuppressLint("Range")
    suspend fun readSellersFromDb(searchText:String): ArrayList<Seller> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<Seller>()
        val selection = "${DbName.COLUMN_NAME_ID_FNS_SELLERS} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME_SELLERS, null, selection, arrayOf("%$searchText%"),
            null, null, null
        )

        while (cursor?.moveToNext()!!) {
            val dataId =
                cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_ID_FNS_SELLERS))
            val dataDescription =
                cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_DESCRIPTION_SELLERS))
            val item = Seller()
            item.id_fns = dataTitle
            item.description = dataDescription
            item.id = dataId
            dataList.add(item)
        }

        //if(readDataCallback!=null)readDataCallback.readData(dataList)

        cursor.close()
        return@withContext dataList
    }

    //    PURCHASES
    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun insertPurchase(purchase:Purchase) :Int?= withContext(Dispatchers.IO){
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_TITLE, purchase.title)
            put(DbName.COLUMN_NAME_CONTENT, purchase.content)
            put(DbName.COLUMN_NAME_CONTENT_HTML, purchase.content_html)
            put(DbName.COLUMN_NAME_SUMMA_PURCHASES, purchase.summa)
            put(DbName.COLUMN_NAME_ID_FNS, purchase.idfns)
            //val time= UtilsHelper.getCurrentDate()
            put(DbName.COLUMN_NAME_TIME, purchase.time)

        }
        val id=db?.insert(DbName.TABLE_NAME,null, values)
        return@withContext id?.toInt()
    }

    @RequiresApi(Build.VERSION_CODES.N)
 //   suspend fun updatePurchase(purchase:Purchase) = withContext(Dispatchers.IO){
    fun updatePurchase(purchase:Purchase) {
        val id=purchase.id
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {

            put(DbName.COLUMN_NAME_TITLE, purchase.title)
            put(DbName.COLUMN_NAME_CONTENT, purchase.content)
            put(DbName.COLUMN_NAME_SUMMA_PURCHASES, purchase.summa)
            put(DbName.COLUMN_NAME_CONTENT_HTML,purchase.content_html)
            put(DbName.COLUMN_NAME_ID_FNS, purchase.idfns)
            put(DbName.COLUMN_NAME_TIME, purchase.time)
            put(DbName.COLUMN_NAME_SELLER_ID, purchase.idseller)
        }
        db?.update(DbName.TABLE_NAME, values, selection, null)
    }

    fun removePurchase(id: Int){
        val selection = BaseColumns._ID + "=$id"
        db?.delete(DbName.TABLE_NAME,selection, null)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("Range")
//    suspend fun readPurchases(searchText:String): ArrayList<Purchase> = withContext(Dispatchers.IO) {
//        val purchaseList = ArrayList<Purchase>()
//        val selection = "${DbName.COLUMN_NAME_TITLE} like ?"
//        val cursor = db?.query(
//            DbName.TABLE_NAME, null, selection, arrayOf("%$searchText%"),
//            null, null, "${DbName.COLUMN_NAME_TIME} DESC"
//        )
//        if(cursor!=null){
//            setPurchaseListFromCursor(cursor!!,purchaseList)
//            cursor!!.close()
//        }
//        return@withContext purchaseList
//    }

    suspend fun queryPurchases(searchText:String,purchaseList: ArrayList<Purchase>):Double = withContext(Dispatchers.IO) {
//        val purchaseList = ArrayList<Purchase>()
//        val cursor = db?.query(
//            DbName.TABLE_NAME, null, selection, arrayOf("%$searchText%"),
//            null, null, "${DbName.COLUMN_NAME_TIME} DESC"
//        )
        val selection = "${DbName.COLUMN_NAME_CONTENT} like ?"
        val selectionArgs = arrayOf("%"+searchText + "%")

        val temp: String = DbName.PURCHASE_QUERY
        val selectQuery: String = temp.replace(
            DbName.WHERE_FOR_PURCHASE_QUERY,
            "WHERE $selection "
        )
        val cursor = db?.rawQuery(selectQuery, selectionArgs)
        var amount=0.0
        if(cursor!=null){
            amount=setPurchaseListFromCursor(cursor!!,purchaseList)
            cursor!!.close()
        }
        //return@withContext purchaseList
        return@withContext amount
    }

    fun queryPurchases(dates_begin: ArrayList<String>, dates_end: ArrayList<String>,purchaseList: ArrayList<Purchase>):Double {
        //val db: SQLiteDatabase = MyDbHelper.getWritableDatabase()
        //var purchaseList  = ArrayList<Purchase>()
        var selection = ""
        for (i in dates_begin.indices) {
            selection += "${DbName.COLUMN_NAME_TIME} >= " + dates_begin[i] + " AND ${DbName.COLUMN_NAME_TIME}<=" + dates_end[i]
            if (i < dates_begin.size - 1) selection += " OR "
        }
        val temp: String = DbName.PURCHASE_QUERY
        val selectQuery: String = temp.replace(
            DbName.WHERE_FOR_PURCHASE_QUERY,
            "WHERE $selection "
        )
        val cursor = db?.rawQuery(selectQuery, null)
        var amount=0.0
        if(cursor!=null){
            amount=setPurchaseListFromCursor(cursor!!,purchaseList)
            cursor!!.close()
        }
        // return note list
        return amount//purchaseList
    }

    @SuppressLint("Range")
    private fun setPurchaseListFromCursor(cursor: Cursor, purchaseList: ArrayList<Purchase>):Double{
        var amount=0.0
        while (cursor?.moveToNext()!!) {
            val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_TITLE))
            val dataContent = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_CONTENT))
            val dataContentHtml = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_CONTENT_HTML))

            val dataIdFns = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_ID_FNS))

            val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val time = cursor.getLong(cursor.getColumnIndex(DbName.COLUMN_NAME_TIME))
            val dataSumma = cursor.getDouble(cursor.getColumnIndex(DbName.COLUMN_NAME_SUMMA_PURCHASES))
            val sellername=cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_SELLER_NAME))
            val sellerid=cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_SELLER_ID))
            val purchase = Purchase()
            purchase.title = dataTitle
            purchase.content = dataContent

            purchase.content_html= dataContentHtml
            purchase.id = dataId
            purchase.time = time
            purchase.summa=dataSumma
            purchase.idfns=dataIdFns
            purchase.idseller=sellerid
            purchase.sellername=sellername
            purchaseList.add(purchase)
            amount+=dataSumma
        }
        return amount
    }
        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("Range")
        suspend fun readOnePurchase(id:Int): Purchase? = withContext(Dispatchers.IO) {

            val selection ="${DbName.TABLE_NAME}"+"."+BaseColumns._ID + "=?"
            val selectionArgs = arrayOf(id.toString())
            val temp: String = DbName.PURCHASE_QUERY
            val selectQuery: String = temp.replace(
                DbName.WHERE_FOR_PURCHASE_QUERY,
                "WHERE $selection "
            )
            val cursor = db?.rawQuery(selectQuery, selectionArgs)
            var amount=0.0
            var purchase :Purchase?= null
            val purchaseList= ArrayList<Purchase>()
            if(cursor!=null){
                amount=setPurchaseListFromCursor(cursor!!,purchaseList)
                purchase=if(purchaseList.size>0) purchaseList[0] else null
                cursor!!.close()
            }
//            val cursor = db?.query(
//                DbName.TABLE_NAME,
//                null,
//                selection, arrayOf(id.toString()),
//                null, null, null
//            )
//            val purchase = Purchase()
//            while (cursor?.moveToNext()!!) {
//
//                val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_TITLE))
//                val dataContent =
//                    cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_CONTENT))
//                val dataContentHtml =
//                    cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_CONTENT_HTML))
//                val dataIdFns =
//                    cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_ID_FNS))
//                val dataId =
//                    cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
//                val time =
//                    cursor.getLong(cursor.getColumnIndex(DbName.COLUMN_NAME_TIME))
//                val dataSumma =
//                    cursor.getDouble(cursor.getColumnIndex(DbName.COLUMN_NAME_SUMMA_PURCHASES))
//                val sellername=cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_SELLER_NAME))
//                val sellerid=cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_SELLER_ID))
//                purchase.title = dataTitle
//                purchase.content = dataContent
//                purchase.content_html = dataContentHtml
//                purchase.id = dataId
//                purchase.time = time
//                purchase.summa=dataSumma
//                purchase.idfns=dataIdFns
//                purchase.sellername=sellername
//                purchase.idseller=sellerid
//                break
//
//            }
//            //if(readDataCallback!=null)readDataCallback.readData(dataList)
//
//            cursor.close()
            return@withContext purchase
            //return dataList
        }

    @SuppressLint("Range")
    suspend fun readPurchaseFns(idFns:String): Int = withContext(Dispatchers.IO) {
        val selection = "${DbName.COLUMN_NAME_ID_FNS} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME,
            null,
            selection, arrayOf(idFns),
            null, null, null
        )
        var Id:Int=0
        if(cursor?.moveToNext()!!) {
            Id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
        }
        //if(readDataCallback!=null)readDataCallback.readData(dataList)

        cursor.close()
        return@withContext Id
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
            val idProduct = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_PRODUCT_ID))
            var productName = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_PRODUCT_NAME))
            if(productName==null) productName=""
            val item = PurchaseItem()
            item.price = dataPrice
            item.quantity = dataQuantity
            item.summa = dataSumma
            item.id = dataId
            item.idPurchase = id
            item.idProduct=idProduct
            item.productName=productName
            dataList.add(item)
        }
        //if(readDataCallback!=null)readDataCallback.readData(dataList)

        cursor.close()
        return@withContext dataList
        //return dataList
    }

    @SuppressLint("Range")
    //suspend fun updatePurchaseItem(purchaseItem:PurchaseItem) = withContext(Dispatchers.IO){
    fun updatePurchaseItem(purchaseItem:PurchaseItem){
        val id:Int=purchaseItem.id
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_PRODUCT_NAME, purchaseItem.productName)
            put(DbName.COLUMN_NAME_PRODUCT_ID, purchaseItem.idProduct)
            put(DbName.COLUMN_NAME_PRICE, purchaseItem.price)
            put(DbName.COLUMN_NAME_QUANTITY, purchaseItem.quantity)
            put(DbName.COLUMN_NAME_SUMMA, purchaseItem.summa)
            put(DbName.COLUMN_NAME_PURCHASE_ID, purchaseItem.idPurchase)
            //put(DbName.COLUMN_NAME_TIME, time)
        }
        db?.update(DbName.TABLE_NAME_PURCHASE_ITEMS, values, selection, null)
    }

    @SuppressLint("Range")
    suspend fun insertPurchaseItem(purchaseItem:PurchaseItem):Int? = withContext(Dispatchers.IO){
    //fun insertPurchaseItem(purchaseItem:PurchaseItem):Int?{
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_PRODUCT_NAME, purchaseItem.productName)
            put(DbName.COLUMN_NAME_PRODUCT_ID, purchaseItem.idProduct)
            put(DbName.COLUMN_NAME_PRICE, purchaseItem.price)
            put(DbName.COLUMN_NAME_QUANTITY, purchaseItem.quantity)
            put(DbName.COLUMN_NAME_SUMMA, purchaseItem.summa)
            put(DbName.COLUMN_NAME_PURCHASE_ID, purchaseItem.idPurchase)

        }
        val id=db?.insert(DbName.TABLE_NAME_PURCHASE_ITEMS,null, values)
        return@withContext id?.toInt()
    }

    suspend fun removePurchaseItem(id: Int) = withContext(Dispatchers.IO){
        val selection = BaseColumns._ID + "=${id}"
        db?.delete(DbName.TABLE_NAME_PURCHASE_ITEMS,selection, null)
    }

    suspend fun removePurchaseItems(idpurchase: Int) = withContext(Dispatchers.IO){
        val selection = DbName.COLUMN_NAME_PURCHASE_ID + "=${idpurchase}"
        db?.delete(DbName.TABLE_NAME_PURCHASE_ITEMS,selection, null)
    }

    fun closeDb(){
        myDbHelper.close()
    }

    interface ReadDataCallback {
        fun readData(list: ArrayList<Purchase>)
    }
}
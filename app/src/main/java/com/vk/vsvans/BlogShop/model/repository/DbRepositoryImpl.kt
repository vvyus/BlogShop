package com.vk.vsvans.BlogShop.model.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.vk.vsvans.BlogShop.model.MyDbHelper
import com.vk.vsvans.BlogShop.model.data.*
import com.vk.vsvans.BlogShop.util.FilterForActivity
import com.vk.vsvans.BlogShop.util.UtilsHelper

class DbRepositoryImpl(context: Context):IDbRepository {
    val mDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    override fun openDb() {
        db = mDbHelper.writableDatabase
    }

    override fun closeDb() {
        mDbHelper.close()
    }

    private fun getProductFromCursor(cursor: Cursor): Product {
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
        return product
    }

    override fun getProducts(searchText: String): ArrayList<Product> {
        val dataList = ArrayList<Product>()
        val selection = "${DbName.COLUMN_NAME_NAME_PRODUCTS} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME_PRODUCTS,null,selection,arrayOf("%$searchText%"),//, arrayOf("_id","idparent","name","id_fns","level","count","fullpath"), selection, arrayOf("%$searchText%"),
            null, null, "fullpath ASC"
        )

        while (cursor?.moveToNext()!!) {
            val product=getProductFromCursor(cursor)
            dataList.add(product)
        }
        cursor.close()
        return dataList
    }

    override fun getProductsFns(searchText: String): ArrayList<Product> {
        val dataList = ArrayList<Product>()
        val selection = "${DbName.COLUMN_NAME_ID_FNS_PRODUCTS} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME_PRODUCTS, null, selection, arrayOf("%$searchText%"),
            null, null, null
        )

        while (cursor?.moveToNext()!!) {
            val product=getProductFromCursor(cursor)
            dataList.add(product)
        }
        cursor.close()
        return dataList
    }

    fun getProductContentValues(product: Product): ContentValues {
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_NAME_PRODUCTS, product.name)
            put(DbName.COLUMN_NAME_ID_FNS_PRODUCTS, product.id_fns)
            put(DbName.COLUMN_NAME_IDPARENT_PRODUCTS, product.idparent)
            put(DbName.COLUMN_NAME_LEVEL_PRODUCTS, product.level)
            put(DbName.COLUMN_NAME_FULLPATH_PRODUCTS, product.fullpath)
            put(DbName.COLUMN_NAME_COUNT_PRODUCTS, product.count)
        }
        return values
    }

    override fun insertProduct(product: Product): Int? {
        val values =getProductContentValues(product)
        val id=db?.insert(DbName.TABLE_NAME_PRODUCTS,null, values)
        return id?.toInt()
    }

    override fun updateProduct(product: Product) {
        val id=product.id
        val selection = BaseColumns._ID + "=$id"
        val values =getProductContentValues(product)
        db?.update(DbName.TABLE_NAME_PRODUCTS, values, selection, null)
    }

    override fun removeProduct(id: Int) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(DbName.TABLE_NAME_PRODUCTS,selection, null)
    }

// SELLERS
    private fun getSellerFromCursor(cursor: Cursor): Seller {
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
        return seller
    }

    override fun getSellers(searchText: String): ArrayList<Seller> {
        val dataList = ArrayList<Seller>()
        val selection = "${DbName.COLUMN_NAME_NAME_SELLERS} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME_SELLERS,null,selection,arrayOf("%$searchText%"), //arrayOf("_id","idparent","name","id_fns","level","count","fullpath"), selection, arrayOf("%$searchText%"),
            null, null, "fullpath ASC"
        )
        while (cursor?.moveToNext()!!) {
            val seller=getSellerFromCursor(cursor)
            dataList.add(seller)
        }
        cursor.close()

        return dataList
    }

    override fun getSellersFns(searchText: String): ArrayList<Seller> {
        val dataList = ArrayList<Seller>()
        val selection = "${DbName.COLUMN_NAME_ID_FNS_SELLERS} like ?"
        val cursor = db?.query(
            DbName.TABLE_NAME_SELLERS, null, selection, arrayOf("%$searchText%"),
            null, null, null
        )

        while (cursor?.moveToNext()!!) {
            val seller=getSellerFromCursor(cursor)
            dataList.add(seller)
        }
        cursor.close()
        return dataList
    }

    fun getSellerContentValues(seller: Seller):ContentValues {
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_NAME_SELLERS, seller.name)
            put(DbName.COLUMN_NAME_ID_FNS_SELLERS, seller.id_fns)
            put(DbName.COLUMN_NAME_DESCRIPTION_SELLERS, seller.description)
            put(DbName.COLUMN_NAME_IDPARENT_SELLERS, seller.idparent)
            put(DbName.COLUMN_NAME_LEVEL_SELLERS, seller.level)
            put(DbName.COLUMN_NAME_FULLPATH_SELLERS, seller.fullpath)
            put(DbName.COLUMN_NAME_COUNT_SELLERS, seller.count)
        }
        return values
    }

    override fun updateSeller(seller: Seller) {
        val id=seller.id
        val selection = BaseColumns._ID + "=$id"
        val values = getSellerContentValues(seller)
        db?.update(DbName.TABLE_NAME_SELLERS, values, selection, null)
    }

    override fun insertSeller(seller: Seller): Int? {
        val values = getSellerContentValues(seller)
        val id=db?.insert(DbName.TABLE_NAME_SELLERS,null, values)
        return id?.toInt()
    }

    override fun removeSeller(id: Int) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(DbName.TABLE_NAME_SELLERS,selection, null)
    }
// PURCHASES

    private fun getPurchaseFromCursor(cursor: Cursor): Purchase {
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
        if(sellername!=null)purchase.sellername=sellername

        return purchase
    }

    private fun setPurchaseListFromCursor(cursor: Cursor, purchaseList: ArrayList<Purchase>, calendar_events:HashMap<String, Int>):Double{
        var amount=0.0
        while (cursor?.moveToNext()!!) {

            val purchase=getPurchaseFromCursor(cursor)
            amount+=purchase.summa
            purchaseList.add(purchase)

            val event_key: String = UtilsHelper.getDate(purchase.time)
            var event_int: Int? = calendar_events[event_key]

            if (event_int == null)
                calendar_events[event_key] = 1
            else
                calendar_events[event_key] = ++event_int
        }
        return amount
    }
    private fun setPurchaseListFromCursor(cursor: Cursor, purchaseList: ArrayList<Purchase>):Double{
        var amount=0.0
        while (cursor?.moveToNext()!!) {

            val purchase=getPurchaseFromCursor(cursor)
            amount+=purchase.summa
            purchaseList.add(purchase)
        }
        return amount
    }

    override fun getPurchases(filter: FilterForActivity, purchaseList: ArrayList<Purchase>,
                              calendar_events: HashMap<String, Int>): Double {

        var selection =""
        var args = ArrayList<String>()
        if(filter.idSeller!=null){
            selection += "${DbName.COLUMN_NAME_SELLER_ID} = ? AND "
            args.add(filter.idSeller.toString())
        }
        if(filter.content!=null) {
            selection += "${DbName.COLUMN_NAME_CONTENT} like ? AND "
            args.add("%"+filter.content + "%")
        }
        if(filter.dates_begin!=null && filter.dates_end!=null) {
            for (i in filter.dates_begin!!.indices) {
                if(i==0)selection+="("
                selection += "${DbName.COLUMN_NAME_TIME} >= " + filter.dates_begin!![i] + " AND ${DbName.COLUMN_NAME_TIME}<=" + filter.dates_end!![i]
                if (i < filter.dates_begin!!.size - 1) selection += " OR " else selection +=")" //" AND "
            }
        }
        if(selection.endsWith(" AND "))selection=selection.substring(0, selection.length - 5)
        val selectionArgs: Array<String> = args.toTypedArray()
        println(" !!! "+selection)
        //selectionArgs = arrayOf(idSeller.toString())

        val temp: String = DbName.PURCHASE_QUERY
        val selectQuery: String = temp.replace(
            DbName.WHERE_FOR_PURCHASE_QUERY,
            if(selection=="") "" else "WHERE $selection "
        )
        val cursor = db?.rawQuery(selectQuery, selectionArgs)
        var amount=0.0
        if(cursor!=null){
            amount=setPurchaseListFromCursor(cursor!!,purchaseList,calendar_events)
            cursor!!.close()
        }
        //return@withContext purchaseList
        return amount

    }

    override fun getPurchases(filter: FilterForActivity, purchaseList: ArrayList<Purchase>): Double {
        var selection =""
        var args = ArrayList<String>()
        if(filter.idSeller!=null){
            selection += "${DbName.COLUMN_NAME_SELLER_ID} = ? AND "
            args.add(filter.idSeller.toString())
        }
        if(filter.content!=null) {
            selection += "${DbName.COLUMN_NAME_CONTENT} like ? AND "
            args.add("%"+filter.content + "%")
        }
        if(filter.dates_begin!=null && filter.dates_end!=null) {
            for (i in filter.dates_begin!!.indices) {
                selection += "${DbName.COLUMN_NAME_TIME} >= " + filter.dates_begin!![i] + " AND ${DbName.COLUMN_NAME_TIME}<=" + filter.dates_end!![i]
                if (i < filter.dates_begin!!.size - 1) selection += " OR " else selection += " AND "
            }
        }
        if(selection.endsWith(" AND "))selection=selection.substring(0, selection.length - 5)
        val selectionArgs: Array<String> = args.toTypedArray()
        //selectionArgs = arrayOf(idSeller.toString())

        val temp: String = DbName.PURCHASE_QUERY
        val selectQuery: String = temp.replace(
            DbName.WHERE_FOR_PURCHASE_QUERY,
            if(selection=="") "" else "WHERE $selection "
        )
        val cursor = db?.rawQuery(selectQuery, selectionArgs)
        var amount=0.0
        if(cursor!=null){
            amount=setPurchaseListFromCursor(cursor!!,purchaseList)
            cursor!!.close()
        }
        //return@withContext purchaseList
        return amount

    }

    override fun getPurchaseFns(idFns: String): Int {
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

        cursor.close()
        return Id
    }

    override fun getPurchase(id: Int): Purchase? {
        val selection ="${DbName.TABLE_NAME}"+"."+BaseColumns._ID + "=?"
        val selectionArgs = arrayOf(id.toString())
        val temp: String = DbName.PURCHASE_QUERY
        val selectQuery: String = temp.replace(
            DbName.WHERE_FOR_PURCHASE_QUERY,
            "WHERE $selection "
        )
        val cursor = db?.rawQuery(selectQuery, selectionArgs)
        var amount=0.0
        var purchase : Purchase?= null
        val purchaseList= ArrayList<Purchase>()
        if(cursor!=null){
            amount=setPurchaseListFromCursor(cursor!!,purchaseList)
            purchase=if(purchaseList.size>0) purchaseList[0] else null
            cursor!!.close()
        }
        return purchase

    }

    fun getPurchaseContentValues(purchase: Purchase):ContentValues {
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_TITLE, purchase.title)
            put(DbName.COLUMN_NAME_CONTENT, purchase.content)
            put(DbName.COLUMN_NAME_SUMMA_PURCHASES, purchase.summa)
            put(DbName.COLUMN_NAME_CONTENT_HTML,purchase.content_html)
            put(DbName.COLUMN_NAME_ID_FNS, purchase.idfns)
            put(DbName.COLUMN_NAME_TIME, purchase.time)
            put(DbName.COLUMN_NAME_SELLER_ID, purchase.idseller)
        }
        return values
    }
    override fun insertPurchase(purchase: Purchase): Int? {
        val values=getPurchaseContentValues(purchase)
        val id=db?.insert(DbName.TABLE_NAME,null, values)
        return id?.toInt()
    }

    override fun updatePurchase(purchase: Purchase) {
        val id=purchase.id
        val selection = BaseColumns._ID + "=$id"
        val values=getPurchaseContentValues(purchase)
        db?.update(DbName.TABLE_NAME, values, selection, null)
    }

    override fun removePurchase(id: Int) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(DbName.TABLE_NAME,selection, null)
    }

// PURCHASE ITEMS

    fun getPurchaseItemContentValues(purchaseItem: PurchaseItem):ContentValues {
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_PRODUCT_NAME, purchaseItem.productName)
            put(DbName.COLUMN_NAME_PRODUCT_ID, purchaseItem.idProduct)
            put(DbName.COLUMN_NAME_PRICE, purchaseItem.price)
            put(DbName.COLUMN_NAME_QUANTITY, purchaseItem.quantity)
            put(DbName.COLUMN_NAME_SUMMA, purchaseItem.summa)
            put(DbName.COLUMN_NAME_PURCHASE_ID, purchaseItem.idPurchase)

        }
        return values
    }

    override fun getPurchaseItems(id: Int): ArrayList<PurchaseItem> {
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

        cursor.close()
        return dataList

    }

    override fun updatePurchaseItem(purchaseItem: PurchaseItem) {
        val id:Int=purchaseItem.id
        val selection = BaseColumns._ID + "=$id"
        val values = getPurchaseItemContentValues(purchaseItem)
        db?.update(DbName.TABLE_NAME_PURCHASE_ITEMS, values, selection, null)
    }

    override fun insertPurchaseItem(purchaseItem: PurchaseItem): Int? {
        val values = getPurchaseItemContentValues(purchaseItem)
        val id=db?.insert(DbName.TABLE_NAME_PURCHASE_ITEMS,null, values)
        return id?.toInt()
    }

    override fun removePurchaseItem(id: Int) {
        val selection = BaseColumns._ID + "=${id}"
        db?.delete(DbName.TABLE_NAME_PURCHASE_ITEMS,selection, null)
    }

    override fun removePurchaseItems(idpurchase: Int) {
        val selection = DbName.COLUMN_NAME_PURCHASE_ID + "=${idpurchase}"
        db?.delete(DbName.TABLE_NAME_PURCHASE_ITEMS,selection, null)
    }

// PRODUCT AMOUNT

    private fun getProductAmountFromCursor(cursor: Cursor): ProductAmount {
        val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
        val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_ID_FNS_PRODUCTS))
        val dataName = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_NAME_PRODUCTS))
        val idparent = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_IDPARENT_PRODUCTS))
        val level = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_LEVEL_PRODUCTS))
        val fullpath = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_FULLPATH_PRODUCTS))
        val count = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_COUNT_PRODUCTS))
        val productAmount = ProductAmount()
        productAmount.id = dataId
        productAmount.name = dataName
        productAmount.id_fns=dataTitle
        productAmount.level = level?:0
        productAmount.idparent = idparent?:0
        productAmount.fullpath = fullpath?:""
        productAmount.count = count?:0
        productAmount.monthAmount=cursor.getDouble(cursor.getColumnIndex("monthamount"))
        productAmount.yearAmount=cursor.getDouble(cursor.getColumnIndex("yearamount"))
        productAmount.weekAmount=cursor.getDouble(cursor.getColumnIndex("weekamount"))
        return productAmount
    }

    override fun getProductAmount(searchText: String,time:Long): ArrayList<ProductAmount> {
        val dataList = ArrayList<ProductAmount>()
//        val selection = "${DbName.COLUMN_NAME_NAME_PRODUCTS} like ?"
//        val cursor = db?.query(
//            DbName.TABLE_NAME_PRODUCTS,null,selection,arrayOf("%$searchText%"),//, arrayOf("_id","idparent","name","id_fns","level","count","fullpath"), selection, arrayOf("%$searchText%"),
//            null, null, "fullpath ASC"
//        )
        val datec=UtilsHelper.correct_date_end(time).toString()
        val datey=UtilsHelper.getFirstDayOfYear(time).toString()
        val datem=UtilsHelper.getFirstDayOfMonth(time).toString()
        val datew=UtilsHelper.getFirstDayOfWeek(time).toString()
        val selectionArgs = arrayOf(datey,datec,datem,datec,datew,datec,datey,datec,datem,datec,datew,datec)
        val selectQuery: String = DbName.PRODUCT_AMOUNT_QUERY
        val cursor = db?.rawQuery(selectQuery, selectionArgs)
        while (cursor?.moveToNext()!!) {
            val product=getProductAmountFromCursor(cursor)
            dataList.add(product)
        }
        cursor.close()
        return dataList
    }
// seller amount
    private fun getSellerAmountFromCursor(cursor: Cursor): SellerAmount {
        val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
        val dataTitle = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_ID_FNS_SELLERS))
        val dataName = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_NAME_SELLERS))
        val idparent = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_IDPARENT_SELLERS))
        val level = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_LEVEL_SELLERS))
        val fullpath = cursor.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_FULLPATH_SELLERS))
        val count = cursor.getInt(cursor.getColumnIndex(DbName.COLUMN_NAME_COUNT_SELLERS))
        val sellerAmount = SellerAmount()
        sellerAmount.id = dataId
        sellerAmount.name = dataName
        sellerAmount.id_fns=dataTitle
        sellerAmount.level = level?:0
        sellerAmount.idparent = idparent?:0
        sellerAmount.fullpath = fullpath?:""
        sellerAmount.count = count?:0
        sellerAmount.monthAmount=cursor.getDouble(cursor.getColumnIndex("monthamount"))
        sellerAmount.yearAmount=cursor.getDouble(cursor.getColumnIndex("yearamount"))
        sellerAmount.weekAmount=cursor.getDouble(cursor.getColumnIndex("weekamount"))
        return sellerAmount
    }

    override fun getSellerAmount(searchText: String,time:Long): ArrayList<SellerAmount> {
        val dataList = ArrayList<SellerAmount>()
        val datec=UtilsHelper.correct_date_end(time).toString()
        val datey=UtilsHelper.getFirstDayOfYear(time).toString()
        val datem=UtilsHelper.getFirstDayOfMonth(time).toString()
        val datew=UtilsHelper.getFirstDayOfWeek(time).toString()
        val selectionArgs = arrayOf(datey,datec,datem,datec,datew,datec,datey,datec,datem,datec,datew,datec)
        val selectQuery: String = DbName.SELLER_AMOUNT_QUERY
        val cursor = db?.rawQuery(selectQuery, selectionArgs)
        while (cursor?.moveToNext()!!) {
            val seller=getSellerAmountFromCursor(cursor)
            dataList.add(seller)
        }
        cursor.close()
        return dataList
    }


    // ADD
    override fun getAllPurchases(filter: FilterForActivity,readDataCallback: IDbRepository.ReadDataCallback?){
        val purchaseArray=ArrayList<Purchase>()
        val purchaseCalendarEvents= HashMap<String, Int>()
        var amount=0.0
        amount=getPurchases(filter,purchaseArray,purchaseCalendarEvents)
        if(readDataCallback!=null)readDataCallback.readData(purchaseArray,purchaseCalendarEvents,
            amount
        )
    }

    override fun getAllProducts(filterString:String,readProductCallback: IDbRepository.ReadProductCallback?){
        //var productArray=ArrayList<Product>()
        val productArray=getProducts(filterString)
        if(readProductCallback!=null)readProductCallback.readData(productArray)
    }

    override fun getAllSellers(filterString: String, readSellerCallback: IDbRepository.ReadSellerCallback?) {
        val sellerArray=getSellers(filterString)
        if(readSellerCallback!=null)readSellerCallback.readData(sellerArray)
    }

    override fun getAllPurchaseItems(idPurchase: Int, readPurchaseItemCallback: IDbRepository.ReadPurchaseItemCallback?) {
        val purchaseItemArray= getPurchaseItems(idPurchase)
        if(readPurchaseItemCallback!=null)readPurchaseItemCallback.readData(purchaseItemArray)
    }

    override fun getAllProductAmount(filterString:String,readProductAmountCallback: IDbRepository.ReadProductAmountCallback?,time:Long){

        val productArray=getProductAmount(filterString,time)
        if(readProductAmountCallback!=null)readProductAmountCallback.readData(productArray)
    }

    override fun getAllSellerAmount(filterString: String, readSellerCallback: IDbRepository.ReadSellerAmountCallback?, time: Long) {
        val sellerArray=getSellerAmount(filterString,time)
        if(readSellerCallback!=null)readSellerCallback.readData(sellerArray)
    }
}
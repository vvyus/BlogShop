package com.vk.vsvans.BlogShop.model
import android.provider.BaseColumns

object DbName {
        const val DATABASE_VERSION = 15
        const val DATABASE_NAME = "BlogShopDb.db"
  // Table Purchase
        const val TABLE_NAME = "Purchases"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_CONTENT = "content"
    const val COLUMN_NAME_CONTENT_HTML = "content_html"
        const val COLUMN_NAME_SUMMA_PURCHASES = "summa"
        const val COLUMN_NAME_TIME = "time"
    const val COLUMN_NAME_ID_FNS = "id_fns"
        const val CREAT_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_TITLE TEXT," +
                "$COLUMN_NAME_CONTENT TEXT," +
                "$COLUMN_NAME_CONTENT_HTML TEXT,"+
                "$COLUMN_NAME_SUMMA_PURCHASES DOUBLE,"+
                "$COLUMN_NAME_TIME LONG," +
                "$COLUMN_NAME_ID_FNS TEXT" +
                ")"
        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

        // Table Sellers
        const val TABLE_NAME_SELLERS = "Sellers"
        const val COLUMN_NAME_TITLE_SELLERS = "title"
    const val COLUMN_NAME_NAME_SELLERS = "name"
        const val COLUMN_NAME_DESCRIPTION_SELLERS = "description"

        const val CREAT_TABLE_SELLERS = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_SELLERS (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_TITLE_SELLERS TEXT," +
                "$COLUMN_NAME_NAME_SELLERS TEXT," +
                "$COLUMN_NAME_DESCRIPTION_SELLERS TEXT" +
                ")"
        const val SQL_DELETE_TABLE_SELLERS = "DROP TABLE IF EXISTS $TABLE_NAME_SELLERS"

        // Table Purchase Items
        const val TABLE_NAME_PURCHASE_ITEMS = "PurchaseItems"
        const val COLUMN_NAME_PURCHASE_ID = "idpurchase"
      const val COLUMN_NAME_PRODUCT_ID = "idproduct"
        const val COLUMN_NAME_PRICE = "price"
        const val COLUMN_NAME_QUANTITY = "quantity"
        const val COLUMN_NAME_SUMMA = "summa"
        const val COLUMN_NAME_PRODUCT_NAME = "productname"

        const val CREAT_TABLE_PURCHASE_ITEMS = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_PURCHASE_ITEMS (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_PURCHASE_ID INTEGER," +
                "$COLUMN_NAME_PRICE DOUBLE," +
                "$COLUMN_NAME_QUANTITY DOUBLE," +
                "$COLUMN_NAME_SUMMA DOUBLE," +
                "$COLUMN_NAME_PRODUCT_ID INTEGER," +
                "$COLUMN_NAME_PRODUCT_NAME TEXT"+
                ")"
        const val SQL_DELETE_TABLE_PURCHASE_ITEMS= "DROP TABLE IF EXISTS $TABLE_NAME_PURCHASE_ITEMS"

    // TABLE PRODUCTS
    const val TABLE_NAME_PRODUCTS = "Products"
    const val COLUMN_NAME_TITLE_PRODUCTS = "title"
    const val COLUMN_NAME_NAME_PRODUCTS = "name"
    const val COLUMN_NAME_BCOLOR_PRODUCTS = "bcolor"
    const val COLUMN_NAME_IDPARENT_PRODUCTS = "idparent"
    const val COLUMN_NAME_LEVEL_PRODUCTS = "level"
    const val COLUMN_NAME_FULLPATH_PRODUCTS = "fullpath"
    const val CREAT_TABLE_PRODUCTS = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_PRODUCTS (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_NAME_TITLE_PRODUCTS TEXT," +
            "$COLUMN_NAME_NAME_PRODUCTS TEXT," +
            "$COLUMN_NAME_BCOLOR_PRODUCTS INTEGER," +
            "$COLUMN_NAME_IDPARENT_PRODUCTS INTEGER," +
            "$COLUMN_NAME_LEVEL_PRODUCTS INTEGER," +
            "$COLUMN_NAME_FULLPATH_PRODUCTS TEXT"+
            ")"
    const val SQL_DELETE_TABLE_PRODUCTS = "DROP TABLE IF EXISTS $TABLE_NAME_PRODUCTS"

}
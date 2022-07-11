package com.vk.vsvans.BlogShop.model.repository
import android.provider.BaseColumns

object DbName {
        const val DATABASE_VERSION = 23
        const val DATABASE_NAME = "BlogShopDb.db"
  // Table Purchase
        const val TABLE_NAME = "Purchases"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_CONTENT = "content"
        const val COLUMN_NAME_CONTENT_HTML = "content_html"
        const val COLUMN_NAME_SUMMA_PURCHASES = "summa"
        const val COLUMN_NAME_TIME = "time"
        const val COLUMN_NAME_ID_FNS = "id_fns"
        const val COLUMN_NAME_SELLER_ID = "idseller"
        const val COLUMN_NAME_SELLER_NAME = "sellername"

        const val CREAT_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_TITLE TEXT," +
                "$COLUMN_NAME_CONTENT TEXT," +
                "$COLUMN_NAME_CONTENT_HTML TEXT,"+
                "$COLUMN_NAME_SUMMA_PURCHASES DOUBLE,"+
                "$COLUMN_NAME_TIME LONG," +
                "$COLUMN_NAME_ID_FNS TEXT," +
                "$COLUMN_NAME_SELLER_ID INTEGER," +
                "$COLUMN_NAME_SELLER_NAME TEXT"+
                ")"
        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

        // Table Sellers
        const val TABLE_NAME_SELLERS = "Sellers"
    // поле title ключ для фнс
        const val COLUMN_NAME_ID_FNS_SELLERS = "id_fns"
        const val COLUMN_NAME_NAME_SELLERS = "name"
        const val COLUMN_NAME_DESCRIPTION_SELLERS = "description"
        const val COLUMN_NAME_IDPARENT_SELLERS = "idparent"
        const val COLUMN_NAME_LEVEL_SELLERS = "level"
        const val COLUMN_NAME_FULLPATH_SELLERS = "fullpath"
        const val COLUMN_NAME_COUNT_SELLERS = "count"
        const val CREAT_TABLE_SELLERS = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_SELLERS (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_ID_FNS_SELLERS TEXT," +
                "$COLUMN_NAME_NAME_SELLERS TEXT," +
                "$COLUMN_NAME_DESCRIPTION_SELLERS TEXT," +
                "$COLUMN_NAME_IDPARENT_SELLERS INTEGER," +
                "$COLUMN_NAME_LEVEL_SELLERS INTEGER," +
                "$COLUMN_NAME_COUNT_SELLERS INTEGER," +
                "$COLUMN_NAME_FULLPATH_SELLERS TEXT"+
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
    // поле title ключ для фнс
    const val COLUMN_NAME_ID_FNS_PRODUCTS = "id_fns"
    const val COLUMN_NAME_NAME_PRODUCTS = "name"
    const val COLUMN_NAME_BCOLOR_PRODUCTS = "bcolor"
    const val COLUMN_NAME_IDPARENT_PRODUCTS = "idparent"
    const val COLUMN_NAME_LEVEL_PRODUCTS = "level"
    const val COLUMN_NAME_FULLPATH_PRODUCTS = "fullpath"
    const val COLUMN_NAME_COUNT_PRODUCTS = "count"
    const val CREAT_TABLE_PRODUCTS = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_PRODUCTS (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_NAME_ID_FNS_PRODUCTS TEXT," +
            "$COLUMN_NAME_NAME_PRODUCTS TEXT," +
            "$COLUMN_NAME_BCOLOR_PRODUCTS INTEGER," +
            "$COLUMN_NAME_IDPARENT_PRODUCTS INTEGER," +
            "$COLUMN_NAME_LEVEL_PRODUCTS INTEGER," +
            "$COLUMN_NAME_COUNT_PRODUCTS INTEGER," +
            "$COLUMN_NAME_FULLPATH_PRODUCTS TEXT"+
            ")"
    const val SQL_DELETE_TABLE_PRODUCTS = "DROP TABLE IF EXISTS $TABLE_NAME_PRODUCTS"

    const val WHERE_FOR_PURCHASE_QUERY = "'%WHERETRUE%'"
    val PURCHASE_QUERY = "SELECT $TABLE_NAME.${BaseColumns._ID} as ${BaseColumns._ID}," +
            "$TABLE_NAME.$COLUMN_NAME_TITLE as $COLUMN_NAME_TITLE," +
            "$COLUMN_NAME_CONTENT,$COLUMN_NAME_CONTENT_HTML," +
            "$TABLE_NAME.$COLUMN_NAME_ID_FNS,$COLUMN_NAME_TIME,$COLUMN_NAME_SUMMA_PURCHASES," +
            "$TABLE_NAME_SELLERS.$COLUMN_NAME_NAME_SELLERS AS $COLUMN_NAME_SELLER_NAME," +
            "$TABLE_NAME_SELLERS.${BaseColumns._ID} AS $COLUMN_NAME_SELLER_ID" +
            " FROM $TABLE_NAME " + "LEFT JOIN $TABLE_NAME_SELLERS "+
            "ON $TABLE_NAME.$COLUMN_NAME_SELLER_ID=$TABLE_NAME_SELLERS.${BaseColumns._ID} " +
             WHERE_FOR_PURCHASE_QUERY +
            "ORDER BY $COLUMN_NAME_TIME DESC"
    //ADD
    // case when p._id=p.idparent then p.idparent else p._id end = t.idproduct
    val PRODUCT_AMOUNT_QUERY="SELECT p._id,p.name,p.id_fns,p.bcolor,p.idparent,p.level,p.fullpath,p.count,t.yearamount,t.monthamount,t.weekamount " +
            "FROM products as p LEFT JOIN "+
            "( SELECT idproduct,SUM(yearamount) as yearamount,SUM(monthamount) as monthamount,SUM(weekamount) as weekamount FROM (" +
            "SELECT PurchaseItems.idproduct,PurchaseItems.summa as yearamount,0 as monthamount,0 as weekamount FROM PurchaseItems " +
            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id WHERE Purchases.time>=? " +
            "UNION ALL SELECT PurchaseItems.idproduct,0,PurchaseItems.summa,0 FROM PurchaseItems "+
            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id WHERE Purchases.time>=? "+
            "UNION ALL SELECT PurchaseItems.idproduct,0,0,PurchaseItems.summa FROM PurchaseItems "+
            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id WHERE Purchases.time>=? " +
            //
            "UNION ALL SELECT PR.idparent,PurchaseItems.summa,0,0 FROM PurchaseItems "+
            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id " +
            "LEFT JOIN Products as PR ON PurchaseItems.idproduct=PR._id WHERE PR._id!=PR.idparent and Purchases.time>=? " +
            //
            "UNION ALL SELECT PR.idparent,0,PurchaseItems.summa,0 FROM PurchaseItems "+
            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id " +
            "LEFT JOIN Products as PR ON PurchaseItems.idproduct=PR._id WHERE PR._id!=PR.idparent and Purchases.time>=? " +
            //
            "UNION ALL SELECT PR.idparent,0,0,PurchaseItems.summa FROM PurchaseItems "+
            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id " +
            "LEFT JOIN Products as PR ON PurchaseItems.idproduct=PR._id WHERE PR._id!=PR.idparent and Purchases.time>=? " +
            //
            ") GROUP BY idproduct ) t ON p._id = t.idproduct ORDER BY p.fullpath ASC"

//    val PRODUCT_AMOUNT_QUERY="select _id,name,id_fns,bcolor,idparent,level,fullpath,count,yearamount,monthamount,weekamount from(" +
//            "SELECT p._id,p.name,p.id_fns,p.bcolor,p.idparent,p.level,p.fullpath,p.count,t.yearamount,t.monthamount,t.weekamount " +
//            "FROM products as p LEFT JOIN "+
//            "( SELECT idproduct,SUM(yearamount) as yearamount,SUM(monthamount) as monthamount,SUM(weekamount) as weekamount FROM (" +
//            "SELECT PurchaseItems.idproduct,PurchaseItems.summa as yearamount,0 as monthamount,0 as weekamount FROM PurchaseItems " +
//            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id WHERE Purchases.time>=? " +
//            "UNION ALL SELECT PurchaseItems.idproduct,0,PurchaseItems.summa,0 FROM PurchaseItems "+
//            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id WHERE Purchases.time>=? "+
//            "UNION ALL SELECT PurchaseItems.idproduct,0,0,PurchaseItems.summa FROM PurchaseItems "+
//            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id WHERE Purchases.time>=? " +
//            //
//            "UNION ALL SELECT PR.idparent,PurchaseItems.summa,0,0 FROM PurchaseItems "+
//            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id " +
//            "LEFT JOIN Products as PR ON PurchaseItems.idproduct=PR._id WHERE PR._id!=PR.idparent and Purchases.time>=? " +
//            //
//            "UNION ALL SELECT PR.idparent,0,PurchaseItems.summa,0 FROM PurchaseItems "+
//            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id " +
//            "LEFT JOIN Products as PR ON PurchaseItems.idproduct=PR._id WHERE PR._id!=PR.idparent and Purchases.time>=? " +
//            //
//            "UNION ALL SELECT PR.idparent,0,0,PurchaseItems.summa FROM PurchaseItems "+
//            "LEFT JOIN Purchases ON PurchaseItems.idpurchase=Purchases._id " +
//            "LEFT JOIN Products as PR ON PurchaseItems.idproduct=PR._id WHERE PR._id!=PR.idparent and Purchases.time>=? " +
//            //
//            ") GROUP BY idproduct ) t ON p._id = t.idproduct ORDER BY p.fullpath ASC) ORDER BY fullpath ASC,count DESC"

}
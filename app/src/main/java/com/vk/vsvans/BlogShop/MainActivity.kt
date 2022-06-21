package com.vk.vsvans.BlogShop

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.vk.vsvans.BlogShop.activity.EditPurchaseActivity
import com.vk.vsvans.BlogShop.activity.ProductActivity
import com.vk.vsvans.BlogShop.activity.SellerActivity
import com.vk.vsvans.BlogShop.adapters.PurchaseRcAdapter
import com.vk.vsvans.BlogShop.databinding.ActivityMainBinding
import com.vk.vsvans.BlogShop.dialogs.DialogHelper
import com.vk.vsvans.BlogShop.fns.import_checks
import com.vk.vsvans.BlogShop.interfaces.IDeleteItem
import com.vk.vsvans.BlogShop.interfaces.IDialogDateFiterCallback
import com.vk.vsvans.BlogShop.interfaces.IFilterCallBack
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.BaseList
import com.vk.vsvans.BlogShop.model.DbManager
import com.vk.vsvans.BlogShop.model.Purchase
import com.vk.vsvans.BlogShop.utils.FilterForActivity
import com.vk.vsvans.BlogShop.utils.UtilsHelper
import com.vk.vsvans.BlogShop.utils.isPermissinGrant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var rootElement: ActivityMainBinding
    val dbManager= DbManager(this)
    private val purchaseArray=ArrayList<Purchase>()
    private var job: Job? = null
    private lateinit var toolbar: Toolbar
    private lateinit var searchView:SearchView
    //private var isSetFilter=false
    lateinit var pLauncher:ActivityResultLauncher<String>
    private val filter_fact=FilterForActivity("main")

    val adapter= PurchaseRcAdapter(object:OnClickItemCallback{
        override fun onClickItem(id:Int) {
            if(id>0) {
                val intent = Intent(this@MainActivity, EditPurchaseActivity::class.java)
                intent.putExtra(R.string.PURCHASE_ID.toString(), id)
                // сообщаем системе о запуске активити
                startActivity(intent)
            }
        }

        override fun onEditItem() {}

        override fun onDeleteItem() {}
        override fun onNewItem(parent: BaseList) {}
        override fun refreshItem() {}
        override fun onParentItem() {}

    })

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        mainActivity=this
        rootElement= ActivityMainBinding.inflate(layoutInflater)
        val view=rootElement.root
        setContentView(view)

        init()

        initRecyclerView()
        bottomMenuOnClick()
        adapter.setFilterCallback(object: IFilterCallBack {
            override fun onTimeClick(time:Long) {
                DialogHelper.getCalendarDialog(this@MainActivity,object: IDialogDateFiterCallback {
                    override fun confirmFilter(selected_date: HashMap<String, Date?>) {
                        if (selected_date.size != 0) {
                            val dates = ArrayList(selected_date.values)
                            Collections.sort(dates, object : Comparator<Date?> {

                                override fun compare(o1: Date?, o2: Date?): Int {
                                    if (o1 != null) {
                                        return o1.compareTo(o2)
                                    }else return 0
                                }
                            })
                            filter_fact.dates_begin = ArrayList<String>()
                            filter_fact.dates_end = ArrayList<String>()
                            var str: String?
                            for (i in 0 until selected_date.size) {
                                str =
                                    java.lang.String.valueOf(UtilsHelper.correct_date_begin(dates[i]!!.time))
                                filter_fact.dates_begin!!.add(str)
                                str =
                                    java.lang.String.valueOf(UtilsHelper.correct_date_end(dates[i]!!.time))
                                filter_fact.dates_end!!.add(str)
                            }

                            fillAdapter()
                        }
                    }

                    override fun cancelFilter() {
                        filter_fact.dates_begin=null
                        filter_fact.dates_end=null
                        resetFilterPanel()
                        fillAdapter()
                    }
                },filter_fact,time)
            }
            override fun onSellerClick(purchase: Purchase) {
                this@MainActivity.onSellerClick(purchase)
            }
        })
    }

     @RequiresApi(Build.VERSION_CODES.N)
    fun onSellerClick(purchase: Purchase) {
        var str=""
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch{
            if( filter_fact.idSeller!=null ) {
                filter_fact.idSeller=null
//                fillAdapter(str)
//                resetFilterPanel()
            }else{
                filter_fact.idSeller=purchase.idseller
//                val purchaseList = ArrayList<Purchase>()
//                val amount = dbManager.queryPurchases(purchase.idseller,purchaseList)
//                adapter.updateAdapter(purchaseList)
//                setFilterPanel(amount,purchaseList.size)
            }
            val purchaseList = ArrayList<Purchase>()
            val amount = dbManager.queryPurchases(filter_fact,purchaseList)
            adapter.updateAdapter(purchaseList)
            if(isSetFilter()) setFilterPanel(amount,purchaseList.size)
            else resetFilterPanel()
        }
    }

    fun isSetFilter():Boolean{
        return !(filter_fact.idSeller==null && filter_fact.dates_begin==null && filter_fact.dates_end==null && filter_fact.content==null)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        dbManager.openDb()
        fillAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun fillAdapter() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch{
            val purchaseList = ArrayList<Purchase>()
            //val amount = dbManager.queryPurchases(text,purchaseList)
//            if(text.isEmpty()) filter_fact.content=null
//            else filter_fact.content=text
            val amount = dbManager.queryPurchases(filter_fact,purchaseList)
            adapter.updateAdapter(purchaseList)

            if(isSetFilter()) setFilterPanel(amount,purchaseList.size)
            else resetFilterPanel()
        }
    }
    private fun init(){
        rootElement.navView.setNavigationItemSelectedListener(this)

        toolbar=rootElement.mainContent.toolbar
        toolbar.title=""
        setSupportActionBar(toolbar)

        val toggle= ActionBarDrawerToggle(this,rootElement.drawerLayout,toolbar,R.string.open,R.string.close)
        rootElement.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_choose_purchase_item, menu)
        setUpToolbar()
        //return super.onCreateOptionsMenu(menu)
        return true
    }

    private fun initRecyclerView(){
        rootElement.apply {
            //если просто указатб this это будет ссылка на rootElement поэтому this@MainActivity
            mainContent.rcView.layoutManager= LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter=adapter
        }
    }

    fun setFilterPanel(amount:Double,count:Int){
       // isSetFilter=true
        showFilterPanel(amount,count)
    }

    fun resetFilterPanel(){
       // isSetFilter=false
        showFilterPanel()
    }

    private fun showFilterPanel(amount:Double,count: Int){
        val str_amount=amount.toString().format(R.string.double_format)
        val str_count=count.toString().format(R.string.int_format)
        rootElement.mainContent.tvFilteredSumma.text=str_amount
        rootElement.mainContent.tvFilteredCount.text=str_count
        rootElement.mainContent.llFilterdPanel.visibility=View.VISIBLE
    }

    private fun showFilterPanel(){
        rootElement.mainContent.tvFilteredSumma.text=""
        rootElement.mainContent.tvFilteredCount.text=""
        rootElement.mainContent.llFilterdPanel.visibility=View.GONE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.id_purchase_food->{
                Toast.makeText(this,"Pressed food", Toast.LENGTH_LONG).show()
            }
            R.id.id_purchase_drug->{
                Toast.makeText(this,"Pressed drug", Toast.LENGTH_LONG).show()
            }else-> rootElement.drawerLayout.closeDrawer(GravityCompat.START)

        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun bottomMenuOnClick()=with(rootElement){
        mainContent.bNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.id_new_purchase->{
                    val i= Intent(this@MainActivity, EditPurchaseActivity::class.java)
                    i.putExtra(R.string.PURCHASE_ID.toString(),0)
                    // сообщаем системе о запуске активити
                    startActivity(i)
                    //Toast.makeText(this@MainActivity,"Pressed new purchase", Toast.LENGTH_LONG).show()
                }

//                R.id.id_refresh_purchase->{
//                    //resetFilterPanel("")
//                    isSetFilter=false
//                    fillAdapter("")
//                }
//
                R.id.id_delete_purchase->{
                    val id=adapter.getPurchaseId()
                    if(id>0) {
                        //dbManager.removePurchaseItemFromDb(id)
                        DialogHelper.showPurchaseDeleteItemDialog(this@MainActivity,id,object:
                            IDeleteItem {
                            @RequiresApi(Build.VERSION_CODES.N)
                            override fun onDeleteItem(id: Int) {
                                dbManager.removePurchase(id)
                                fillAdapter()
                            }

                        })
                    }
                }
                R.id.id_product->{
                    val intent= Intent(this@MainActivity, ProductActivity::class.java)
                    intent.putExtra(R.string.PURCHASE_ID.toString(),0)
                    // сообщаем системе о запуске активити
                    startActivity(intent)
                    //Toast.makeText(this@MainActivity,"Pressed new purchase", Toast.LENGTH_LONG).show()
                }
                R.id.id_seller->{
                    val intent= Intent(this@MainActivity, SellerActivity::class.java)
                    intent.putExtra(R.string.PURCHASE_ID.toString(),0)
                    // сообщаем системе о запуске активити
                    startActivity(intent)
                    //Toast.makeText(this@MainActivity,"Pressed new purchase", Toast.LENGTH_LONG).show()
                }
                R.id.id_import_checks->{

                    job?.cancel()
                    job = CoroutineScope(Dispatchers.Main).launch{
                        import_checks.doImport(this@MainActivity)
                        fillAdapter()
                    }
                }
                }//when
            true
        }
    }

    private fun permissionListener(){
        pLauncher=registerForActivityResult( ActivityResultContracts.RequestPermission() ) {
        }
    }

    private fun checkPermission(){
        permissionListener()
        if(!isPermissinGrant(Manifest.permission.READ_EXTERNAL_STORAGE))
            pLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun setUpToolbar() {
        rootElement.apply {

            //toolbar.inflateMenu(R.menu.menu_choose_purchase_item)

            val searchItem: MenuItem =toolbar.menu.findItem(R.id.action_search)
            if (searchItem != null) {
                searchView = MenuItemCompat.getActionView(searchItem) as SearchView
                searchView.setOnCloseListener(object : SearchView.OnCloseListener {
                    override fun onClose(): Boolean {
                        return true
                    }
                })

                val searchPlate =        searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
                searchPlate.hint = getString(R.string.search_hint)
                val searchPlateView: View =
                    searchView.findViewById(androidx.appcompat.R.id.search_plate)
                searchPlateView.setBackgroundColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        android.R.color.transparent
                    )
                )

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText != null) {
                            if(newText.isEmpty()){
                                //isSetFilter=false
                                filter_fact.content=null
                            } else {
                                filter_fact.content=newText
                                //isSetFilter=true
                            }
                            fillAdapter()//call to showFilterPanel
                            //val amount = dbManager.queryPurchases(filter_fact,purchaseList)
                        }
                        return true
                    }
                })

                val searchManager =
                    getSystemService(Context.SEARCH_SERVICE) as SearchManager
                searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            }//search_item

            // GO BACK
            toolbar.setNavigationOnClickListener {
 //               onBackPressed()
                val drawer = rootElement.drawerLayout//findViewById<DrawerLayout>(R.id.drawerLayout)

                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START)
                }else{
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
        }
    }//setupToolbar

    override fun onBackPressed() {
        val drawer =rootElement.drawerLayout //findViewById<DrawerLayout>(R.id.drawerLayout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }else {
            //super.onBackPressed()
            if (!searchView.isIconified()) {
                searchView.onActionViewCollapsed();
                //showFilterPanel(false,"")
                resetFilterPanel()
            } else {
                super.onBackPressed();
            }
        }
    }
}

var mainActivity: MainActivity? =null
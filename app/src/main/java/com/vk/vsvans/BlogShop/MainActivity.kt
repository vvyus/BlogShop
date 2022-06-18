package com.vk.vsvans.BlogShop

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
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
import com.vk.vsvans.BlogShop.interfaces.IFilterCallBack
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.BaseList
import com.vk.vsvans.BlogShop.model.DbManager
import com.vk.vsvans.BlogShop.model.Product
import com.vk.vsvans.BlogShop.model.Purchase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var rootElement: ActivityMainBinding
    val dbManager= DbManager(this)
    private val purchaseArray=ArrayList<Purchase>()
    private var job: Job? = null
    private lateinit var toolbar: Toolbar
    private lateinit var searchView:SearchView
    private var isSetFilter=false
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
        mainActivity=this
        rootElement= ActivityMainBinding.inflate(layoutInflater)
        val view=rootElement.root
        setContentView(view)

        init()

        initRecyclerView()
        bottomMenuOnClick()
        adapter.setFilterCallback(object: IFilterCallBack {
            override fun onTimeClick() {
                DialogHelper.getCalendarDialog(this@MainActivity)
            }
            override fun onSellerClick(purchase: Purchase) {
                this@MainActivity.onSellerClick(purchase)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        dbManager.openDb()
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun fillAdapter(text: String):String{
        var str=""
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch{
            val purchaseList = ArrayList<Purchase>()
            val amount = dbManager.queryPurchases(text,purchaseList)
            adapter.updateAdapter(purchaseList)
            if(isSetFilter) {
                str = "${purchaseList.size} покуп на сумму ${amount.toString().format("%12.2f")}"
            }
            showFilterPanel(str)
        }
        return str
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

    @RequiresApi(Build.VERSION_CODES.N)
    fun onSellerClick(purchase: Purchase) {
        var str=""
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch{
            if(isSetFilter) {
                fillAdapter(str)
                resetFilterPanel(str)
            }else{
                val purchaseList = ArrayList<Purchase>()
                val amount = dbManager.queryPurchases(purchase.idseller,purchaseList)
                adapter.updateAdapter(purchaseList)

                isSetFilter=true
                str = "${purchaseList.size} покуп на сумму ${amount.toString().format("%12.2f")}"
            }
            showFilterPanel(str)
        }
    }

    fun setFilterPanel(amount:String){
        isSetFilter=true
        showFilterPanel(amount)
    }

    fun resetFilterPanel(amount:String){
        isSetFilter=false
        showFilterPanel(amount)
    }

    private fun showFilterPanel(amount:String){
        if(isSetFilter) {
            rootElement.mainContent.llFilterdPanel.visibility=View.VISIBLE
        }
        else rootElement.mainContent.llFilterdPanel.visibility=View.GONE
        rootElement.mainContent.tvFilteredSumma.text=amount
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
                                fillAdapter("")
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
                        fillAdapter("")
                    }
                }
                }//when
            true
        }
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
                            if(newText.isEmpty())isSetFilter=false else isSetFilter=true
                            fillAdapter(newText)//call to showFilterPanel
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
                resetFilterPanel("")
            } else {
                super.onBackPressed();
            }
        }
    }
}

var mainActivity: MainActivity? =null
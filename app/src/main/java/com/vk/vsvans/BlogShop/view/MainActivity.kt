package com.vk.vsvans.BlogShop.view

import android.Manifest
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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.view.adapter.PurchaseRcAdapter
import com.vk.vsvans.BlogShop.databinding.ActivityMainBinding
import com.vk.vsvans.BlogShop.model.data.*
import com.vk.vsvans.BlogShop.model.fns.*
import com.vk.vsvans.BlogShop.view.dialog.DialogHelper
import com.vk.vsvans.BlogShop.view.`interface`.*
import com.vk.vsvans.BlogShop.util.FilterForActivity
import com.vk.vsvans.BlogShop.util.UtilsHelper
import com.vk.vsvans.BlogShop.util.UtilsString
import com.vk.vsvans.BlogShop.util.isPermissinGrant
import com.vk.vsvans.BlogShop.view.dialog.ProgressDialog
import com.vk.vsvans.BlogShop.view.fragment.BaseAmountFragment
import com.vk.vsvans.BlogShop.viewmodel.ActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.String.valueOf
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    //val dbManager= DbManager(this)
    //val viewModel:ActivityViewModel by viewModels()

    //val viewModel= ActivityViewModel(this)
    var viewModel:ActivityViewModel=ActivityViewModel()//?=null
    private val purchaseArray=ArrayList<Purchase>()
    private var job: Job? = null
    private lateinit var toolbar: Toolbar
    private lateinit var searchView:SearchView

    val filter_fact=FilterForActivity("main")
    // for count purchases
    val calendar_events=HashMap<String, Int>()

    //val launcher=getLauncher()
    lateinit var launcherGetPermission:ActivityResultLauncher<String>
    lateinit var launcherEPA:ActivityResultLauncher<Intent>

    var livePurchaseList_size=0
    var liveAmount=0.0
    var liveCalendarEvents=HashMap<String, Int>()
    var liveProductAmount=ArrayList<BaseAmount>()
    var liveSellerAmount=ArrayList<BaseAmount>()

    val number_demo_files=3
    var index_demo_files=0

    private var baseAmountFragment: BaseAmountFragment?=null

    val adapter= PurchaseRcAdapter(object:OnClickItemCallback{
        override fun onClickItem(id:Int) {
            if(id>0) {
//                val intent= Intent(this@MainActivity, EditPurchaseActivity::class.java)
//                intent.putExtra(R.string.PURCHASE_ID.toString(),id)
//                launcher?.launch(intent)
                launchEditPurchaseActivity(id)
            }
        }

        override fun onClickItem(baseList: BaseList) {
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
       // viewModel= ActivityViewModel(application)
        mainActivity=this
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
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
                            println("Dates size is :"+dates.size+" selected date size is "+selected_date.size)
                            filter_fact.dates_begin = ArrayList<String>()
                            filter_fact.dates_end = ArrayList<String>()
                            var str: String?
                            for (i in 0 until dates.size) {
                                println("To filter Date is :"+dates[i])
                                str = valueOf(UtilsHelper.correct_date_begin(dates[i]!!.time))
                                filter_fact.dates_begin!!.add(str)
                                str = valueOf(UtilsHelper.correct_date_end(dates[i]!!.time))
                                filter_fact.dates_end!!.add(str)
                            }

                            fillAdapter()
                        }
                    }

                    override fun cancelFilter() {
                        filter_fact.dates_begin=null
                        filter_fact.dates_end=null
                        //resetFilterPanel()
                        fillAdapter()
                    }
                },filter_fact,time)
            }
            override fun onSellerClick(purchase: Purchase) {
                this@MainActivity.onSellerClick(purchase)
            }
        })//adapter set call back

        initViewModel()

    }//on create

    private fun initViewModel(){
        //{} это слушатель
        //если наше activity доступно не разрушено или ждет когда можно обновить слушателт сработает
        viewModel.livePurchaseList.observe(this,{
            adapter.updateAdapter(it)
            livePurchaseList_size=it.size

        })

        viewModel.liveAmount.observe(this,{
            liveAmount=it
        })

        viewModel.liveCalendarEvents.observe(this, {
            if(!isSetFilter()) {
                calendar_events.clear()
                calendar_events.putAll(it)
            }
        })
// product amount
        viewModel.liveProductAmountList.observe(this,{
            liveProductAmount.clear()
            liveProductAmount.addAll(it)
        })
// seller amount
        viewModel.liveSellerAmountList.observe(this,{
            liveSellerAmount.clear()
            liveSellerAmount.addAll(it)
        })

    }

     @RequiresApi(Build.VERSION_CODES.N)
    fun onSellerClick(purchase: Purchase) {

         if( filter_fact.idSeller!=null ) {
             filter_fact.idSeller=null
         }else{
             filter_fact.idSeller=purchase.idseller
         }
         fillAdapter()
    }

    fun isSetFilter():Boolean{
        return !(filter_fact.idSeller==null && filter_fact.dates_begin==null && filter_fact.dates_end==null && filter_fact.content==null)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        //dbManager.openDb()
        viewModel.openDb()
        fillAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        //dbManager.closeDb()
        viewModel.closeDb()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun fillAdapter() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch{
            getPurchases()
//            viewModel.getPurchases(filter_fact)
//
//            if(isSetFilter()) {
//                setFilterPanel(liveAmount,livePurchaseList_size)
//            }
//            else resetFilterPanel()
        }
    }

    fun getPurchases(){
        viewModel.getPurchases(filter_fact)

        if(isSetFilter()) {
            setFilterPanel(liveAmount,livePurchaseList_size)
        }
        else resetFilterPanel()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun init(){
        binding.navView.setNavigationItemSelectedListener(this)

        toolbar=binding.mainContent.toolbar
        toolbar.title=""
        setSupportActionBar(toolbar)

        val toggle= ActionBarDrawerToggle(this,binding.drawerLayout,toolbar, R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.mainContent.imCloseFilter.setOnClickListener{
            resetFilter_For_Activity()
            fillAdapter()
        }
        launcherEPA=getLauncher()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_choose_purchase_item, menu)
        setUpToolbar()
        //return super.onCreateOptionsMenu(menu)
        return true
    }

    private fun initRecyclerView(){
        binding.apply {
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

    fun resetFilter_For_Activity() {
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
        }
        filter_fact.content=null
        filter_fact.idSeller=null
        filter_fact.dates_begin=null
        filter_fact.dates_end=null
    }

    private fun showFilterPanel(amount:Double,count: Int){
        val str_amount=((amount*100).roundToInt()/100.0).toString()
        //var str_amount=amount.toString().format(R.string.double_format)

        //str_amount= UtilsString.format_string(str_amount)

        val str_count=count.toString().format(R.string.int_format)
        binding.mainContent.tvFilteredSumma.text=str_amount
        binding.mainContent.tvFilteredCount.text=str_count
        binding.mainContent.cvFilterPanel.visibility=View.VISIBLE
    }

    private fun showFilterPanel(){
        binding.mainContent.tvFilteredSumma.text=""
        binding.mainContent.tvFilteredCount.text=""
        binding.mainContent.cvFilterPanel.visibility=View.GONE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.id_products->{
                //val intent= Intent(this@MainActivity, ProductActivity::class.java)
                val intent= Intent(this@MainActivity, BaseListActivity::class.java)
                intent.putExtra(R.string.BASE_LIST_TYPE.toString(),1) // ==1 is product
                intent.putExtra(R.string.PURCHASE_ID.toString(),0)
                intent.putExtra(R.string.BASE_LIST_EDIT_ITEM.toString(),true)
                // сообщаем системе о запуске активити
                startActivity(intent)
            }
            R.id.id_sellers->{
                //val intent= Intent(this@MainActivity, SellerActivity::class.java)
                val intent= Intent(this@MainActivity, BaseListActivity::class.java)
                intent.putExtra(R.string.BASE_LIST_TYPE.toString(),2) // ==2 is seller
                intent.putExtra(R.string.SELLER_ID.toString(),0)
                intent.putExtra(R.string.BASE_LIST_EDIT_ITEM.toString(),true)
                // сообщаем системе о запуске активити
                startActivity(intent)
                //Toast.makeText(this@MainActivity,"Pressed sellers", Toast.LENGTH_LONG).show()

            }
            R.id.id_demo_retrofit->{
                //demoList.clear()
                DialogHelper.getConfirmActionDialog(this,object :IConfirmAction{
                    override fun onConfirm() {
                        Toast.makeText(this@MainActivity,R.string.demo_load_data,Toast.LENGTH_LONG).show()
                        val prefix_demo_files=getString(R.string.prefix_demo_files)
                        var file_name_json=""
                        for(i in 0 until number_demo_files){
                            file_name_json="${prefix_demo_files}${i+1}.json"
                            getDemoJsonByName(file_name_json)
                        }
//                        getDemoJsonByName("demo1.json","") //ошибка получения данных из за 52/1 в retailAddress
//                        getDemoJsonByName("demo2.json","")
//                        getDemoJsonByName("demo3.json",getString(R.string.demo_is_loaded))
                    }

                },getString(R.string.demo_base))
                //Toast.makeText(this,R.string.demo_is_loaded,Toast.LENGTH_LONG).show()
            } else-> binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        return true
    }

    private fun getDemoJsonByName(file_name_json:String){
        val service= RetrofitCommon.retrofitService
        //listOf("6294837fcfb1f1a16860eda9.json","629483daffd38dd204df6c19.json")
        service.getChecksModelItem(file_name_json)?.enqueue(object : Callback<MutableList<ChecksModelItem>> {
            override fun onFailure(call: Call<MutableList<ChecksModelItem>>, t: Throwable) {
                Toast.makeText(this@MainActivity,getString(R.string.demo_fail),Toast.LENGTH_LONG).show()
                println("Retrofit fail!!! "+t)
            }

            override fun onResponse(call: Call<MutableList<ChecksModelItem>>, response: Response<MutableList<ChecksModelItem>>) {
                if (response.isSuccessful()) {
                    val separator=resources.getString(R.string.SEPARATOR)
                    val title_color=getColor(R.color.light_gray_text)

                    val arrayOfChecksModelItem=response.body() as ArrayList<*>
                    var receipt: Receipt?=null
                    for(i in 0 until arrayOfChecksModelItem.size){
                        receipt=(arrayOfChecksModelItem[i] as ChecksModelItem).ticket.document.receipt
                        import_checks.receiptToDb(receipt,viewModel, separator,title_color,100)
                        //demoList.add(receipt)
                        //if(!message.isEmpty()){
                        getPurchases()
                        if(index_demo_files==number_demo_files-1) {
                            index_demo_files=0
                            Toast.makeText(this@MainActivity,getString(R.string.demo_is_loaded),Toast.LENGTH_LONG).show()
                        }else index_demo_files++
                    }
                    //}
                    //}
                }
            }
        })
    } // fun get demo json

    @RequiresApi(Build.VERSION_CODES.N)
    private fun bottomMenuOnClick()=with(binding){
        mainContent.bNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.id_new_purchase->{
                    //addPurchase()
                    launchEditPurchaseActivity(0)
                }
                R.id.id_delete_purchase->{
                    deletePurchase()
                }
                R.id.id_product->{

                    // это вызов ответ придет в liveProductAmount
                    val time=UtilsHelper.getCurrentDate()
                    viewModel.getProductAmount("",time)
                    mainContent.llMainContent.visibility=View.GONE
                    baseAmountFragment=BaseAmountFragment(object: IFragmentCloseInterface {
                        override fun onFragClose(list: ArrayList<PurchaseItem>) {
                        }

                        // при закрытии фрагмента
                        override fun onFragClose() {
                            mainContent.llMainContent.visibility=View.VISIBLE
                            fillAdapter()
                        }

                    },liveProductAmount,filter_fact,BaseAmountType.PRODUCT)

                    val fm=supportFragmentManager.beginTransaction()
                    fm.replace(R.id.drawerLayout, baseAmountFragment!!)
                    fm.commit()
                }
                R.id.id_seller->{
                    val time=UtilsHelper.getCurrentDate()
                    viewModel.getSellerAmount("",time)
                    mainContent.llMainContent.visibility=View.GONE
                    baseAmountFragment=BaseAmountFragment(object: IFragmentCloseInterface {
                        override fun onFragClose(list: ArrayList<PurchaseItem>) {
                        }

                        // при закрытии фрагмента
                        override fun onFragClose() {
                            mainContent.llMainContent.visibility=View.VISIBLE
                            fillAdapter()
                        }

                    },liveSellerAmount,filter_fact,BaseAmountType.SELLER)

                    val fm=supportFragmentManager.beginTransaction()
                    fm.replace(R.id.drawerLayout, baseAmountFragment!!)
                    fm.commit()

                }
                R.id.id_import_checks->{
                    importPurchase()
//                    job?.cancel()
//                    job = CoroutineScope(Dispatchers.Main).launch{
//                        import_checks.doImport(this@MainActivity)
//                        fillAdapter()
//                    }
                }
                }//when
            true
        }
    }

    // instead addPurchase activity result api
    fun launchEditPurchaseActivity(id:Int){
        val intent= Intent(this@MainActivity, EditPurchaseActivity::class.java)
        intent.putExtra(R.string.PURCHASE_ID.toString(),id)
        launcherEPA.launch(intent) //getLauncher().launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLauncher():ActivityResultLauncher<Intent>{
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result:ActivityResult->
            if(result.resultCode==AppCompatActivity.RESULT_OK){
                if(result.data!=null){
                    //result.data is intent
                    val intent=result.data
                    val new_purchase_time=intent!!.getLongExtra(getString(R.string.new_purchase_time),0L)
                    val old_purchase_time=
                        intent.getLongExtra(getString(R.string.old_purchase_time),0L)
                    val idpurchase= intent.getIntExtra(getString(R.string.PURCHASE_ID),0)
                    //
                    if(old_purchase_time!=new_purchase_time) removePurchaseEvent(old_purchase_time)
                    addPurchaseEvent(new_purchase_time)
                    val purchase= intent.getSerializableExtra(Purchase::class.java.getSimpleName()) as Purchase
                    // если новая запись
                    if(idpurchase==0) adapter.addPurchase(purchase)
                    else adapter.setPurchase(purchase)
                    //fillAdapter()
                }
            }
        }
    }

    fun addPurchaseEvent(time:Long){
        val event_key = UtilsHelper.getDate(time)
        var event_int = calendar_events[event_key]
        if (event_int == null) {
            calendar_events[event_key] = 1
        } else calendar_events[event_key] = ++event_int
    }

    private fun importPurchase(){
        DialogHelper.showLoadChecksDialog(this@MainActivity,object: IDialogImportChecks{

            override fun import_checks() {}

//            @RequiresApi(Build.VERSION_CODES.N)
            override fun import_checks(selected_date: HashMap<String, Date?>) {
                val dialog = ProgressDialog.createProgressDialog(this@MainActivity )
                job?.cancel()
                job = CoroutineScope(Dispatchers.Main).launch{
                    //val dialog = ProgressDialog.createProgressDialog(this@MainActivity )
                    val separator=resources.getString(R.string.SEPARATOR)
                    val title_color=getColor(R.color.light_gray_text)
                    import_checks.getReceipt(selected_date,viewModel,separator,title_color,1)
                    fillAdapter()
                    dialog.dismiss()

                }

            }

        })
    }

    private fun deletePurchase(){
        //val purchase=adapter.getPurchase()
//        if(purchase!=null && purchase.id>0 || adapter.getMarkedPosition().size>0) {
        if(  adapter.getMarkedIds().size>0 ) {
            //dbManager.removePurchaseItemFromDb(id)
            DialogHelper.showPurchaseDeleteItemDialog(this@MainActivity,adapter.getMarkedIds(),object:
                IDeleteItem {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onDeleteItem(ids:ArrayList<Int>) {
                    val purchases=adapter.getMarkedObjects()
                    for(i in 0 until ids.size){
                        //1 remove purchase from events
                        removePurchaseEvent(purchases[i].time)
                        //2 remove purchase from database
                        viewModel.removePurchase(ids[i])
                    }
                    adapter.resetMarkedPosition()
                    fillAdapter()
                }

            })
        }
    }

    fun removePurchaseEvent(time: Long){
        val event_key = UtilsHelper.getDate(time)
        var event_int = calendar_events[event_key]
        if (event_int != null) {
            calendar_events[event_key] = --event_int
        }
    }

    private fun permissionListener(){
        launcherGetPermission=registerForActivityResult( ActivityResultContracts.RequestPermission() ) {
        }
    }

    private fun checkPermission(){
        permissionListener()
        if(!isPermissinGrant(Manifest.permission.READ_EXTERNAL_STORAGE))
            launcherGetPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun setUpToolbar() {
        binding.apply {

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
                val drawer = binding.drawerLayout//findViewById<DrawerLayout>(R.id.drawerLayout)

                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START)
                }else{
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
        }
    }//setupToolbar

    override fun onBackPressed() {
        //remove all fragments
        val frags=ArrayList<Fragment>(getSupportFragmentManager().getFragments())
        for (frag in frags ){
            getSupportFragmentManager().beginTransaction().remove(frag).commit()
            return;
        }
//        if(baseAmountFragment!=null) {
//            supportFragmentManager?.beginTransaction()?.remove(baseAmountFragment!!)?.commit()
//            return
//        }
        val drawer =binding.drawerLayout //findViewById<DrawerLayout>(R.id.drawerLayout)
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
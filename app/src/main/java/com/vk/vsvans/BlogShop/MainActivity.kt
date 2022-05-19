package com.vk.vsvans.BlogShop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.vk.vsvans.BlogShop.activity.EditPurchaseActivity
import com.vk.vsvans.BlogShop.adapters.PurchaseRcAdapter
import com.vk.vsvans.BlogShop.databinding.ActivityMainBinding
import com.vk.vsvans.BlogShop.dialogs.DialogHelper
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.DbManager
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

    val adapter= PurchaseRcAdapter(object:OnClickItemCallback{
        override fun onClickItem(id:Int) {
            if(id>0) {
                val intent = Intent(this@MainActivity, EditPurchaseActivity::class.java)
                intent.putExtra(R.string.PURCHASE_ID.toString(), id)
                // сообщаем системе о запуске активити
                startActivity(intent)
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity=this
        rootElement= ActivityMainBinding.inflate(layoutInflater)
        val view=rootElement.root
        setContentView(view)
        init()
        initRecyclerView()
        bottomMenuOnClick()
    }

    override fun onResume() {
        super.onResume()
        dbManager.openDb()
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }

    fun fillAdapter(text: String){

        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch{
            val list = dbManager.readPurchases(text)
            adapter.updateAdapter(list)
        }

    }
    private fun init(){
        // строка с подключенным toolbar(какой тоолбар исп в активити) должна стоять выше всех
        setSupportActionBar(rootElement.mainContent.toolbar)
        val toggle= ActionBarDrawerToggle(this,rootElement.drawerLayout,rootElement.mainContent.toolbar,R.string.open,R.string.close)
        rootElement.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        rootElement.navView.setNavigationItemSelectedListener(this)
        //header с нулевой позиции
       // tvAccount=rootElement.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }

    private fun initRecyclerView(){
        rootElement.apply {
            //если просто указатб this это будет ссылка на rootElement поэтому this@MainActivity
            mainContent.rcView.layoutManager= LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter=adapter
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.id_purchase_food->{
                Toast.makeText(this,"Pressed food", Toast.LENGTH_LONG).show()
            }
            R.id.id_purchase_drug->{
                Toast.makeText(this,"Pressed drug", Toast.LENGTH_LONG).show()
            }
        }
        rootElement.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

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
                R.id.id_edit_purchase->{
                    val id=adapter.getPurchaseId()
                    if(id>0) {
                        val i = Intent(this@MainActivity, EditPurchaseActivity::class.java)
                        i.putExtra(R.string.PURCHASE_ID.toString(), id)
                        // сообщаем системе о запуске активити
                        startActivity(i)
                    }
                }
                R.id.id_delete_purchase->{
                    val id=adapter.getPurchaseId()
                    if(id>0) {
                        //dbManager.removePurchaseItemFromDb(id)
                        DialogHelper.showPurchaseDeleteItemDialog(dbManager,id)
                    }
                }
                }//when
            true
        }
    }
}

var mainActivity: MainActivity? =null
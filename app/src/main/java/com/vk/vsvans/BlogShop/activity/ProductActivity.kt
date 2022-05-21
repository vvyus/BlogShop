package com.vk.vsvans.BlogShop.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.adapters.ProductRcAdapter
import com.vk.vsvans.BlogShop.adapters.PurchaseItemRcAdapter
import com.vk.vsvans.BlogShop.databinding.ActivityMainBinding
import com.vk.vsvans.BlogShop.databinding.ActivityProductBinding
import com.vk.vsvans.BlogShop.dialogs.DialogHelper
import com.vk.vsvans.BlogShop.interfaces.IDeleteItem
import com.vk.vsvans.BlogShop.interfaces.IUpdateProductItemList
import com.vk.vsvans.BlogShop.interfaces.IUpdatePurchaseItemList
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.DbManager
import com.vk.vsvans.BlogShop.model.Product
import com.vk.vsvans.BlogShop.model.PurchaseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProductActivity : AppCompatActivity() {

    private lateinit var rootElement: ActivityProductBinding
    lateinit var adapter: ProductRcAdapter
    private lateinit var tb: Toolbar
    val dbManager= DbManager(this)
    private var job: Job? = null
    private var selectedId=0
    private lateinit var searchView:SearchView
    val TAG="MyLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement= ActivityProductBinding.inflate(layoutInflater)
        val view=rootElement.root
        setContentView(view)
        //setContentView(R.layout.activity_product)
        //устанавливаем тоолбар
        tb=rootElement.tb
        setUpToolbar()

        adapter= ProductRcAdapter(object: OnClickItemCallback {
            override fun onClickItem(id:Int) {
                if(id>0) {
                    // may be delete item
                    selectedId=id
                }
            }
        })
        val rcView=rootElement.rcViewProductList
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        dbManager.openDb()
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
        job?.cancel()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
        } else {
            super.onBackPressed();
        }
    }

    fun fillAdapter(text: String){

        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch{
            val list = dbManager.readProducts(text)
            adapter.updateAdapter(list)
        }

    }

    private fun setUpToolbar() {
        rootElement.apply {

            tb.inflateMenu(R.menu.menu_choose_product_item)
            val deleteProductItem =tb.menu.findItem(R.id.id_delete_item_product)
            val editProductItem = tb.menu.findItem(R.id.id_edit_item_product)
            val addProductItem = tb.menu.findItem(R.id.id_add_item_product)

            val searchItem: MenuItem =tb.menu.findItem(R.id.action_search)
            if (searchItem != null) {
                searchView = MenuItemCompat.getActionView(searchItem) as SearchView
                searchView.setOnCloseListener(object : SearchView.OnCloseListener {
                    override fun onClose(): Boolean {
                        return true
                    }
                })

                val searchPlate =        searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
                searchPlate.hint = "Search"
                val searchPlateView: View =
                    searchView.findViewById(androidx.appcompat.R.id.search_plate)
                searchPlateView.setBackgroundColor(
                    ContextCompat.getColor(
                        this@ProductActivity,
                        android.R.color.transparent
                    )
                )

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
// do your logic here                Toast.makeText(applicationContext, query, Toast.LENGTH_SHORT).show()
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText != null) {
                            fillAdapter(newText)
                        }
                        return true
                    }
                })

                val searchManager =
                    getSystemService(Context.SEARCH_SERVICE) as SearchManager
                searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            }//search_item

            addProductItem?.setOnMenuItemClickListener {
                val product= Product()
                //новая запись
                product.id=0
                DialogHelper.showProductInputDialog(this@ProductActivity,product,
                    object: IUpdateProductItemList {
                        override fun onUpdateProductItemList(product: Product) {
                            // add single product
                            adapter.updateAdapter(product)
                            dbManager.insertProduct(product)
                        }

                    }
                )
                true
            }

            deleteProductItem.setOnMenuItemClickListener {
                if(selectedId>0){
                    DialogHelper.showPurchaseDeleteItemDialog(this@ProductActivity,selectedId,
                        object: IDeleteItem {
                            override fun onDeleteItem(id: Int) {
                                adapter.deleteProductItem()
                                //reset selectedId
                                //selectedId=0
                                dbManager.removeProduct(id)
                            }

                        })
                }else {
                    Toast.makeText(this@ProductActivity,R.string.no_selected_item, Toast.LENGTH_LONG).show()
                }
                true
            }

            tb.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }
}
package com.vk.vsvans.BlogShop.activity

import android.app.SearchManager
import android.content.Context
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
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.adapters.ProductRcAdapter
import com.vk.vsvans.BlogShop.databinding.ActivityProductBinding
import com.vk.vsvans.BlogShop.dialogs.DialogHelper
import com.vk.vsvans.BlogShop.interfaces.IDeleteItem
import com.vk.vsvans.BlogShop.interfaces.IDialogListener
import com.vk.vsvans.BlogShop.interfaces.IUpdateProductItemList
import com.vk.vsvans.BlogShop.interfaces.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.DbManager
import com.vk.vsvans.BlogShop.model.Product
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

            override fun onEditItem() {
                val product= adapter.getProduct()
                if (product != null) {
                    DialogHelper.showProductInputDialog(this@ProductActivity,product,
                        object: IUpdateProductItemList {
                            override fun onUpdateProductItemList(product: Product) {
                                // add single product
                                adapter.updateAdapterEdit(product)
                                dbManager.updateProduct(product)
                            }

                        }
                    )
                }
            }

            override fun onDeleteItem() {
                if(selectedId>0){
                    DialogHelper.showPurchaseDeleteItemDialog(this@ProductActivity,selectedId,
                        object: IDeleteItem {
                            override fun onDeleteItem(id:Int) {
                                adapter.deleteProductItem()
                                val parent=adapter.getParent()
                                if(parent!=null) dbManager.updateProduct(parent)
                                //reset selectedId
                                //selectedId=0
                                dbManager.removeProduct(id)
                            }

                        })
                }else {
                    Toast.makeText(this@ProductActivity,R.string.no_selected_item, Toast.LENGTH_LONG).show()
                }
            }

            override fun onNewItem(parent:Product) {
                //adapter.addProductItem(product)
                // add single product with parent
                val product= Product()
                //новая запись
                product.id=0
                product.idparent=parent.id
                product.level=parent.level+1
                parent.count=parent.count+1
                DialogHelper.showProductInputDialog(this@ProductActivity,product,
                    object: IUpdateProductItemList {
                        override fun onUpdateProductItemList(product: Product) {
                            val id=dbManager.insertProduct(product)
                            if (id != null) {
                                product.id=id
                                product.fullpath=parent.fullpath+id.toString()
                                // update product array in adapter
                                adapter.updateAdapterInsert(product)
                                // update count for parent
                                dbManager.updateProduct(parent)
                                // update fullpath for product
                                dbManager.updateProduct(product)
                            }
                        }

                    }
                )
            }

            override fun refreshItem() {
                adapter.refreshItem()
            }

            override fun onParentItem() {
              DialogHelper.showSelectParentProductDialog("Выберите родителя",this@ProductActivity,
                  object: IDialogListener {
                      //override fun onOkClick(v: View?) {}
                      override fun onOkClick(idParent: Int?) {

                          var parent=if(idParent==null) null else adapter.nodeList.get(idParent)

                          val product= adapter.getProduct()

                          val oldparent=adapter.getParent()
                          if (product != null ) {
                              // select root
                              if(parent==null){
                                  product.idparent=product.id
                                  product.fullpath=product.id.toString()
                                  product.level=0
                              }else {
                                  //select normal node get from parent
                                  product.idparent = parent.id
                                  product.fullpath = parent.fullpath + product.id
                                  product.level = parent.level + 1
                                  // set count for new parent
                                  parent.count=parent.count+1
                                  dbManager.updateProduct(parent)
                              }
                              // replace all children where idparent==product.idparent
                              if(product.count>0){
                                  adapter.childArray.clear()
                                  adapter.setChildren(product.id,product.fullpath,product.level)
                                  for(i in 0 until adapter.childArray.size)
                                      dbManager.updateProduct(adapter.childArray[i])
                                  adapter.childArray.clear()
                              }
                              if(oldparent!=null){
                                  oldparent.count=oldparent.count-1
                                  dbManager.updateProduct(oldparent)
                              }
                              // update count for parent
                              adapter.updateAdapterParent(oldparent,parent,product)
                              dbManager.updateProduct(product)
                              //fillAdapter("")
                              //adapter.notifyDataSetChanged()
                          }
                      }
                  })
            }

            override fun onTimeClick() {}

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
// add root element
    fun onClickAddProduct(view: View){
        val product= Product()
        //новая запись
        product.id=0
        DialogHelper.showProductInputDialog(this@ProductActivity,product,
            object: IUpdateProductItemList {
                override fun onUpdateProductItemList(product: Product) {
                    // add single product
                    val id=dbManager.insertProduct(product)
                    if (id != null) {
                        product.id=id
                        product.idparent=id
                        product.fullpath=id.toString()
                        dbManager.updateProduct(product)
                        adapter.updateAdapterInsert(product)
                    }
                }

            }
        )
    }

    private fun setUpToolbar() {
        rootElement.apply {

            tb.inflateMenu(R.menu.menu_choose_product_item)

            val searchItem: MenuItem =tb.menu.findItem(R.id.action_search)
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
                        this@ProductActivity,
                        android.R.color.transparent
                    )
                )

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
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

            // GO BACK
            tb.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }//setupToolbar

}
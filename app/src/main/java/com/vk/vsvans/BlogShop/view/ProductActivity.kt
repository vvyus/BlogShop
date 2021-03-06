package com.vk.vsvans.BlogShop.view

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
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.view.adapter.BaseListRcAdapter
import com.vk.vsvans.BlogShop.databinding.ActivityProductBinding
import com.vk.vsvans.BlogShop.view.dialog.DialogHelper
import com.vk.vsvans.BlogShop.view.`interface`.IDeleteItem
import com.vk.vsvans.BlogShop.view.`interface`.IDialogListener
import com.vk.vsvans.BlogShop.view.`interface`.IUpdateBaseListItemList
import com.vk.vsvans.BlogShop.view.`interface`.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.data.BaseList
import com.vk.vsvans.BlogShop.model.data.Product
import com.vk.vsvans.BlogShop.model.data.Seller
import com.vk.vsvans.BlogShop.viewmodel.ActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProductActivity : AppCompatActivity() {

    private lateinit var rootElement: ActivityProductBinding
    lateinit var adapter: BaseListRcAdapter
    private lateinit var tb: Toolbar
    var viewModel: ActivityViewModel=ActivityViewModel()//?=null
    //val dbManager= DbManager(this)
    private var job: Job? = null
    private var selectedId=0
    private var startSelectedId=0
    private var expandedList=false
    private lateinit var searchView:SearchView
    val TAG="MyLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent=getIntent()
        startSelectedId=intent.getIntExtra(R.string.PRODUCT_ID.toString(), 0)

        rootElement= ActivityProductBinding.inflate(layoutInflater)
        val view=rootElement.root
        setContentView(view)
        //setContentView(R.layout.activity_product)
        //?????????????????????????? ??????????????
        tb=rootElement.tb
        setUpToolbar()

        adapter= BaseListRcAdapter(object: OnClickItemCallback {
            override fun onClickItem(id:Int) {
                if(id>0) {
                    // may be delete item
                    selectedId=id
                }
            }

            override fun onClickItem(baseList: BaseList) {
                val data = Intent()
                 data.putExtra(getString(R.string.PRODUCT_ID), baseList.id)
                //Seller seller=adapter.
                data.putExtra(Product::class.java.getSimpleName(), baseList as Product)
                setResult(RESULT_OK, data)
                onBackPressed()
            }

            override fun onEditItem() {}
            override fun onDeleteItem() {}
            override fun onNewItem(parent: BaseList) {}
            override fun refreshItem() { adapter.refreshItem() }
            override fun onParentItem() {}
        })

        val rcView=rootElement.rcViewProductList
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = adapter

        initViewModel()
        bottomMenuOnClick()

    }// onCreate

    private fun initViewModel(){
        //{} ?????? ??????????????????
        //???????? ???????? activity ???????????????? ???? ?????????????????? ?????? ???????? ?????????? ?????????? ???????????????? ?????????????????? ??????????????????
        viewModel.liveProductList.observe(this,{
            adapter.updateAdapter(it)
            if(startSelectedId>0){
                val pos=adapter.setSelectedPositionById(startSelectedId)
                if(pos>0) {
                    selectedId=startSelectedId
                    rootElement.rcViewProductList.scrollToPosition(pos)
                }
                startSelectedId=0;
            }
        })
    }

    override fun onResume() {
        super.onResume()
            // viewModel.openDb()
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        //viewModel.closeDb()
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
//            val list = viewModel!!.getProducts(text)
//            adapter.updateAdapter(list)
            viewModel.getProducts(text)
        }

    }

// add root element
    fun onClickAddProduct(view: View){
        val product= Product()
        //?????????? ????????????
        product.id=0
        DialogHelper.showBaseListInputDialog(this@ProductActivity,product,
            object: IUpdateBaseListItemList {
                override fun onUpdateBaseListItemList(product: BaseList) {
                    // add single product
                    val id=viewModel.insertProduct(product as Product)
                    if (id != null) {
                        product.id=id
                        product.idparent=id
                        product.fullpath=id.toString()
                        viewModel.updateProduct(product as Product)
                        adapter.updateAdapterInsert(product)
                    }
                }

            }
        )
    }

    private fun setUpToolbar() {
        rootElement.apply {

            tb.inflateMenu(R.menu.menu_choose_baselist_item)

            val searchItem: MenuItem =tb.menu.findItem(R.id.action_search)
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
            //search_item

            // GO BACK
            tb.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }//setupToolbar

    private fun bottomMenuOnClick()=with(rootElement){
        bNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.id_new_baselist->{
                    val parent=adapter.getBaseList()
                    if(parent!=null) {
                        val product= Product()
                        //?????????? ????????????
                        product.id=0
                        product.idparent=parent.id
                        product.level=parent.level+1
                        parent.count=parent.count+1
                        DialogHelper.showBaseListInputDialog(this@ProductActivity,product,
                            object: IUpdateBaseListItemList {
                                override fun onUpdateBaseListItemList(product: BaseList) {
                                    val id=viewModel.insertProduct(product as Product)
                                    if (id != null) {
                                        product.id=id
                                        product.fullpath=parent.fullpath+id.toString()
                                        // update product array in adapter
                                        adapter.updateAdapterInsert(product)
                                        // update count for parent
                                        viewModel.updateProduct(parent as Product)
                                        // update fullpath for product
                                        viewModel.updateProduct(product)
                                    }
                                }

                            }
                        )
                    }
                }
                R.id.id_delete_baselist->{
                    if(selectedId>0){
                        DialogHelper.showPurchaseDeleteItemDialog(this@ProductActivity,selectedId,
                            object: IDeleteItem {
                                override fun onDeleteItem(id:Int) {
                                    adapter.deleteBaseListItem()
                                    val parent=adapter.getParent()
                                    if(parent!=null) viewModel.updateProduct(parent as Product)
                                    //reset selectedId
                                    //selectedId=0
                                    viewModel.removeProduct(id)
                                }

                            })
                    }else {
                        Toast.makeText(this@ProductActivity,R.string.no_selected_item, Toast.LENGTH_LONG).show()
                    }

                }

                R.id.id_edit_baselist->{
                    val product= adapter.getBaseList()
                    if (product != null) {
                        DialogHelper.showBaseListInputDialog(this@ProductActivity,product,
                            object: IUpdateBaseListItemList {
                                override fun onUpdateBaseListItemList(product: BaseList) {
                                    // add single product
                                    adapter.updateAdapterEdit(product)
                                    viewModel.updateProduct(product as Product)
                                }

                            }
                        )
                    }

                }
                R.id.id_parent_baselist->{
                    if(selectedId>0){
                    DialogHelper.showSelectParentBaseListDialog("???????????????? ????????????????",this@ProductActivity,
                        object: IDialogListener {
                            //override fun onOkClick(v: View?) {}
                            override fun onOkClick(idParent: Int?) {

                                val parent=if(idParent==null) null else adapter.nodeList.get(idParent)

                                val product= adapter.getBaseList()

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
                                        viewModel.updateProduct(parent as Product)
                                    }
                                    // replace all children where idparent==product.idparent
                                    if(product.count>0){
                                        adapter.childArray.clear()
                                        adapter.setChildren(product.id,product.fullpath,product.level)
                                        for(i in 0 until adapter.childArray.size)
                                            viewModel.updateProduct(adapter.childArray[i] as Product)
                                        adapter.childArray.clear()
                                    }
                                    if(oldparent!=null){
                                        oldparent.count=oldparent.count-1
                                        viewModel.updateProduct(oldparent as Product)
                                    }
                                    // update count for parent
                                    adapter.updateAdapterParent(oldparent,parent,product)
                                    viewModel.updateProduct(product as Product)
                                    //fillAdapter("")
                                    //adapter.notifyDataSetChanged()
                                }
                            }
                        })
                    }else {//if selected
                        Toast.makeText(this@ProductActivity,R.string.no_selected_item, Toast.LENGTH_LONG).show()
                    }
                }
                R.id.id_expand->{
                    expandedList=!expandedList
                    adapter.expandAll(expandedList)
                    if(expandedList)it.setIcon(R.drawable.ic_expand_less)
                    else it.setIcon(R.drawable.ic_expand_more)
                    adapter.notifyDataSetChanged()
                }
            }//when
            true
        }
    } // bottom menu

}
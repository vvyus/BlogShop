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
import com.vk.vsvans.BlogShop.databinding.ActivityBaseListBinding
import com.vk.vsvans.BlogShop.view.adapter.BaseListRcAdapter
import com.vk.vsvans.BlogShop.model.data.BaseAmountType
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

class BaseListActivity : AppCompatActivity() {

    private lateinit var rootElement: ActivityBaseListBinding
    lateinit var adapter: BaseListRcAdapter
    private lateinit var tb: Toolbar
    var viewModel: ActivityViewModel=ActivityViewModel()//?=null
    //val dbManager= DbManager(this)
    private var job: Job? = null
    private var selectedId=0
    private var startSelectedId=0
    private var expandedList=false
    private lateinit var searchView:SearchView
    private var baseListType: BaseAmountType=BaseAmountType.PRODUCT
    private var basetype=0
    var edit_item_by_click=false
    val TAG="MyLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent=getIntent()
        basetype=intent.getIntExtra(R.string.BASE_LIST_TYPE.toString(), 0)

        edit_item_by_click=intent.getBooleanExtra(R.string.BASE_LIST_EDIT_ITEM.toString(), false)

        if(basetype==1)baseListType=BaseAmountType.PRODUCT
        else if(basetype==2)baseListType=BaseAmountType.SELLER

        if(baseListType==BaseAmountType.PRODUCT)
            startSelectedId=intent.getIntExtra(R.string.PRODUCT_ID.toString(), 0)
        else if(baseListType==BaseAmountType.SELLER)
            startSelectedId=intent.getIntExtra(R.string.SELLER_ID.toString(), 0)

        rootElement= ActivityBaseListBinding.inflate(layoutInflater)
        val view=rootElement.root
        setContentView(view)
        //setContentView(R.layout.activity_product)
        //устанавливаем тоолбар
        tb=rootElement.tbBaseList
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
                if(baseListType==BaseAmountType.PRODUCT && !edit_item_by_click){
                    data.putExtra(getString(R.string.PRODUCT_ID), baseList.id)
                    data.putExtra(Product::class.java.getSimpleName(), baseList as Product)
                }else if(baseListType==BaseAmountType.SELLER && !edit_item_by_click){
                    data.putExtra(getString(R.string.SELLER_ID), baseList.id)
                    data.putExtra(Seller::class.java.getSimpleName(), baseList as Seller)
                }
                if((baseListType==BaseAmountType.PRODUCT || baseListType==BaseAmountType.SELLER) && edit_item_by_click){
                    editBaseListItem()
                }else{
                    setResult(RESULT_OK, data)
                    onBackPressed()
                }
            }

            override fun onEditItem() {}
            override fun onDeleteItem() {}
            override fun onNewItem(parent: BaseList) {}
            override fun refreshItem() { adapter.refreshItem() }
            override fun onParentItem() {}
        })

        val rcView=rootElement.rcViewBaseList
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = adapter

        initViewModel()
        bottomMenuOnClick()

    }// onCreate

    private fun initViewModel(){
        //{} это слушатель
        //если наше activity доступно не разрушено или ждет когда можно обновить слушателт сработает
        if(baseListType==BaseAmountType.PRODUCT){
            viewModel.liveProductList.observe(this,{
                adapter.updateAdapter(it)
                if(startSelectedId>0){
                    val pos=adapter.setSelectedPositionById(startSelectedId)
                    if(pos>0) {
                        selectedId=startSelectedId
                        rootElement.rcViewBaseList.scrollToPosition(pos)
                    }
                    startSelectedId=0;
                }
            })
        }else if(baseListType==BaseAmountType.SELLER){
            viewModel.liveSellerList.observe(this,{
                adapter.updateAdapter(it)
                if(startSelectedId>0){
                    val pos=adapter.setSelectedPositionById(startSelectedId)
                    if(pos>0) {
                        selectedId=startSelectedId
                        rootElement.rcViewBaseList.scrollToPosition(pos)
                    }
                    startSelectedId=0;
                }
            })

        }
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
            if(baseListType==BaseAmountType.PRODUCT){
                viewModel.getProducts(text)
            } else if(baseListType==BaseAmountType.SELLER){
                viewModel.getSellers(text)
            }
        }
    }

    // add root element
    fun onClickAddProduct(view: View){
        var baselist= BaseList()
        if(baseListType==BaseAmountType.PRODUCT) baselist=Product()
        else if(baseListType==BaseAmountType.SELLER) baselist=Seller()
        //новая запись
        baselist.id=0
        DialogHelper.showBaseListInputDialog(this@BaseListActivity,baselist,
            object: IUpdateBaseListItemList {
                override fun onUpdateBaseListItemList(baselist: BaseList) {
                    // add single product
                    if(baseListType==BaseAmountType.PRODUCT){
                        val id=viewModel.insertProduct(baselist as Product)
                        if (id != null) {
                            baselist.id=id
                            baselist.idparent=id
                            baselist.fullpath=id.toString()
                            viewModel.updateProduct(baselist as Product)
                            adapter.updateAdapterInsert(baselist)
                        }
                    }else if(baseListType==BaseAmountType.SELLER){
                        val id=viewModel.insertSeller(baselist as Seller)
                        if (id != null) {
                            baselist.id=id
                            baselist.idparent=id
                            baselist.fullpath=id.toString()
                            viewModel.updateSeller(baselist as Seller)
                            adapter.updateAdapterInsert(baselist)
                        }
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
                    this@BaseListActivity,
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

    private fun editBaseListItem(){
        val baselist= adapter.getBaseList()
        if (baselist != null) {
            DialogHelper.showBaseListInputDialog(this@BaseListActivity,baselist,
                object: IUpdateBaseListItemList {
                    override fun onUpdateBaseListItemList(baselist: BaseList) {
                        // add single product
                        adapter.updateAdapterEdit(baselist)
                        if(baseListType==BaseAmountType.PRODUCT)
                            viewModel.updateProduct(baselist as Product)
                        else if(baseListType==BaseAmountType.SELLER)
                            viewModel.updateSeller(baselist as Seller)
                    }

                }
            )
        }
    } //editBaseListItem

    private fun bottomMenuOnClick()=with(rootElement){
        bNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.id_new_baselist->{
                    val parent=adapter.getBaseList()
                    if(parent!=null) {
                        if(baseListType==BaseAmountType.PRODUCT){
                            val product= Product()
                            //новая запись
                            product.id=0
                            product.idparent=parent.id
                            product.level=parent.level+1
                            parent.count=parent.count+1
                            DialogHelper.showBaseListInputDialog(this@BaseListActivity,product,
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
                        }else if(baseListType==BaseAmountType.SELLER){
                            val seller = Seller()
                            //новая запись
                            seller.id = 0
                            seller.idparent = parent.id
                            seller.level = parent.level + 1
                            parent.count = parent.count + 1
                            DialogHelper.showBaseListInputDialog(this@BaseListActivity, seller,
                                object : IUpdateBaseListItemList {
                                    override fun onUpdateBaseListItemList(seller: BaseList) {
                                        val id = viewModel.insertSeller(seller as Seller)
                                        if (id != null) {
                                            seller.id = id
                                            seller.fullpath = parent.fullpath + id.toString()
                                            // update seller array in adapter
                                            adapter.updateAdapterInsert(seller)
                                            // update count for parent
                                            viewModel.updateSeller(parent as Seller)
                                            // update fullpath for seller
                                            viewModel.updateSeller(seller as Seller)
                                        }
                                    }

                                }
                            )
                        }//product seller
                    }//parent
                }
                R.id.id_delete_baselist->{
                    if(selectedId>0){
                        val ids=ArrayList<Int>()
                        ids.add(selectedId)
                        DialogHelper.showPurchaseDeleteItemDialog(this@BaseListActivity,ids,
                            object: IDeleteItem {
                                override fun onDeleteItem(ids:ArrayList<Int>) {
                                    adapter.deleteBaseListItem()
                                    val parent=adapter.getParent()
                                    if(baseListType==BaseAmountType.PRODUCT){
                                        if(parent!=null) viewModel.updateProduct(parent as Product)
                                        if(ids.size>0) viewModel.removeProduct(ids[0])
                                    }else if(baseListType==BaseAmountType.SELLER){
                                        if(parent!=null) viewModel.updateSeller(parent as Seller)
                                        if(ids.size>0) viewModel.removeSeller(ids[0])
                                    }
                                }

                            })
                    }else {
                        Toast.makeText(this@BaseListActivity,R.string.no_selected_item, Toast.LENGTH_LONG).show()
                    }

                }

                R.id.id_edit_baselist->{
                    editBaseListItem()
//                    val baselist= adapter.getBaseList()
//                    if (baselist != null) {
//                        DialogHelper.showBaseListInputDialog(this@BaseListActivity,baselist,
//                            object: IUpdateBaseListItemList {
//                                override fun onUpdateBaseListItemList(baselist: BaseList) {
//                                    // add single product
//                                    adapter.updateAdapterEdit(baselist)
//                                    if(baseListType==BaseAmountType.PRODUCT)
//                                        viewModel.updateProduct(baselist as Product)
//                                    else if(baseListType==BaseAmountType.SELLER)
//                                        viewModel.updateSeller(baselist as Seller)
//                                }
//
//                            }
//                        )
//                    }

                }
                R.id.id_parent_baselist->{
                    if(selectedId>0){
                        DialogHelper.showSelectParentBaseListDialog("Выберите родителя",this@BaseListActivity,
                            object: IDialogListener {
                                //override fun onOkClick(v: View?) {}
                                override fun onOkClick(idParent: Int?) {

                                    val parent=if(idParent==null) null else adapter.nodeList.get(idParent)

                                    val baselist= adapter.getBaseList()

                                    val oldparent=adapter.getParent()
                                    if (baselist != null ) {
                                        // select root
                                        if(parent==null){
                                            baselist.idparent=baselist.id
                                            baselist.fullpath=baselist.id.toString()
                                            baselist.level=0
                                        }else {
                                            //select normal node get from parent
                                            baselist.idparent = parent.id
                                            baselist.fullpath = parent.fullpath + baselist.id
                                            baselist.level = parent.level + 1
                                            // set count for new parent
                                            parent.count=parent.count+1
                                            if(baseListType==BaseAmountType.PRODUCT)
                                                viewModel.updateProduct(parent as Product)
                                            else if(baseListType==BaseAmountType.SELLER)
                                                viewModel.updateSeller(parent as Seller)
                                        }
                                        // replace all children where idparent==product.idparent
                                        if(baselist.count>0){
                                            adapter.childArray.clear()
                                            adapter.setChildren(baselist.id,baselist.fullpath,baselist.level)
                                            for(i in 0 until adapter.childArray.size)
                                                viewModel.updateProduct(adapter.childArray[i] as Product)
                                            adapter.childArray.clear()
                                        }
                                        if(oldparent!=null){
                                            oldparent.count=oldparent.count-1
                                            if(baseListType==BaseAmountType.PRODUCT)
                                                viewModel.updateProduct(oldparent as Product)
                                            else if(baseListType==BaseAmountType.SELLER)
                                                viewModel.updateSeller(oldparent as Seller)
                                        }
                                        // update count for parent
                                        adapter.updateAdapterParent(oldparent,parent,baselist)
                                        if(baseListType==BaseAmountType.PRODUCT)
                                            viewModel.updateProduct(baselist as Product)
                                        else if(baseListType==BaseAmountType.SELLER)
                                            viewModel.updateSeller(baselist as Seller)
                                        //fillAdapter("")
                                        //adapter.notifyDataSetChanged()
                                    }
                                }
                            })
                    }else {//if selected
                        Toast.makeText(this@BaseListActivity,R.string.no_selected_item, Toast.LENGTH_LONG).show()
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
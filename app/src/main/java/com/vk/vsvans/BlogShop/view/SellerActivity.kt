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
import com.vk.vsvans.BlogShop.databinding.ActivitySellerBinding
import com.vk.vsvans.BlogShop.view.dialog.DialogHelper
import com.vk.vsvans.BlogShop.view.`interface`.IDeleteItem
import com.vk.vsvans.BlogShop.view.`interface`.IDialogListener
import com.vk.vsvans.BlogShop.view.`interface`.IUpdateBaseListItemList
import com.vk.vsvans.BlogShop.view.`interface`.OnClickItemCallback
import com.vk.vsvans.BlogShop.model.data.BaseList
import com.vk.vsvans.BlogShop.model.data.Seller
import com.vk.vsvans.BlogShop.viewmodel.ActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SellerActivity : AppCompatActivity() {
    private lateinit var rootElement: ActivitySellerBinding
    lateinit var adapter: BaseListRcAdapter
    private lateinit var tb: Toolbar
    //val dbManager= DbManager(this)
    var viewModel: ActivityViewModel=ActivityViewModel()//?=null
    private var job: Job? = null
    private var selectedId=0
    private var startSelectedId=0
    private var expandedList=false
    private lateinit var searchView: SearchView
    val TAG="MyLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel= ActivityViewModel(application)
        val intent=getIntent()
        startSelectedId=intent.getIntExtra(R.string.SELLER_ID.toString(), 0)
        rootElement= ActivitySellerBinding.inflate(layoutInflater)
        val view=rootElement.root
        setContentView(view)
        //setContentView(R.layout.activity_seller)
        //устанавливаем тоолбар
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
                //if(idPurchase==0) old_time=0L
                data.putExtra(getString(R.string.SELLER_ID), baseList.id)
                //Seller seller=adapter.
                data.putExtra(Seller::class.java.getSimpleName(), baseList as Seller)
                setResult(RESULT_OK, data)
                onBackPressed()
            }

            override fun onEditItem() {}
            override fun onDeleteItem() {}
            override fun onNewItem(parent: BaseList) {}
            override fun refreshItem() { adapter.refreshItem() }
            override fun onParentItem() {}
        })// adapter

        val rcView=rootElement.rcViewSellerList
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = adapter

        initViewModel()
        bottomMenuOnClick()
    }//onCreate

    private fun initViewModel(){
        //{} это слушатель
        //если наше activity доступно не разрушено или ждет когда можно обновить слушателт сработает
        viewModel.liveSellerList.observe(this,{
            adapter.updateAdapter(it)
            if(startSelectedId>0){
                val pos=adapter.setSelectedPositionById(startSelectedId)
                if(pos>0) {
                    rootElement.rcViewSellerList.scrollToPosition(pos)
                }
                startSelectedId=0;
            }
        })
    }
    override fun onResume() {
        super.onResume()
 //       viewModel.openDb()
        fillAdapter("")

    }

    override fun onDestroy() {
        super.onDestroy()
//        viewModel.closeDb()
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
//            val list = dbManager.getSellers(text)
//            adapter.updateAdapter(list)
            viewModel.getSellers(text)
        }

    }

    // add root element
    fun onClickAddSeller(view: View){
        val seller= Seller()
        //новая запись
        seller.id=0
        DialogHelper.showBaseListInputDialog(this@SellerActivity,seller,
            object: IUpdateBaseListItemList {
                override fun onUpdateBaseListItemList(seller: BaseList) {
                    // add single product
                    val id=viewModel.insertSeller(seller as Seller)
                    if (id != null) {
                        seller.id=id
                        seller.idparent=id
                        seller.fullpath=id.toString()
                        viewModel.updateSeller(seller as Seller)
                        adapter.updateAdapterInsert(seller)
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
                    this@SellerActivity,
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
                    val parent=adapter.getBaseList() //getParent()
                    if(parent!=null) {
                        val seller = Seller()
                        //новая запись
                        seller.id = 0
                        seller.idparent = parent.id
                        seller.level = parent.level + 1
                        parent.count = parent.count + 1
                        DialogHelper.showBaseListInputDialog(this@SellerActivity, seller,
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
                    }
                }
                R.id.id_delete_baselist->{
                    // selectedId устанавливается в onItemClick
                    if(selectedId>0){
                        DialogHelper.showPurchaseDeleteItemDialog(this@SellerActivity,selectedId,
                            object: IDeleteItem {
                                override fun onDeleteItem(id:Int) {
                                    adapter.deleteBaseListItem()
                                    val parent=adapter.getParent()
                                    if(parent!=null) viewModel.updateSeller(parent as Seller)
                                    //reset selectedId
                                    //selectedId=0
                                    viewModel.removeSeller(id)
                                }

                            })
                    }else {
                        Toast.makeText(this@SellerActivity,R.string.no_selected_item, Toast.LENGTH_LONG).show()
                    }
                }
                R.id.id_edit_baselist->{
                    val seller= adapter.getBaseList()
                    if (seller != null) {
                        DialogHelper.showBaseListInputDialog(this@SellerActivity,seller,
                            object: IUpdateBaseListItemList {
                                override fun onUpdateBaseListItemList(seller: BaseList) {
                                    // add single seller
                                    adapter.updateAdapterEdit(seller)
                                    viewModel.updateSeller(seller as Seller)
                                }

                            }
                        )
                    }

                }
                R.id.id_parent_baselist->{
                    if(selectedId>0){
                    DialogHelper.showSelectParentBaseListDialog("Выберите родителя",this@SellerActivity,
                        object: IDialogListener {
                            //override fun onOkClick(v: View?) {}
                            override fun onOkClick(idParent: Int?) {

                                val parent=if(idParent==null) null else adapter.nodeList.get(idParent)

                                val seller= adapter.getBaseList()

                                val oldparent=adapter.getParent()
                                if (seller != null ) {
                                    // select root
                                    if(parent==null){
                                        seller.idparent=seller.id
                                        seller.fullpath=seller.id.toString()
                                        seller.level=0
                                    }else {
                                        //select normal node get from parent
                                        seller.idparent = parent.id
                                        seller.fullpath = parent.fullpath + seller.id
                                        seller.level = parent.level + 1
                                        // set count for new parent
                                        parent.count=parent.count+1
                                        viewModel.updateSeller(parent as Seller)
                                    }
                                    // replace all children where idparent==seller.idparent
                                    if(seller.count>0){
                                        adapter.childArray.clear()
                                        adapter.setChildren(seller.id,seller.fullpath,seller.level)
                                        for(i in 0 until adapter.childArray.size)
                                            viewModel.updateSeller(adapter.childArray[i] as Seller)
                                        adapter.childArray.clear()
                                    }
                                    if(oldparent!=null){
                                        oldparent.count=oldparent.count-1
                                        viewModel.updateSeller(oldparent as Seller)
                                    }
                                    // update count for parent
                                    adapter.updateAdapterParent(oldparent,parent,seller)
                                    viewModel.updateSeller(seller as Seller)
                                    //fillAdapter("")
                                    //adapter.notifyDataSetChanged()
                                }
                            }
                        })
                    }else {//if selected
                        Toast.makeText(this@SellerActivity,R.string.no_selected_item, Toast.LENGTH_LONG).show()
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
    }

}
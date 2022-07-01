package com.vk.vsvans.BlogShop.view

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
    var viewModel: ActivityViewModel?=null
    private var job: Job? = null
    private var selectedId=0
    private lateinit var searchView: SearchView
    val TAG="MyLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= ActivityViewModel(application)
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

            override fun onEditItem() {
                val seller= adapter.getBaseList()
                if (seller != null) {
                    DialogHelper.showBaseListInputDialog(this@SellerActivity,seller,
                        object: IUpdateBaseListItemList {
                            override fun onUpdateBaseListItemList(seller: BaseList) {
                                // add single seller
                                adapter.updateAdapterEdit(seller)
                                viewModel!!.updateSeller(seller as Seller)
                            }

                        }
                    )
                }
            }

            override fun onDeleteItem() {
                if(selectedId>0){
                    DialogHelper.showPurchaseDeleteItemDialog(this@SellerActivity,selectedId,
                        object: IDeleteItem {
                            override fun onDeleteItem(id:Int) {
                                adapter.deleteBaseListItem()
                                val parent=adapter.getParent()
                                if(parent!=null) viewModel!!.updateSeller(parent as Seller)
                                //reset selectedId
                                //selectedId=0
                                viewModel!!.removeSeller(id)
                            }

                        })
                }else {
                    Toast.makeText(this@SellerActivity,R.string.no_selected_item, Toast.LENGTH_LONG).show()
                }
            }

            override fun onNewItem(parent: BaseList) {
                //adapter.addsellerItem(seller)
                // add single seller with parent
                val seller= Seller()
                //новая запись
                seller.id=0
                seller.idparent=parent.id
                seller.level=parent.level+1
                parent.count=parent.count+1
                DialogHelper.showBaseListInputDialog(this@SellerActivity,seller,
                    object: IUpdateBaseListItemList {
                        override fun onUpdateBaseListItemList(seller: BaseList) {
                            val id=viewModel!!.insertSeller(seller as Seller)
                            if (id != null) {
                                seller.id=id
                                seller.fullpath=parent.fullpath+id.toString()
                                // update seller array in adapter
                                adapter.updateAdapterInsert(seller)
                                // update count for parent
                                viewModel!!.updateSeller(parent as Seller)
                                // update fullpath for seller
                                viewModel!!.updateSeller(seller as Seller)
                            }
                        }

                    }
                )
            }

            override fun refreshItem() {
                adapter.refreshItem()
            }

            override fun onParentItem() {
                DialogHelper.showSelectParentBaseListDialog("Выберите родителя",this@SellerActivity,
                    object: IDialogListener {
                        //override fun onOkClick(v: View?) {}
                        override fun onOkClick(idParent: Int?) {

                            var parent=if(idParent==null) null else adapter.nodeList.get(idParent)

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
                                    viewModel!!.updateSeller(parent as Seller)
                                }
                                // replace all children where idparent==seller.idparent
                                if(seller.count>0){
                                    adapter.childArray.clear()
                                    adapter.setChildren(seller.id,seller.fullpath,seller.level)
                                    for(i in 0 until adapter.childArray.size)
                                        viewModel!!.updateSeller(adapter.childArray[i] as Seller)
                                    adapter.childArray.clear()
                                }
                                if(oldparent!=null){
                                    oldparent.count=oldparent.count-1
                                    viewModel!!.updateSeller(oldparent as Seller)
                                }
                                // update count for parent
                                adapter.updateAdapterParent(oldparent,parent,seller)
                                viewModel!!.updateSeller(seller as Seller)
                                //fillAdapter("")
                                //adapter.notifyDataSetChanged()
                            }
                        }
                    })
            }

            //override fun onTimeClick() {}

        })
        val rcView=rootElement.rcViewSellerList
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = adapter

        initViewModel()

    }//onCreate

    private fun initViewModel(){
        //{} это слушатель
        //если наше activity доступно не разрушено или ждет когда можно обновить слушателт сработает
        viewModel?.liveSellerList?.observe(this,{
            adapter.updateAdapter(it)
        })
    }
    override fun onResume() {
        super.onResume()
        viewModel!!.openDb()
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel!!.closeDb()
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
            viewModel!!.getSellers(text)
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
                    val id=viewModel!!.insertSeller(seller as Seller)
                    if (id != null) {
                        seller.id=id
                        seller.idparent=id
                        seller.fullpath=id.toString()
                        viewModel!!.updateSeller(seller as Seller)
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
            }//search_item

            // GO BACK
            tb.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }//setupToolbar

}
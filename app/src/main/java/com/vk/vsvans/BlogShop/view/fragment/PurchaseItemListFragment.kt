package com.vk.vsvans.BlogShop.view.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.vk.vsvans.BlogShop.view.EditPurchaseActivity

import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.view.adapter.PurchaseItemRcAdapter
import com.vk.vsvans.BlogShop.databinding.ListPurchaseItemFragBinding
import com.vk.vsvans.BlogShop.view.dialog.DialogHelper
import com.vk.vsvans.BlogShop.view.`interface`.*
import com.vk.vsvans.BlogShop.model.data.BaseList

import com.vk.vsvans.BlogShop.model.data.PurchaseItem

import kotlinx.coroutines.Job

//!реклама
//class PurchaseItemListFragment(private val fragCloseInterface: FragmentCloseInterface, private val newList:ArrayList<PurchaseItem>?) : BaseMobAdFrag(),
//    AdapterCallback {
class PurchaseItemListFragment(private val fragCloseInterface:IFragmentCloseInterface, val fragCallBack: IFragmentCallBack, private val newList:ArrayList<PurchaseItem>?) : Fragment() {

    //    lateinit var rootElement: ListPurchaseItemFragBinding
    lateinit var adapter: PurchaseItemRcAdapter
    //    val dragCallback= ItemTouchMoveCallBack(adapter)
//    val touchHelper=ItemTouchHelper(dragCallback)
    private var job: Job?=null
    //private var addPurchaseItem:MenuItem?=null
    //private lateinit var tb:Toolbar
    val TAG="MyLog"
    private var selectedId=0
    lateinit var binding:ListPurchaseItemFragBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= ListPurchaseItemFragBinding.inflate(layoutInflater,container,false)

//!реклама
//        adView=binding.adView

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        //Assign adapter
        adapter= PurchaseItemRcAdapter(object: OnClickItemCallback {
            override fun onClickItem(id:Int) {
                if(id>0) {
                    // may be delete item
                    selectedId=id
                }
            }

            override fun onEditItem() {
                val pit= adapter.getPurchaseItem()
                if (pit != null) {
                    DialogHelper.showPurchaseItemInputDialog(activity as EditPurchaseActivity,pit,
                        object:IUpdatePurchaseItemList{
                            override fun onUpdatePurchaseItemList(pit: PurchaseItem) {
                                adapter.updateAdapterEdit(pit)
                            }

                        })
                }
            }

            override fun onDeleteItem() {
                if(selectedId>=0){
                    DialogHelper.showPurchaseDeleteItemDialog(activity as EditPurchaseActivity,selectedId,
                        object: IDeleteItem {
                            override fun onDeleteItem(id: Int) {
                                fragCallBack.onFragmentCallBack(adapter.getPurchaseItem()!!)
                                adapter.deletePurchaseItem()
                            }

                        })
                }else {
                    Toast.makeText(activity as EditPurchaseActivity,R.string.no_selected_item, Toast.LENGTH_LONG).show()
                }
            }

            override fun onNewItem(parent: BaseList) {}
            override fun refreshItem() {}
            override fun onParentItem() {}
//            override fun onTimeClick() {}
        })
// end assign adapter

        val dragCallback= ItemTouchMoveCallBack(adapter)
        val touchHelper=ItemTouchHelper(dragCallback)

        binding.apply {
            //val rcView=view.findViewById<RecyclerView>(R.id.rcViewSelectImage)
            setupToolbar()
            val rcView = rcViewSelectPurchaseItem
            touchHelper.attachToRecyclerView(rcView)
            rcView.layoutManager = LinearLayoutManager(activity)
            rcView.adapter = adapter

            if (newList != null) {
                adapter.updateAdapter(newList)
            }

        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupToolbar(){
        // кнопка home <- слушатель
        binding.tb.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this@PurchaseItemListFragment)?.commit()
        }

    }


//!реклама
/*
    override fun onClose() {
        super.onClose()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@ImageListFrag)?.commit()
    }
*/

}
package com.vk.vsvans.BlogShop.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vk.vsvans.BlogShop.activity.EditPurchaseActivity

import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.adapters.PurchaseItemRcAdapter
import com.vk.vsvans.BlogShop.dialogs.DialogHelper
import com.vk.vsvans.BlogShop.interfaces.*
import com.vk.vsvans.BlogShop.model.Product

import com.vk.vsvans.BlogShop.model.PurchaseItem

import kotlinx.coroutines.Job

//!реклама
//class PurchaseItemListFragment(private val fragCloseInterface: FragmentCloseInterface, private val newList:ArrayList<PurchaseItem>?) : BaseMobAdFrag(),
//    AdapterCallback {
class PurchaseItemListFragment(private val fragCloseInterface:FragmentCloseInterface,val fragCallBack: IFragmentCallBack,private val newList:ArrayList<PurchaseItem>?) : Fragment(),AdapterCallback {

    //lateinit var rootElement:ListImageFragBinding
    lateinit var adapter: PurchaseItemRcAdapter
//    val dragCallback= ItemTouchMoveCallBack(adapter)
//    val touchHelper=ItemTouchHelper(dragCallback)
    private var job: Job?=null
    //private var addPurchaseItem:MenuItem?=null
    private lateinit var tb:Toolbar
    val TAG="MyLog"
    private var selectedId=0
//    lateinit var binding:ListProductFragBinding

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        binding= ListProductFragBinding.inflate(layoutInflater)
//
////!реклама
////        adView=binding.adView
//
//        return binding.root
//    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      // super.onCreateView(inflater, container, savedInstanceState)
    val view: View = inflater.inflate(R.layout.list_purchase_item_frag, container,false)
    return view
}
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
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
                           // adapter.setPurchaseItem(pit, adapter.selected_position)
                            adapter.updateAdapterEdit(pit)
                            //(activity as EditPurchaseActivity).dbManager.updatePurchaseItem(pit)
                        }

                    })
                }
            }

            override fun onDeleteItem() {
                if(selectedId>0){
                    DialogHelper.showPurchaseDeleteItemDialog(activity as EditPurchaseActivity,selectedId,
                        object: IDeleteItem {
                            override fun onDeleteItem(id: Int) {
                                adapter.deleteProductItem()
                            }

                        })
                }else {
                    Toast.makeText(activity as EditPurchaseActivity,R.string.no_selected_item, Toast.LENGTH_LONG).show()
                }
            }
        })
        val dragCallback= ItemTouchMoveCallBack(adapter)
        val touchHelper=ItemTouchHelper(dragCallback)
        //устанавливаем тоолбар
        tb=view.findViewById<Toolbar>(R.id.tb)
        setUpToolbar()
        val rcView=view.findViewById<RecyclerView>(R.id.rcViewSelectPurchaseItem)
        touchHelper.attachToRecyclerView(rcView)

        rcView.layoutManager = LinearLayoutManager(activity)
        rcView.adapter = adapter

        if (newList != null) {
            adapter.updateAdapter(newList)

        }
//        binding.apply {
//            //val rcView=view.findViewById<RecyclerView>(R.id.rcViewSelectImage)
//            val rcView = rcViewSelectPurchaseItem
////            touchHelper.attachToRecyclerView(rcView)
//            rcView.layoutManager = LinearLayoutManager(activity)
//
//            rcView.adapter = adapter
//
//            if (newList != null) {
//                //adapter.updateAdapter(newList,true)
//                adapter.update(newList)
//            }
//
//        }

    }

    override fun onResume() {
        super.onResume()
        setAddImageButton()
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setUpToolbar(){
//        binding.apply {

            //tb.inflateMenu(R.menu.menu_choose_purchase_item)
            // кнопка home <- слушатель
            tb.setNavigationOnClickListener {
                // просто this будет ссылаться на binding класс поэтому this@ImageListFrag
                //!реклама
                activity?.supportFragmentManager?.beginTransaction()?.remove(this@PurchaseItemListFragment)?.commit()
                //!реклама
                //            showInterAd()
            }

    }


    fun setAddImageButton(){
//        if(adapter.mainArray.size <ImagePicker.MAX_IMAGE_COUNT){
//            addImageItem?.isVisible=true
//        }else{
//            addImageItem?.isVisible=false
//        }
    }

    // вызывается из фрагмента при очистке списка выбранных картинок


    override fun onItemDelete(pit:PurchaseItem) {
        if(fragCallBack!=null) fragCallBack.onFragmentCallBack(pit)
//        if(activity!=null){
//            (activity as EditPurchaseActivity).deletePurchaseItem(pit)
//        }
        //setAddImageButton()
    }

//!реклама
/*
    override fun onClose() {
        super.onClose()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@ImageListFrag)?.commit()
    }
*/

}
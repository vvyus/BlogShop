package com.vk.vsvans.BlogShop.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.vk.vsvans.BlogShop.R
import com.vk.vsvans.BlogShop.databinding.FragmentProductAmountBinding
import com.vk.vsvans.BlogShop.databinding.ListPurchaseItemFragBinding
import com.vk.vsvans.BlogShop.model.data.ProductAmount
import com.vk.vsvans.BlogShop.model.data.PurchaseItem
import com.vk.vsvans.BlogShop.util.FilterForActivity
import com.vk.vsvans.BlogShop.view.`interface`.ICallBackAmountAdapter
import com.vk.vsvans.BlogShop.view.`interface`.IFragmentCloseInterface
import com.vk.vsvans.BlogShop.view.adapter.ProductAmountRcAdapter
import com.vk.vsvans.BlogShop.view.adapter.PurchaseItemRcAdapter

class ProductAmountFragment(val fragCloseInterface: IFragmentCloseInterface, val newList:ArrayList<ProductAmount>?,val filterForActivity: FilterForActivity) : Fragment() {
    lateinit var binding:FragmentProductAmountBinding
    lateinit var adapter: ProductAmountRcAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_product_amount, container, false)
        binding= FragmentProductAmountBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter= ProductAmountRcAdapter(object : ICallBackAmountAdapter {
            override fun onClickItem() {
                //binding.tbProductAmount.
                activity?.supportFragmentManager?.beginTransaction()?.remove(this@ProductAmountFragment)?.commit()

            }

        },filterForActivity)
        setupToolbar()
        binding.apply {
            val rcView = rcViewProductAmount
            rcView.layoutManager = LinearLayoutManager(activity)
            rcView.adapter = adapter
            if (newList != null) {
                adapter.updateAdapter(newList)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose()
    }
    private fun setupToolbar(){
        // кнопка home <- слушатель
        binding.tbProductAmount.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this@ProductAmountFragment)?.commit()
        }

    }
}
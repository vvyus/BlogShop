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
import com.vk.vsvans.BlogShop.view.`interface`.IFragmentCloseInterface

class ProductAmountFragment(private val fragCloseInterface: IFragmentCloseInterface) : Fragment() {
    lateinit var binding:FragmentProductAmountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_product_amount, container, false)
        binding= FragmentProductAmountBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        binding.apply {
            val rcView = rcViewProductAmount
            rcView.layoutManager = LinearLayoutManager(activity)
            //rcView.adapter = adapter
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
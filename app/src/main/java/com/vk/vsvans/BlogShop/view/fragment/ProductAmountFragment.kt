package com.vk.vsvans.BlogShop.view.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.recyclerview.widget.LinearLayoutManager
import com.vk.vsvans.BlogShop.databinding.FragmentProductAmountBinding
import com.vk.vsvans.BlogShop.model.data.BaseAmount
import com.vk.vsvans.BlogShop.model.data.ProductAmount
import com.vk.vsvans.BlogShop.util.FilterForActivity
import com.vk.vsvans.BlogShop.util.UtilsHelper
import com.vk.vsvans.BlogShop.view.MainActivity
import com.vk.vsvans.BlogShop.view.`interface`.ICallBackAmountAdapter
import com.vk.vsvans.BlogShop.view.`interface`.IFragmentCloseInterface
import com.vk.vsvans.BlogShop.view.adapter.BaseAmountRcAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductAmountFragment(val fragCloseInterface: IFragmentCloseInterface, val newList:ArrayList<BaseAmount>?,val filterForActivity: FilterForActivity) : Fragment() {
    lateinit var binding:FragmentProductAmountBinding
    lateinit var adapter: BaseAmountRcAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_product_amount, container, false)
        binding= FragmentProductAmountBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter= BaseAmountRcAdapter(object : ICallBackAmountAdapter {
            override fun onClickItem() {
                //binding.tbProductAmount.
                activity?.supportFragmentManager?.beginTransaction()?.remove(this@ProductAmountFragment)?.commit()

            }

        },filterForActivity)
        setupToolbar()
        var time=UtilsHelper.getCurrentDate()
        binding.apply {
             val rcView = rcViewProductAmount
            rcView.layoutManager = LinearLayoutManager(activity)
            rcView.adapter = adapter
            if (newList != null) {
                adapter.updateAdapter(newList)
            }
            tvDateAmount.setOnClickListener{
                val calendar: Calendar = Calendar.getInstance()
                calendar.setTime(Date(time))
                DatePickerDialog(
                    activity!!,
                    { datePicker: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        val calendar1: Calendar = Calendar.getInstance()
                        calendar1.setTime(Date(time))
                        calendar1.set(year, monthOfYear, dayOfMonth)
                        time=calendar1.getTimeInMillis()
                        initDateTimeButtons(time)
                        (activity as MainActivity)!!.viewModel.getProductAmount("",time)
                        //refresh adapter
                        adapter.updateAdapter((activity as MainActivity)!!.liveProductAmount)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                    .show()
            }
            initDateTimeButtons(time)
        }//binding

    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose()
    }

    private fun initDateTimeButtons(time:Long){
        //val mDateMediumFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        //
        val mDateMediumFormat = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        binding.tvDateAmount.setText(mDateMediumFormat.format(Date(time)))
    }
    private fun setupToolbar(){
        // кнопка home <- слушатель
        binding.tbProductAmount.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this@ProductAmountFragment)?.commit()
        }

    }
}
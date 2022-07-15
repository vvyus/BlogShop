package com.vk.vsvans.BlogShop.view.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.recyclerview.widget.LinearLayoutManager
import com.vk.vsvans.BlogShop.databinding.FragmentBaseAmountBinding
import com.vk.vsvans.BlogShop.model.data.BaseAmount
import com.vk.vsvans.BlogShop.model.data.BaseAmountType
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

class BaseAmountFragment(val fragCloseInterface: IFragmentCloseInterface, val newList:ArrayList<BaseAmount>?,
                         val filterForActivity: FilterForActivity,val baseAmountType: BaseAmountType) : Fragment() {
    lateinit var binding:FragmentBaseAmountBinding
    lateinit var adapter: BaseAmountRcAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_product_amount, container, false)
        binding= FragmentBaseAmountBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter= BaseAmountRcAdapter(object : ICallBackAmountAdapter {
            override fun onClickItem() {
                //binding.tbProductAmount.
                activity?.supportFragmentManager?.beginTransaction()?.remove(this@BaseAmountFragment)?.commit()

            }

        },filterForActivity,baseAmountType)
        setupToolbar()
        var time=UtilsHelper.getCurrentDate()
        binding.apply {
             val rcView = rcViewBaseAmount
            rcView.layoutManager = LinearLayoutManager(activity)
            rcView.adapter = adapter
            var pos=0
            if (newList != null) {
                adapter.updateAdapter(newList)
                // return from activity to fragment
                if(filterForActivity.idSeller!=null && filterForActivity.idSeller!!>0 && baseAmountType==BaseAmountType.SELLER){
                    pos=adapter.setSelectedPositionById(filterForActivity.idSeller)
                    if(pos>0) {
                        rcView.scrollToPosition(pos)
                    }
                }else if(filterForActivity.content!=null && baseAmountType==BaseAmountType.PRODUCT){
                    pos=adapter.setSelectedPositionByContent(filterForActivity.content)
                    if(pos>0) {
                        rcView.scrollToPosition(pos)
                    }
                }
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
                        if(baseAmountType==BaseAmountType.PRODUCT){
                            (activity as MainActivity)!!.viewModel.getProductAmount("",time)
                            //refresh adapter
                            adapter.updateAdapter((activity as MainActivity)!!.liveProductAmount)
                        }else if(baseAmountType==BaseAmountType.SELLER){
                            (activity as MainActivity)!!.viewModel.getSellerAmount("",time)
                            //refresh adapter
                            adapter.updateAdapter((activity as MainActivity)!!.liveSellerAmount)

                        }

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
        binding.tbBaseAmount.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this@BaseAmountFragment)?.commit()
        }

    }
}
package com.zx.landdisaster.module.disaster.func.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.zx.landdisaster.R
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.util.ZXToastUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Xiangb on 2019/4/16.
 * 功能：
 */
class DateRegionTool(var context: Context) {


    companion object {
        fun getInstance(context: Context) = DateRegionTool(context)
    }

    lateinit var rgDate: RadioGroup
    lateinit var rbStart: RadioButton
    lateinit var rbEnd: RadioButton
    lateinit var dpDate: DatePicker
    lateinit var tvStart: TextView
    lateinit var tvEnd: TextView
    lateinit var ivStartReset: ImageView
    lateinit var ivEndReset: ImageView
    var startTime = ""
    var endTime = ""
    var callBack: (String) -> Unit = {}

    fun showDateDialog(startInitTime: String, endInitTime: String, callInitBack: (String) -> Unit) {
        startTime = startInitTime
        endTime = endInitTime
        callBack = callInitBack
        val view = LayoutInflater.from(context).inflate(R.layout.layout_date_filter, null, false)
        rgDate = view.findViewById(R.id.rg_date_filter)
        rbStart = view.findViewById(R.id.rb_date_filterStart)
        rbEnd = view.findViewById(R.id.rb_date_filterEnd)
        dpDate = view.findViewById(R.id.dp_date_filter)
        tvStart = view.findViewById(R.id.tv_filter_startTime)
        tvEnd = view.findViewById(R.id.tv_filter_endTime)
        ivStartReset = view.findViewById(R.id.iv_filter_start_reset)
        ivEndReset = view.findViewById(R.id.iv_filter_end_reset)
        rgDate.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rb_date_filterStart) {
                dpDate.initDate(startTime)
            } else {
                dpDate.initDate(endTime)
            }
        }
//        dpDate.initDate(startTime)
        rbStart.isChecked = true
        tvStart.text = startTime
        tvEnd.text = endTime
        ivStartReset.setOnClickListener {
            tvStart.text = ""
            startTime = ""
        }
        ivEndReset.setOnClickListener {
            tvEnd.text = ""
            endTime = ""
        }
        tvStart.addTextChange(1)
        tvEnd.addTextChange(2)
        ZXDialogUtil.showCustomViewDialog(context, "", view, { dialog, which ->
            if (startTime.isNotEmpty() && endTime.isNotEmpty() && ZXTimeUtil.string2Millis(startTime, "yyyy-MM-dd") > ZXTimeUtil.string2Millis(endTime, "yyyy-MM-dd")) {
                endTime = ""
            }
            callBack("$startTime,$endTime")
        }, { dialog, which ->
        })
    }

    private fun TextView.addTextChange(type: Int) {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (type == 1) {
                    ivStartReset.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
                } else {
                    ivEndReset.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
                }
            }

        })
    }

    private fun DatePicker.initDate(dateString: String) {
        val currentYear: Int
        val currentMonth: Int
        val currentDay: Int
        if (dateString.isNotEmpty()) {
            currentYear = dateString.substring(0, 4).toInt()
            currentMonth = dateString.substring(5, 7).toInt() - 1
            currentDay = dateString.substring(8, 10).toInt()
        } else {
            currentYear = Calendar.getInstance().get(Calendar.YEAR)
            currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val calendar = Calendar.getInstance()
            calendar.set(getYear(), getMonth(), getDayOfMonth())
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            if (rbStart.isChecked) {
                startTime = sdf.format(calendar.time)
                tvStart.text = startTime
            } else {
                endTime = sdf.format(calendar.time)
                tvEnd.text = endTime
            }
        }
        init(currentYear, currentMonth, currentDay) { view, year, monthOfYear, dayOfMonth ->
            if (rgDate.checkedRadioButtonId == R.id.rb_date_filterStart) {
                if (startTime.isNotEmpty() && endTime.isNotEmpty() && ZXTimeUtil.string2Millis(startTime, "yyyy-MM-dd") > ZXTimeUtil.string2Millis(endTime, "yyyy-MM-dd")) {
                    ZXToastUtil.showToast("开始时间不能大于结束时间")
                }
                val calendar = Calendar.getInstance()
                calendar.set(getYear(), getMonth(), getDayOfMonth())
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                startTime = sdf.format(calendar.time)
                tvStart.text = startTime
            } else {
                if (startTime.isNotEmpty() && endTime.isNotEmpty() && ZXTimeUtil.string2Millis(startTime, "yyyy-MM-dd") > ZXTimeUtil.string2Millis(endTime, "yyyy-MM-dd")) {
                    ZXToastUtil.showToast("开始时间不能大于结束时间")
                }
                val calendar = Calendar.getInstance()
                calendar.set(getYear(), getMonth(), getDayOfMonth())
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                endTime = sdf.format(calendar.time)
                tvEnd.text = endTime
            }
        }
    }

}
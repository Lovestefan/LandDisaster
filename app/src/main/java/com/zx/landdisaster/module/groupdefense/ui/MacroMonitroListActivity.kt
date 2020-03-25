package com.zx.landdisaster.module.groupdefense.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jzxiang.pickerview.TimePickerDialog
import com.jzxiang.pickerview.data.Type
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroBean
import com.zx.landdisaster.module.groupdefense.func.adapter.MacroMonitroListAdapter
import com.zx.landdisaster.module.groupdefense.mvp.contract.MacroMonitroListContract
import com.zx.landdisaster.module.groupdefense.mvp.model.MacroMonitroListModel
import com.zx.landdisaster.module.groupdefense.mvp.presenter.MacroMonitroListPresenter
import com.zx.zxutils.util.ZXSystemUtil
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_macro_monitro_list.*
import java.text.SimpleDateFormat


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MacroMonitroListActivity : BaseActivity<MacroMonitroListPresenter, MacroMonitroListModel>(), MacroMonitroListContract.View {
    private var setid: String = ""

    private val dataList = arrayListOf<MacroMonitroBean>()

    private val macroMonitroListAdapter = MacroMonitroListAdapter(dataList)

    companion object {
        /**
         * 启动器
         */

        fun startAction(activity: Activity, isFinish: Boolean, setid: String) {
            val intent = Intent(activity, MacroMonitroListActivity::class.java)
            intent.putExtra("setid", setid)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_macro_monitro_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        toolbar_view.setRightClickListener { ZXSystemUtil.callTo(this, resources.getString(R.string.toolbar_tel_string)) }

        setid = intent.getStringExtra("setid")

        tv_start_time.setOnClickListener {
            TimePickerDialog.Builder().apply {
                setCallBack { _, millseconds ->

                    if ("结束时间" != tv_end_time.text) {

                        var startMillis = ZXTimeUtil.string2Millis(tv_end_time.text.toString())

                        if (millseconds > startMillis) {
                            showToast("开始时间大于结束时间")
                            return@setCallBack
                        }
                    }

                    tv_start_time.text = ZXTimeUtil.getTime(millseconds)
                    if ("开始时间" != tv_start_time.text)
                        iv_close_start_time.visibility = View.VISIBLE
                }
                setCyclic(true)
                setMaxMillseconds(System.currentTimeMillis())
                setThemeColor(ContextCompat.getColor(mContext!!, R.color.colorPrimary))
                setType(Type.ALL)
                setWheelItemTextNormalColor(ContextCompat.getColor(mContext!!, R.color.text_color_light))
                setWheelItemTextSelectorColor(ContextCompat.getColor(mContext!!, R.color.text_color_normal))
                setTitleStringId("请选择时间")

            }.build().show(this.supportFragmentManager, "time_select")

        }

        tv_end_time.setOnClickListener {
            TimePickerDialog.Builder().apply {
                setCallBack { _, millseconds ->


                    if ("开始时间" != tv_start_time.text) {

                        var startMillis = ZXTimeUtil.string2Millis(tv_start_time.text.toString())

                        if (millseconds < startMillis) {
                            showToast("结束时间小于开始时间")
                            return@setCallBack
                        }
                    }

                    tv_end_time.text = ZXTimeUtil.getTime(millseconds)
                    if ("结束时间" != tv_end_time.text)
                        iv_close_end_time.visibility = View.VISIBLE
                }
                setCyclic(true)
                setMaxMillseconds(System.currentTimeMillis())
                setThemeColor(ContextCompat.getColor(mContext!!, R.color.colorPrimary))
                setType(Type.ALL)
                setWheelItemTextNormalColor(ContextCompat.getColor(mContext!!, R.color.text_color_light))
                setWheelItemTextSelectorColor(ContextCompat.getColor(mContext!!, R.color.text_color_normal))
                setTitleStringId("请选择时间")

            }.build().show(this.supportFragmentManager, "time_select")
        }

        iv_close_start_time.setOnClickListener {
            if ("开始时间" != tv_start_time.text) {
                tv_start_time.text = "开始时间"
                iv_close_start_time.visibility = View.GONE
            }

        }

        iv_close_end_time.setOnClickListener {
            if ("结束时间" != tv_end_time.text) {
                tv_end_time.text = "结束时间"
                iv_close_end_time.visibility = View.GONE
            }
        }

        tv_search.setOnClickListener {
            findMacroMonitroList()
        }

        rv_macro_monitro.setLayoutManager(LinearLayoutManager(this@MacroMonitroListActivity))
                .setAdapter(macroMonitroListAdapter)
                .setPageSize(9999)
                .showLoadInfo(true)
                .setSRListener(object : ZXSRListener<MacroMonitroBean> {
                    override fun onItemLongClick(item: MacroMonitroBean?, position: Int) {

                    }

                    override fun onLoadMore() {

                    }

                    override fun onRefresh() {
                        findMacroMonitroList()
                    }

                    override fun onItemClick(item: MacroMonitroBean?, position: Int) {
                    }

                })

        findMacroMonitroList()
    }

    @SuppressLint("SimpleDateFormat")
    private fun findMacroMonitroList() {
        var startTime = "2019-01-01 00:00:00"
        var endTime = ZXTimeUtil.getCurrentTime(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))

        if ("开始时间" != tv_start_time.text) {
            startTime = tv_start_time.text.toString()
        }
        if ("结束时间" != tv_end_time.text) {
            endTime = tv_end_time.text.toString()
        }

        mPresenter.findMacroMonitroList(setid, startTime, endTime)
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    override fun onfindMacroMonitroListResult(disasterList: List<MacroMonitroBean>?) {
        rv_macro_monitro.stopRefresh()
        if (disasterList != null) {
            rv_macro_monitro.setLoadInfo(disasterList.size)
            dataList.clear()
            dataList.addAll(disasterList)
            macroMonitroListAdapter.notifyDataSetChanged()
        } else {
            macroMonitroListAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { findMacroMonitroList() }
            rv_macro_monitro.setLoadInfo(0)
            dataList.clear()
            macroMonitroListAdapter.notifyDataSetChanged()
        }
    }

}

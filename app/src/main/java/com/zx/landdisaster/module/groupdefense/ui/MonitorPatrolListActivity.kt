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
import com.zx.landdisaster.module.groupdefense.bean.MonitorPatrolBean
import com.zx.landdisaster.module.groupdefense.func.adapter.MonitorPatrolListAdapter
import com.zx.landdisaster.module.groupdefense.mvp.contract.MonitorPatrolListContract
import com.zx.landdisaster.module.groupdefense.mvp.model.MonitorPatrolListModel
import com.zx.landdisaster.module.groupdefense.mvp.presenter.MonitorPatrolListPresenter
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_monitor_patrol_list.*
import java.text.SimpleDateFormat


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MonitorPatrolListActivity : BaseActivity<MonitorPatrolListPresenter, MonitorPatrolListModel>(), MonitorPatrolListContract.View {

    private var mpid: String = ""
    private var name: String = ""

    private val dataList = arrayListOf<MonitorPatrolBean>()

    private val monitorPatrolListAdapter = MonitorPatrolListAdapter(dataList)


    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, mpid: String, name: String) {
            val intent = Intent(activity, MonitorPatrolListActivity::class.java)
            intent.putExtra("mpid", mpid)
            intent.putExtra("name", name)

            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_monitor_patrol_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        mpid = intent.getStringExtra("mpid")
        name = intent.getStringExtra("name")

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

            findFixMonitroList()
        }

//        rv_monitor_patrol.apply {
//            layoutManager = LinearLayoutManager(mContext)
//            adapter = monitorPatrolListAdapter
//        }

        rv_monitor_patrol.setLayoutManager(LinearLayoutManager(this@MonitorPatrolListActivity))
                .setAdapter(monitorPatrolListAdapter)
                .setPageSize(9999)
                .showLoadInfo(true)
                .setSRListener(object : ZXSRListener<MonitorPatrolBean> {
                    override fun onItemLongClick(item: MonitorPatrolBean?, position: Int) {

                    }

                    override fun onLoadMore() {

                    }

                    override fun onRefresh() {
                        findFixMonitroList()
                    }

                    override fun onItemClick(item: MonitorPatrolBean?, position: Int) {
                    }

                })

        findFixMonitroList()

    }

    @SuppressLint("SimpleDateFormat")
    private fun findFixMonitroList() {
        var startTime = "2019-1-1 00:00:00"
        var endTime = ZXTimeUtil.getCurrentTime(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))

        if ("开始时间" != tv_start_time.text) {
            startTime = tv_start_time.text.toString()
        }
        if ("结束时间" != tv_end_time.text) {
            endTime = tv_end_time.text.toString()
        }

        mPresenter.findFixMonitroList(mpid, name, startTime, endTime)
    }


    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    override fun onfindFixMonitroListResult(disasterList: List<MonitorPatrolBean>?) {

        rv_monitor_patrol.stopRefresh()
        if (disasterList != null) {
            rv_monitor_patrol.setLoadInfo(disasterList.size)
            dataList.clear()
            dataList.addAll(disasterList)
            monitorPatrolListAdapter.notifyDataSetChanged()
        } else {
            monitorPatrolListAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { findFixMonitroList() }
            rv_monitor_patrol.setLoadInfo(0)
            dataList.clear()
            monitorPatrolListAdapter.notifyDataSetChanged()
        }
    }
}

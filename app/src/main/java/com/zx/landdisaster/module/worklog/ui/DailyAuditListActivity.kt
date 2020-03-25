package com.zx.landdisaster.module.worklog.ui

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
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.worklog.bean.DailyAuditListBean
import com.zx.landdisaster.module.worklog.func.adapter.DailyAuditListAdapter
import com.zx.landdisaster.module.worklog.mvp.contract.DailyAuditListContract
import com.zx.landdisaster.module.worklog.mvp.model.DailyAuditListModel
import com.zx.landdisaster.module.worklog.mvp.presenter.DailyAuditListPresenter
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_daily_audit_list.*
import java.text.SimpleDateFormat


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class DailyAuditListActivity : BaseActivity<DailyAuditListPresenter, DailyAuditListModel>(), DailyAuditListContract.View {

    val dataBeans = arrayListOf<DailyAuditListBean>()
    private val listAdapter = DailyAuditListAdapter(dataBeans)
    var pageNo = 1

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, DailyAuditListActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_daily_audit_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        uploadLog(303, 1, "查询日报审核列表")
        sr_daily_audit_list.setLayoutManager(LinearLayoutManager(this))
                .setAdapter(listAdapter)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<DailyAuditListBean> {
                    override fun onItemLongClick(item: DailyAuditListBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()
                    }

                    override fun onRefresh() {
                        loadData(true)
                    }

                    override fun onItemClick(item: DailyAuditListBean?, position: Int) {
                        DailyAuditActivity.startAction(this@DailyAuditListActivity, false, dataBeans[position])
                    }

                })
        loadData(true)
    }

    /**
     * 数据加载
     */
    @SuppressLint("SimpleDateFormat")
    fun loadData(reload: Boolean = false) {
        if (reload) {
            pageNo = 1
            sr_daily_audit_list.clearStatus()
        }

        var startTime = "2017-1-1 00:00:00"
        var endTime = ZXTimeUtil.getCurrentTime(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))

        if ("开始时间" != tv_start_time.text) {
            startTime = tv_start_time.text.toString()
        }
        if ("结束时间" != tv_end_time.text) {
            endTime = tv_end_time.text.toString()
        }

        mPresenter.getPageAuditReport(ApiParamUtil.pageAuditReport("0", startTime, endTime, pageNo, pageSize))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

        tv_start_time.setOnClickListener {
            TimePickerDialog.Builder().apply {
                setCallBack { _, millseconds ->

                    if ("结束时间" != tv_end_time.text) {

                        var startMillis = ZXTimeUtil.string2Millis(tv_end_time.text.toString())

                        if (millseconds > startMillis) {
                            showToast("开始时间小于结束时间")
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
            loadData(true)
        }
    }

    override fun onPageAuditReportResult(list: NormalList<DailyAuditListBean>?) {
        if (list != null) {
            sr_daily_audit_list.refreshData(list.result!!, list.totalRecords)
        } else {
            listAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData(true) }
        }

//        sr_daily_audit_list.stopRefresh()
//        if (dailyList != null) {
//            sr_daily_audit_list.setLoadInfo(dailyList.result!!.size)
//            dataBeans.clear()
//            dataBeans.addAll(dailyList.result!!)
//            listAdapter.notifyDataSetChanged()
//        } else {
//            listAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData() }
//            sr_daily_audit_list.setLoadInfo(0)
//            dataBeans.clear()
//            listAdapter.notifyDataSetChanged()
//        }
    }

}

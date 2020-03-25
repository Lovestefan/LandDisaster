package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.jzxiang.pickerview.TimePickerDialog
import com.jzxiang.pickerview.data.Type
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.disaster.bean.ReportStatisGroupDetailBean
import com.zx.landdisaster.module.disaster.func.adapter.ReportStatisGroupDetailAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.ReportstatisDetailContract
import com.zx.landdisaster.module.disaster.mvp.model.ReportstatisDetailModel
import com.zx.landdisaster.module.disaster.mvp.presenter.ReportstatisDetailPresenter
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_reportstatis_detail.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Create By admin On 2017/7/11
 * 功能：监测数据详情
 */
class ReportstatisDetailActivity : BaseActivity<ReportstatisDetailPresenter, ReportstatisDetailModel>(), ReportstatisDetailContract.View {
    val dataBeans = arrayListOf<ReportStatisGroupDetailBean>()
    private val adapter = ReportStatisGroupDetailAdapter(dataBeans)
    var pageNo = 1

    var startTime = ""
    var endTime = ""

    var areacode = ""//所选区县

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, areaCode: String, title: String) {
            val intent = Intent(activity, ReportstatisDetailActivity::class.java)
            intent.putExtra("code", areaCode)
            intent.putExtra("title", title)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_reportstatis_detail
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        areacode = intent.getStringExtra("code")
        var title = intent.getStringExtra("title")

        tbv_title.setMidText("监测数据-$title")


        rv_info_list.setLayoutManager(LinearLayoutManager(this))
                .setAdapter(adapter)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<ReportStatisGroupDetailBean> {
                    override fun onItemLongClick(item: ReportStatisGroupDetailBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()

                    }

                    override fun onRefresh() {
                        loadData(true)
                    }

                    override fun onItemClick(item: ReportStatisGroupDetailBean?, position: Int) {
                    }

                })
        loadData(true)
    }

    var showTip = false
    fun getWeekDay(seconds: Long, week: Int = 1, show: Boolean = false): String {//week==1 获取本周日，week=2 获取本周一
        var now = seconds
        val c = Calendar.getInstance()
        c.timeInMillis = seconds
        var wek = c.get(Calendar.DAY_OF_WEEK)
        if (week == 1 && wek != week) {//获取本周日
            now += 86400000 * (8 - wek)
            if (show) showTip = true
        } else if (week == 2 && wek != week) {//获取本周一
            if (wek == 1) wek = 8
            now -= 86400000 * (wek - 2)
            if (show) showTip = true
        }
        return ZXTimeUtil.getTime(now, SimpleDateFormat("yyyy-MM-dd"))
    }

    /**
     * 数据加载
     */
    fun loadData(refresh: Boolean = false) {
        if (refresh) {
            pageNo = 1
            rv_info_list.clearStatus()
        }
        mPresenter.pageGroupdefensesreportrateDetail(ApiParamUtil.getReportstatis(pageNo = pageNo, pageSize = pageSize, areacode = areacode, startTime = startTime, endTime = endTime))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

        //重置
        tv_clear.setOnClickListener {
            startTime = ""
            endTime = ""

            tv_startTime.text = startTime
            tv_endTime.text = endTime
            loadData(true)
        }
        //搜索
        tv_search.setOnClickListener {
            if (!tv_startTime.text.toString().isEmpty())
                startTime = getWeekDay(ZXTimeUtil.string2Millis(tv_startTime.text.toString(), "yyyy-MM-dd"), 2, true)
            if (!tv_endTime.text.toString().isEmpty())
                endTime = getWeekDay(ZXTimeUtil.string2Millis(tv_endTime.text.toString(), "yyyy-MM-dd"), 1, true)

            tv_startTime.text = startTime
            tv_endTime.text = endTime

            if (showTip) {
                showToast("已自动校准时间")
                showTip = false
            }
            loadData(true)
        }
        tv_startTime.setOnClickListener {
            TimePickerDialog.Builder().apply {
                setCallBack { _, millseconds ->

                    if (!tv_endTime.text.toString().isEmpty()) {
                        var end = ZXTimeUtil.string2Millis(tv_endTime.text.toString(), "yyyy-MM-dd")

                        if (millseconds > end) {
                            showToast("开始时间不能大于结束时间")
                            return@setCallBack
                        }
                    }
                    tv_startTime.text = ZXTimeUtil.getTime(millseconds)
                }
                setCyclic(true)
                setMaxMillseconds(System.currentTimeMillis())
                setThemeColor(ContextCompat.getColor(mContext!!, R.color.colorPrimary))
                setType(Type.YEAR_MONTH_DAY)
                setWheelItemTextNormalColor(ContextCompat.getColor(mContext!!, R.color.text_color_light))
                setWheelItemTextSelectorColor(ContextCompat.getColor(mContext!!, R.color.text_color_normal))
                setTitleStringId("请选择时间")

            }.build().show(supportFragmentManager, "time_select")

        }
        tv_endTime.setOnClickListener {
            TimePickerDialog.Builder().apply {
                setCallBack { _, millseconds ->

                    if (!tv_startTime.text.toString().isEmpty()) {
                        var start = ZXTimeUtil.string2Millis(tv_startTime.text.toString(), "yyyy-MM-dd")

                        if (millseconds < start) {
                            showToast("结束时间不能小于开始时间")
                            return@setCallBack
                        }
                    }
                    tv_endTime.text = ZXTimeUtil.getTime(millseconds)
                }
                setCyclic(true)
                setMaxMillseconds(System.currentTimeMillis())
                setThemeColor(ContextCompat.getColor(mContext!!, R.color.colorPrimary))
                setType(Type.YEAR_MONTH_DAY)
                setWheelItemTextNormalColor(ContextCompat.getColor(mContext!!, R.color.text_color_light))
                setWheelItemTextSelectorColor(ContextCompat.getColor(mContext!!, R.color.text_color_normal))
                setTitleStringId("请选择时间")

            }.build().show(supportFragmentManager, "time_select")
        }
    }

    override fun onPageGroupdefensesReportRateResult(list: NormalList<ReportStatisGroupDetailBean>?) {
        if (list != null) {
            rv_info_list.refreshData(list.result!!, list.totalRecords)
        } else {
            adapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData(true) }
        }

//        rv_info_list.stopRefresh()
//        if (list != null) {
//            rv_info_list.setLoadInfo(list.totalRecords)
//            dataBeans.clear()
//            dataBeans.addAll(list.result!!)
//            adapter.notifyDataSetChanged()
//        } else {
//            adapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData() }
//            rv_info_list.setLoadInfo(0)
//            dataBeans.clear()
//            adapter.notifyDataSetChanged()
//        }

    }
}

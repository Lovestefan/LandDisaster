package com.zx.landdisaster.module.disaster.ui

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
import com.zx.landdisaster.module.disaster.bean.ReportZhuShouDetailBean
import com.zx.landdisaster.module.disaster.func.adapter.ReportStatisZhuShouDetailAdapter
import com.zx.landdisaster.module.disaster.func.adapter.ReportStatisZhuShouTongJiDetailAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.ReportZhuShouDetailContract
import com.zx.landdisaster.module.disaster.mvp.model.ReportZhuShouDetailModel
import com.zx.landdisaster.module.disaster.mvp.presenter.ReportZhuShouDetailPresenter
import com.zx.landdisaster.module.worklog.bean.WorkLogListBean
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_report_zhu_shou_detail.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Create By admin On 2017/7/11
 * 功能：驻守日志-详情
 */
class ReportZhuShouDetailActivity : BaseActivity<ReportZhuShouDetailPresenter, ReportZhuShouDetailModel>(), ReportZhuShouDetailContract.View {

    val dataBeans = arrayListOf<ReportZhuShouDetailBean>()
    private val adapter = ReportStatisZhuShouDetailAdapter(dataBeans)
    var pageNo = 1


    val dataBeans1 = arrayListOf<WorkLogListBean>()
    private val adapter1 = ReportStatisZhuShouTongJiDetailAdapter(dataBeans1)
    var pageNo1 = 1

    var startTime = ""
    var endTime = ""

    var areacode = ""//所选区县
    var recorder = ""

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, areaCode: String, title: String, startTime: String, endTime: String) {
            val intent = Intent(activity, ReportZhuShouDetailActivity::class.java)
            intent.putExtra("code", areaCode)
            intent.putExtra("title", title)
            intent.putExtra("startTime", startTime)
            intent.putExtra("endTime", endTime)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_report_zhu_shou_detail
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        areacode = intent.getStringExtra("code")
        startTime = intent.getStringExtra("startTime") + " 00:00:00"
        endTime = intent.getStringExtra("endTime") + " 23:59:59"
        var title = intent.getStringExtra("title")

        tbv_title.setMidText("驻守日志-$title")
        tv_startTime.text = startTime
        tv_endTime.text = endTime


        rv_info_list.setLayoutManager(LinearLayoutManager(this))
                .setAdapter(adapter)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<ReportZhuShouDetailBean> {
                    override fun onItemLongClick(item: ReportZhuShouDetailBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()

                    }

                    override fun onRefresh() {
                        loadData(true)
                    }

                    override fun onItemClick(item: ReportZhuShouDetailBean?, position: Int) {
                    }

                })
        loadData(true)

        rv_showList.setLayoutManager(LinearLayoutManager(this))
                .setAdapter(adapter1)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<WorkLogListBean> {
                    override fun onItemLongClick(item: WorkLogListBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo1++
                        getlistData()

                    }

                    override fun onRefresh() {
                        getlistData(true)
                    }

                    override fun onItemClick(item: WorkLogListBean?, position: Int) {
                    }

                })
    }

    var showTip = false
    fun getWeekDay(seconds: Long, week: Int = 1, show: Boolean = false, isUpWeek: Boolean = false): String {//week==1 获取本周日，week=2 获取本周一,是否是上周
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
        if (isUpWeek) now -= 86400000 * 7
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
        mPresenter.pageGarrisonreportrateDetail(ApiParamUtil.getReportstatis(pageNo = pageNo, pageSize = pageSize, areacode = areacode, startTime = startTime, endTime = endTime))
    }

    fun getlistData(refresh: Boolean = false) {
        if (refresh) {
            pageNo1 = 1
            rv_showList.clearStatus()
        }
        mPresenter.findWorkdairy(ApiParamUtil.getReportstatis(pageNo = pageNo1, startTime = startTime, endTime = endTime,
                recorder = recorder, areacode = areacode))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        tv_showLayout_ok.setOnClickListener {
            rl_showLayout.visibility = View.GONE
        }
        adapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.tv_sjsb) {//有效上报
                showLoading("正在获取数据...")
                recorder = dataBeans[position].personid
                getlistData()
                rl_showLayout.visibility = View.VISIBLE
            }
        }
        //重置
        tv_clear.setOnClickListener {
            var start = ZXTimeUtil.getTime(System.currentTimeMillis(), SimpleDateFormat("yyyy-MM-dd"))
            var end = start

            var isWeek = true
            if (!tv_startTime.text.toString().isEmpty()) {
                start = tv_startTime.text.toString()
                isWeek = false
            }
            startTime = getWeekDay(ZXTimeUtil.string2Millis(start, "yyyy-MM-dd"), 2, true, isWeek) + " 00:00:00"

            if (!tv_endTime.text.toString().isEmpty()) {
                end = tv_endTime.text.toString()
            }
            endTime = getWeekDay(ZXTimeUtil.string2Millis(end, "yyyy-MM-dd"), 1, true) + " 23:59:59"


            tv_startTime.text = startTime
            tv_endTime.text = endTime
            loadData(true)
        }
        //搜索
        tv_search.setOnClickListener {
            startTime = getWeekDay(ZXTimeUtil.string2Millis(tv_startTime.text.toString(), "yyyy-MM-dd"), 2, true) + " 00:00:00"
            endTime = getWeekDay(ZXTimeUtil.string2Millis(tv_endTime.text.toString(), "yyyy-MM-dd"), 1, true) + " 23:59:59"

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

    override fun onFindWorkdairyResult(list: NormalList<WorkLogListBean>?) {
        dismissLoading()

        if (list != null) {
            rv_showList.refreshData(list.result!!, list.totalRecords)
        } else {
            adapter1.emptyView = EmptyViewTool.getInstance(this).getEmptyView { getlistData(true) }
        }

//
//        rv_showList.stopRefresh()
//        if (list != null) {
//            rv_showList.setLoadInfo(list.totalRecords)
//            dataBeans1.clear()
//            dataBeans1.addAll(list.result!!)
//            adapter1.notifyDataSetChanged()
//        } else {
//            adapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData() }
//            rv_showList.setLoadInfo(0)
//            dataBeans1.clear()
//            adapter1.notifyDataSetChanged()
//        }
    }

    override fun onPageGarrisonreportrateDetailResult(list: NormalList<ReportZhuShouDetailBean>?) {

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

    override fun onBackPressed() {
        if (rl_showLayout.visibility == View.VISIBLE) {
            rl_showLayout.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }
}

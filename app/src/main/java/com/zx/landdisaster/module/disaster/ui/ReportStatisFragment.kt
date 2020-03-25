package com.zx.landdisaster.module.disaster.ui

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import com.jzxiang.pickerview.TimePickerDialog
import com.jzxiang.pickerview.data.Type
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.disaster.bean.ReportStatisGroupdeBean
import com.zx.landdisaster.module.disaster.bean.ReportstatisBean
import com.zx.landdisaster.module.disaster.func.adapter.ReportStatisAdapter
import com.zx.landdisaster.module.disaster.func.adapter.ReportStatisGroupdeAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.ReportStatisContract
import com.zx.landdisaster.module.disaster.mvp.model.ReportStatisModel
import com.zx.landdisaster.module.disaster.mvp.presenter.ReportStatisPresenter
import com.zx.landdisaster.module.system.bean.AreaBean
import com.zx.zxutils.entity.KeyValueEntity
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.fragment_report_statis.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Create By admin On 2017/7/11
 * 功能：上爆率统计
 */
class ReportStatisFragment : BaseFragment<ReportStatisPresenter, ReportStatisModel>(), ReportStatisContract.View {

    val dataBeans = arrayListOf<ReportstatisBean>()
    private var adapter = ReportStatisAdapter(dataBeans)
    val dataBeans1 = arrayListOf<ReportStatisGroupdeBean>()
    private val adapter1 = ReportStatisGroupdeAdapter(dataBeans1)//监测数据
    var pageNo = 1
    var sbType = "1"//1：区县日报，2：驻守日志，3：片区周报，4：监测数据

    var startTime = ""
    var endTime = ""

    var areacode = ""//所选区县
    val areaList = arrayListOf<KeyValueEntity>()

    companion object {
        /**
         * 启动器
         */
        fun newInstance(): ReportStatisFragment {
            val fragment = ReportStatisFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_report_statis
    }

    fun setType(t: String) {
        sbType = t
        adapter.setTypes(sbType)
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setDefaultTime(System.currentTimeMillis())

        rv_info_list.setLayoutManager(LinearLayoutManager(activity!!))
        if (sbType != "4") {
            rv_info_list.setAdapter(adapter)
                    .setPageSize(pageSize)
                    .autoLoadMore()
                    .setSRListener(object : ZXSRListener<ReportstatisBean> {
                        override fun onItemLongClick(item: ReportstatisBean?, position: Int) {

                        }

                        override fun onLoadMore() {
                            pageNo++
                            loadData()

                        }

                        override fun onRefresh() {
                            loadData(true)
                        }

                        override fun onItemClick(item: ReportstatisBean?, position: Int) {
                        }

                    })
        } else {
            rv_info_list.setAdapter(adapter1)
                    .setPageSize(pageSize)
                    .autoLoadMore()
                    .setSRListener(object : ZXSRListener<ReportStatisGroupdeBean> {
                        override fun onItemLongClick(item: ReportStatisGroupdeBean?, position: Int) {

                        }

                        override fun onLoadMore() {
                            pageNo++
                            loadData()

                        }

                        override fun onRefresh() {
                            loadData(true)
                        }

                        override fun onItemClick(item: ReportStatisGroupdeBean?, position: Int) {
                            ReportstatisDetailActivity.startAction(activity!!, false, item!!.streetcode, item!!.streetname)
                        }

                    })
        }

        loadData(true)
        mPresenter.getFindByParent(ApiParamUtil.findByParent("500"))
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
        //sbType = "1"//1：区县日报，2：驻守日志，3：片区周报，4：监测数据
        if (sbType == "1") {
            mPresenter.countReports(ApiParamUtil.getReportstatis(pageNo = pageNo, pageSize = pageSize, areacode = areacode, startTime = startTime, endTime = endTime))
        } else if (sbType == "2") {
            mPresenter.pageGarrisonreportrate(ApiParamUtil.getReportstatis(pageNo = pageNo, pageSize = pageSize, areacode = areacode, startTime = startTime, endTime = endTime))
        } else if (sbType == "3") {
            mPresenter.pageAreaManagerreportrate(ApiParamUtil.getReportstatis(pageNo = pageNo, pageSize = pageSize, areacode = areacode, startTime = startTime, endTime = endTime))
        } else if (sbType == "4") {
            mPresenter.pageGroupdefensesreportrate(ApiParamUtil.getReportstatis(pageNo = pageNo, pageSize = pageSize, areacode = areacode, startTime = startTime, endTime = endTime))
        }
    }

    fun setDefaultTime(seconds: Long) {
        if (sbType == "1") {//默认月初
            endTime = ZXTimeUtil.getTime(seconds, SimpleDateFormat("yyyy-MM-dd"))
            startTime = endTime.substring(0, 8) + "01"
        } else if (sbType == "2") {
            startTime = getWeekDay(seconds, 2, isUpWeek = true)
            endTime = getWeekDay(seconds, 1, isUpWeek = true)
        } else if (sbType == "3") {
            startTime = ""
            endTime = ""
        } else {
            startTime = ""
            endTime = ""
        }

        tv_startTime.text = startTime
        tv_endTime.text = endTime
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        adapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.tv_ysb) {//已上报
                if (sbType == "1") {//区县日报
                    DailyReportListActivity.startAction(activity!!, false, dataBeans[position].areacode, dataBeans[position].areaname,
                            startTime, endTime)
                } else if (sbType == "2") {//驻守日志
                    ReportZhuShouDetailActivity.startAction(activity!!, false, dataBeans[position].streetcode,
                            dataBeans[position].streetname, startTime, endTime)
                } else if (sbType == "3") {//片区周报
                    ReportPianQuDetailActivity.startAction(activity!!, false, dataBeans[position].streetcode, dataBeans[position].streetname, startTime, endTime)
                } else {
                    showToast("功能正在开发中...")
                }
            } else if (view.id == R.id.tv_wsb) {//未上报
                if (sbType == "1") {//区县上报
                    showLoading("正在获取数据...")
                    mPresenter.findNoReportTime(ApiParamUtil.getReportstatis(startTime = startTime, endTime = endTime, areacode = dataBeans[position].areacode))
                }
            }
        }
        //重置
        tv_clear.setOnClickListener {
            setDefaultTime(System.currentTimeMillis())
            areacode = ""
            tv_area.apply {
                setDefaultItem(0)
            }.build()
            loadData(true)
        }
        //搜索
        tv_search.setOnClickListener {
            if (sbType == "1") {//默认月初
                startTime = tv_startTime.text.toString()
                endTime = tv_endTime.text.toString()
            } else {
                if (!tv_startTime.text.toString().isEmpty())
                    startTime = getWeekDay(ZXTimeUtil.string2Millis(tv_startTime.text.toString(), "yyyy-MM-dd"), 2, true)
                if (!tv_endTime.text.toString().isEmpty())
                    endTime = getWeekDay(ZXTimeUtil.string2Millis(tv_endTime.text.toString(), "yyyy-MM-dd"), 1, true)
            }
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

            }.build().show(childFragmentManager, "time_select")

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

            }.build().show(childFragmentManager, "time_select")
        }
    }

    override fun onFindNoReportTime(list: List<String>?) {
        dismissLoading()
        if (list != null) {
            ZXDialogUtil.showListDialog(activity!!, "未上报时间统计", "确定", list, null)
        } else {
            ZXDialogUtil.showInfoDialog(activity!!, "未上报时间统计", "没有数据")
        }
    }

    override fun onReportStatisResult(list: NormalList<ReportstatisBean>?) {
        if (list != null) {
            rv_info_list.refreshData(list.result!!, list.totalRecords)
        } else {
            adapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData(true) }
        }

//        rv_info_list.stopRefresh()
//        if (list != null) {
//            rv_info_list.setLoadInfo(list.totalRecords)
//            dataBeans.clear()
//            dataBeans.addAll(list.result!!)
//            rv_info_list.recyclerView.scrollToPosition(0)
//            adapter.notifyDataSetChanged()
//        } else {
//            adapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
//            rv_info_list.setLoadInfo(0)
//            dataBeans.clear()
//            adapter.notifyDataSetChanged()
//        }

    }

    override fun onPageGroupdefensesreportrate(list: NormalList<ReportStatisGroupdeBean>?) {
        if (list != null) {
            rv_info_list.refreshData(list.result!!, list.totalRecords)
        } else {
            adapter1.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData(true) }
        }
//        rv_info_list.stopRefresh()
//        if (list != null) {
//            rv_info_list.setLoadInfo(list.totalRecords)
//            dataBeans1.clear()
//            dataBeans1.addAll(list.result!!)
//            adapter1.notifyDataSetChanged()
//            rv_info_list.recyclerView.scrollToPosition(0)
//        } else {
//            adapter1.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
//            rv_info_list.setLoadInfo(0)
//            dataBeans1.clear()
//            adapter1.notifyDataSetChanged()
//        }
    }

    override fun onFindByParentResult(areaBean: List<AreaBean>?) {
        if (areaBean != null) {
            areaList.add(KeyValueEntity("", ""))
            for (i in 0 until areaBean.size) {
                areaList.add(KeyValueEntity(areaBean[i].name, areaBean[i].code))
            }
            tv_area.apply {
                setItemHeightDp(30)
                setData(areaList)
                setDefaultItem(0)
                setItemTextSizeSp(17)
                showSelectedTextColor(true, R.color.colorPrimary)
                showUnderineColor(false)
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        areacode = areaList[position].getValue().toString()
                    }
                }
            }.build()
        }
    }
}

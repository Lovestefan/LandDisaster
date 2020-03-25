package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import com.jzxiang.pickerview.TimePickerDialog
import com.jzxiang.pickerview.data.Type
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.disaster.func.adapter.DailyReportListAdapter

import com.zx.landdisaster.module.disaster.mvp.contract.DailyReportListContract
import com.zx.landdisaster.module.disaster.mvp.model.DailyReportListModel
import com.zx.landdisaster.module.disaster.mvp.presenter.DailyReportListPresenter
import com.zx.landdisaster.module.system.bean.AreaBean
import com.zx.landdisaster.module.worklog.bean.DailyListBean
import com.zx.landdisaster.module.worklog.ui.DailyReportDetailActivity
import com.zx.zxutils.entity.KeyValueEntity
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_daily_report_list.*
import java.text.SimpleDateFormat


/**
 * Create By admin On 2017/7/11
 * 功能：上报率列表-区县日报-上报列表
 */
class DailyReportListActivity : BaseActivity<DailyReportListPresenter, DailyReportListModel>(), DailyReportListContract.View {

    private val dataBeans = arrayListOf<DailyListBean>()
    private val listAdapter = DailyReportListAdapter(dataBeans)

    var areaName = ""
    var areacode = ""
    var status = "1"//日报状态，默认有效1有效，0正在审核中，-1驳回
    val areaList = arrayListOf<KeyValueEntity>()
    val statusList = arrayListOf<KeyValueEntity>()

    var pageNo = 1
    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, areaCode: String, areaName: String, startTime: String, endTime: String) {
            val intent = Intent(activity, DailyReportListActivity::class.java)
            intent.putExtra("areaCode", areaCode)
            intent.putExtra("areaName", areaName)
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
        return R.layout.activity_daily_report_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)


        tv_startTime.text = intent.getStringExtra("startTime") + " 00:00:00"
        tv_endTime.text = intent.getStringExtra("endTime") + ZXTimeUtil.getTime(System.currentTimeMillis(), SimpleDateFormat(" HH:mm:ss"))
        areacode = intent.getStringExtra("areaCode")
        areaName = intent.getStringExtra("areaName")
        mPresenter.getFindByParent(ApiParamUtil.findByParent("500"))

        statusList.add(KeyValueEntity("有效", "1"))
        statusList.add(KeyValueEntity("正在审核", "0"))
        statusList.add(KeyValueEntity("驳回", "-1"))

        tv_status.apply {
            setItemHeightDp(30)
            setData(statusList)
            setDefaultItem(0)
            setItemTextSizeSp(13)
            showSelectedTextColor(true, R.color.colorPrimary)
            showUnderineColor(false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    status = statusList[position].getValue().toString()
                }
            }
        }.build()
        rv_daily_list.setLayoutManager(LinearLayoutManager(this))
                .setAdapter(listAdapter)
                .setPageSize(pageSize)
                .autoLoadMore()
//                .showLoadInfo(true)
                .setSRListener(object : ZXSRListener<DailyListBean> {
                    override fun onItemLongClick(item: DailyListBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()
                    }

                    override fun onRefresh() {
                        pageNo = 1
                        loadData()
                    }

                    override fun onItemClick(item: DailyListBean?, position: Int) {
                        DailyReportDetailActivity.startAction(this@DailyReportListActivity, false, dataBeans[position],"1")
                    }

                })
        loadData()
    }

    fun loadData(reload: Boolean = false) {
        if (reload) {
            pageNo = 1
            rv_daily_list.clearStatus()
        }
        mPresenter.getDailyList(ApiParamUtil.dailyListParam(pageNo = pageNo, pageSize = pageSize, startTime = tv_startTime.text.toString(), endTime = tv_endTime.text.toString(),
                areacode = areacode, status = status))
    }

    override fun onDailyListResult(list: NormalList<DailyListBean>?) {
        if (list != null) {
            rv_daily_list.refreshData(list.result!!, list.totalRecords)
        } else {
            listAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData() }
        }
//        rv_daily_list.stopRefresh()
//        if (dailyList != null) {
//            rv_daily_list.setLoadInfo(dailyList.size)
//            dataBeans.clear()
//            dataBeans.addAll(dailyList)
//            listAdapter.notifyDataSetChanged()
//            rv_daily_list.recyclerView.scrollToPosition(0)
//        } else {
//            listAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData() }
//            rv_daily_list.setLoadInfo(0)
//            dataBeans.clear()
//            listAdapter.notifyDataSetChanged()
//        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //重置
        tv_clear.setOnClickListener {
            var nowTime = ZXTimeUtil.getTime(System.currentTimeMillis(), SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))

            tv_startTime.text = nowTime.substring(0, 8) + "01 00:00:00"
            tv_endTime.text = nowTime
            areacode = ""
            status = "1"
            tv_area.apply {
                setDefaultItem(0)
            }.build()
            tv_status.apply {
                setDefaultItem(0)
            }.build()
            loadData()
        }
        //搜索
        tv_search.setOnClickListener {

            loadData()

        }
        tv_startTime.setOnClickListener {
            TimePickerDialog.Builder().apply {
                setCallBack { _, millseconds ->

                    if (!tv_endTime.text.toString().isEmpty()) {
                        var end = ZXTimeUtil.string2Millis(tv_endTime.text.toString(), "yyyy-MM-dd HH:mm:ss")

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
                setType(Type.ALL)
                setWheelItemTextNormalColor(ContextCompat.getColor(mContext!!, R.color.text_color_light))
                setWheelItemTextSelectorColor(ContextCompat.getColor(mContext!!, R.color.text_color_normal))
                setTitleStringId("请选择时间")

            }.build().show(supportFragmentManager, "time_select")

        }
        tv_endTime.setOnClickListener {
            TimePickerDialog.Builder().apply {
                setCallBack { _, millseconds ->

                    if (!tv_startTime.text.toString().isEmpty()) {
                        var start = ZXTimeUtil.string2Millis(tv_startTime.text.toString(), "yyyy-MM-dd HH:mm:ss")

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
                setType(Type.ALL)
                setWheelItemTextNormalColor(ContextCompat.getColor(mContext!!, R.color.text_color_light))
                setWheelItemTextSelectorColor(ContextCompat.getColor(mContext!!, R.color.text_color_normal))
                setTitleStringId("请选择时间")

            }.build().show(supportFragmentManager, "time_select")
        }
    }

    override fun onFindByParentResult(areaBean: List<AreaBean>?) {
        if (areaBean != null) {
            var index = 0
            areaList.add(KeyValueEntity("所有区县", ""))
            for (i in 0 until areaBean.size) {
                areaList.add(KeyValueEntity(areaBean[i].name, areaBean[i].code))
                if (areaBean[i].name == areaName) index = i + 1
            }
            tv_area.apply {
                setItemHeightDp(30)
                setData(areaList)
                setDefaultItem(index)
                setItemTextSizeSp(13)
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

package com.zx.landdisaster.module.areamanager.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jzxiang.pickerview.TimePickerDialog
import com.jzxiang.pickerview.data.Type
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.areamanager.bean.WeekWorkListBean
import com.zx.landdisaster.module.areamanager.func.WeekWorkListAdapter
import com.zx.landdisaster.module.areamanager.mvp.contract.WeekWorkListContract
import com.zx.landdisaster.module.areamanager.mvp.model.WeekWorkListModel
import com.zx.landdisaster.module.areamanager.mvp.presenter.WeekWorkListPresenter
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_week_work_list.*
import java.text.SimpleDateFormat


/**
 * 功能：周报上报情况
 */
class WeekWorkListActivity : BaseFragment<WeekWorkListPresenter, WeekWorkListModel>(), WeekWorkListContract.View {
    val dataBeans = arrayListOf<WeekWorkListBean>()
    private val listAdapter = WeekWorkListAdapter(dataBeans)

    var pageNo = 1

    companion object {
        /**
         * 启动器
         */
        fun newInstance(): WeekWorkListActivity {
            val fragment = WeekWorkListActivity()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_week_work_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

//        sr_weekwork_list.apply {
//            layoutManager = LinearLayoutManager(this@WeekWorkListActivity)
//            adapter = listAdapter
//        }

        sr_weekwork_list.setLayoutManager(LinearLayoutManager(activity))
                .setAdapter(listAdapter)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<WeekWorkListBean> {
                    override fun onItemLongClick(item: WeekWorkListBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()
                    }

                    override fun onRefresh() {
                        pageNo = 1
                        loadData()
                    }

                    override fun onItemClick(item: WeekWorkListBean?, position: Int) {
                        WeekWorkDetail.startAction(activity!!, false, dataBeans[position])
                    }

                })
        loadData()
    }

    /**
     * 数据加载
     */
    @SuppressLint("SimpleDateFormat")
    fun loadData(reload: Boolean = false) {
        if (reload) {
            pageNo = 1
            sr_weekwork_list.clearStatus()
        }

        var startTime = "2017-1-1 00:00:00"
        var endTime = ZXTimeUtil.getCurrentTime(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))

        if ("开始时间" != tv_start_time.text) {
            startTime = tv_start_time.text.toString()
        }
        if ("结束时间" != tv_end_time.text) {
            endTime = tv_end_time.text.toString()
        }

        mPresenter.getWeekWorkListData(ApiParamUtil.getWeekWorkList(pageNo, pageSize, UserManager.getUser().currentUser!!.name, startTime, endTime))
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

            }.build().show(fragmentManager, "time_select")

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

            }.build().show(fragmentManager, "time_select")
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
            loadData()
        }
    }

    override fun onWeekWorkListResult(list: NormalList<WeekWorkListBean>?) {
        if (list != null) {
            sr_weekwork_list.refreshData(list.result!!, list.totalRecords)
        } else {
            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
        }
//        sr_weekwork_list.stopRefresh()
//        if (weekWorkList != null) {
//            sr_weekwork_list.setLoadInfo(weekWorkList.result!!.size)
//            dataBeans.clear()
//            dataBeans.addAll(weekWorkList.result!!)
//            listAdapter.notifyDataSetChanged()
//        } else {
//            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
//            sr_weekwork_list.setLoadInfo(0)
//            dataBeans.clear()
//            listAdapter.notifyDataSetChanged()
//        }
    }

}

package com.zx.landdisaster.module.worklog.ui

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
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.worklog.bean.WorkLogListBean
import com.zx.landdisaster.module.worklog.func.adapter.WorkLogListAdapter
import com.zx.landdisaster.module.worklog.mvp.contract.WorkLogListContract
import com.zx.landdisaster.module.worklog.mvp.model.WorkLogListModel
import com.zx.landdisaster.module.worklog.mvp.presenter.WorkLogListPresenter
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.fragment_work_log_list.*
import java.text.SimpleDateFormat

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class WorkLogListFragment : BaseFragment<WorkLogListPresenter, WorkLogListModel>(), WorkLogListContract.View {

    private val dataBeans = arrayListOf<WorkLogListBean>()
    private val listAdapter = WorkLogListAdapter(dataBeans)

    private var type = 0
    var pageNo = 1

    companion object {
        /**
         * 启动器
         */
        fun newInstance(type: Int): WorkLogListFragment {
            val fragment = WorkLogListFragment()
            val bundle = Bundle()
            bundle.putInt("type", type)
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_work_log_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
//        rv_worklog_list.apply {
//            layoutManager = LinearLayoutManager(activity!!)
//            adapter = listAdapter
//        }
        rv_worklog_list.setLayoutManager(LinearLayoutManager(activity!!))
                .setAdapter(listAdapter)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<WorkLogListBean> {
                    override fun onItemLongClick(item: WorkLogListBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        if (type == 0) {
                            rv_worklog_list.stopRefresh()
                        } else {
                            pageNo++
                            loadData()
                        }

                    }

                    override fun onRefresh() {
                        if (type == 0) {
                            rv_worklog_list.stopRefresh()
                        } else {
                            pageNo = 1
                            loadData()
                        }
                    }

                    override fun onItemClick(item: WorkLogListBean?, position: Int) {
                        if (item != null) {
                            item.type = type
                            item.position = position
                            if (type == 0) {
                                WorkLogAddActivity.startAction(activity!!, false, item)
                            } else {
//                                WorkLogAddActivity.startAction(activity!!, false, item)
//                                WorkLogDetailActivity.startAction(activity!!, false, item)
                                mPresenter.getWorkLogFile(ApiParamUtil.getWeekWorkFileList(dataBeans[position].dairyid, 1), position)
                            }
                        }
                    }

                })
        type = arguments!!.getInt("type")
        if (type == 0) {
            ll_time_search.visibility = View.GONE
            dataBeans.clear()
            if (mSharedPrefUtil.contains("workLogBeanList") && mSharedPrefUtil.getList<WorkLogListBean>("workLogBeanList") != null) {
                dataBeans.addAll(mSharedPrefUtil.getList("workLogBeanList"))
            }
            listAdapter.notifyDataSetChanged()
        } else {
            loadData()
        }
    }

    fun loadData(reload: Boolean = false) {
        if (reload) {
            pageNo = 1
            rv_worklog_list.clearStatus()
        }

        var startTime = "2017-1-1 00:00:00"
        var endTime = ZXTimeUtil.getCurrentTime(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))

        if ("开始时间" != tv_start_time.text) {
            startTime = tv_start_time.text.toString()
        }
        if ("结束时间" != tv_end_time.text) {
            endTime = tv_end_time.text.toString()
        }
        mPresenter.getWorkLogList(ApiParamUtil.workLogListParams(pageNo, pageSize, UserManager.getUser().currentUser!!.name, startTime, endTime))
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

            }.build().show(childFragmentManager, "time_select")

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

            }.build().show(childFragmentManager, "time_select")
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

    override fun onWorkLogFileResult(fileList: List<AuditReportFileBean>?, position: Int) {
        val addFileList = arrayListOf<AddFileBean>()
        if (fileList != null && fileList.isNotEmpty()) {
            if (fileList.isNotEmpty()) {
                fileList.forEach {
                    addFileList.add(AddFileBean.reSetType(it.type, it.name, it.path))
                }
            }
        }
        dataBeans[position].files = addFileList
        dataBeans[position].type = type
        WorkLogAddActivity.startAction(activity!!, false, dataBeans[position])
//        ZXFragmentUtil.addFragment(supportFragmentManager, WorkLogAddActivity.startAction(workLogBean), R.id.fm_work_log_detail)
    }

    override fun onWorkLogListResult(list: NormalList<WorkLogListBean>?) {
        if (list != null) {
            rv_worklog_list.refreshData(list.result!!, list.totalRecords)
        } else {
            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
        }
//        rv_worklog_list.stopRefresh()
//        if (workLogList != null) {
//            rv_worklog_list.setLoadInfo(workLogList.result!!.size)
//            dataBeans.clear()
//            dataBeans.addAll(workLogList.result!!)
//            listAdapter.notifyDataSetChanged()
//        } else {
//            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
//            rv_worklog_list.setLoadInfo(0)
//            dataBeans.clear()
//            listAdapter.notifyDataSetChanged()
//        }
    }

    override fun onResume() {
        super.onResume()
        if (type == 0) {
            dataBeans.clear()
            if (mSharedPrefUtil.contains("workLogBeanList") && mSharedPrefUtil.getList<WorkLogListBean>("workLogBeanList") != null) {
                dataBeans.addAll(mSharedPrefUtil.getList("workLogBeanList"))
            }
            listAdapter.notifyDataSetChanged()
        } else {
            loadData()
        }
    }
}

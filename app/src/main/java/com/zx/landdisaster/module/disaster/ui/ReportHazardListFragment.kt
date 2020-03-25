package com.zx.landdisaster.module.disaster.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportListBean
import com.zx.landdisaster.module.disaster.func.adapter.ReportListAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.ReportHazardListContract
import com.zx.landdisaster.module.disaster.mvp.model.ReportHazradListModel
import com.zx.landdisaster.module.disaster.mvp.presenter.ReportHazardListPresenter
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.fragment_report_record_list.*

/**
 * Create By admin On 2017/7/11
 * 功能：上报记录列表
 */
class ReportHazardListFragment : BaseFragment<ReportHazardListPresenter, ReportHazradListModel>(), ReportHazardListContract.View {

    val dataBeans = arrayListOf<ReportListBean>()
    val listAdapter = ReportListAdapter(dataBeans)

    var pageNo = 1

    companion object {
        /**
         * 启动器
         */
        fun newInstance(): ReportHazardListFragment {
            val fragment = ReportHazardListFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_report_record_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        sr_report_list.setLayoutManager(LinearLayoutManager(activity))
                .setAdapter(listAdapter)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<ReportListBean> {
                    override fun onItemLongClick(item: ReportListBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()
                    }

                    override fun onRefresh() {
                        pageNo = 1
                        loadData()
                    }

                    override fun onItemClick(item: ReportListBean?, position: Int) {
                        if (item != null) {
                            ReportDetailActivity.startAction(activity!!, false, item.reportdataid)
                        }
                    }

                })
        loadData()
    }

    /**
     * 数据加载
     */
    fun loadData(reload: Boolean = false) {
        if (reload) {
            pageNo = 1
            sr_report_list.clearStatus()
        }
        mPresenter.getReportList(ApiParamUtil.reportListParam(pageNo, pageSize))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && mPresenter != null) {
            loadData()
        }
    }

    /**
     * 查询列表
     */
    override fun onReportListResult(list: NormalList<ReportListBean>?) {
        uploadLog(302, 7, "查询灾险情上报记录")
        if (list != null) {
            sr_report_list.refreshData(list.result!!, list.totalRecords)
        } else {
            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
        }

//        sr_report_list.stopRefresh()
//        if (auditList == null) {
//            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData(true) }
//            sr_report_list.setLoadInfo(0)
//            dataBeans.clear()
//        } else if (auditList.result == null) {
//            sr_report_list.setLoadInfo(0)
//            dataBeans.clear()
//        } else {
//            sr_report_list.setLoadInfo(auditList.totalRecords)
//            dataBeans.clear()
//            dataBeans.addAll(auditList.result!!)
//            sr_report_list.recyclerView.scrollToPosition(0)
//        }
//        sr_report_list.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0x01) {
            loadData(true)
        }
    }
}

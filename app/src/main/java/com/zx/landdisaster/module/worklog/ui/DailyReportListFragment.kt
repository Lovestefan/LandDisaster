package com.zx.landdisaster.module.worklog.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.worklog.bean.DailyListBean
import com.zx.landdisaster.module.worklog.func.adapter.DailyListAdapter
import com.zx.landdisaster.module.worklog.mvp.contract.DailyReportListContract
import com.zx.landdisaster.module.worklog.mvp.model.DailyReportListModel
import com.zx.landdisaster.module.worklog.mvp.presenter.DailyReportListPresenter
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.fragment_daily_report_list.*


/**
 * Create By admin On 2017/7/11
 * 功能：日报记录
 */
class DailyReportListFragment : BaseFragment<DailyReportListPresenter, DailyReportListModel>(), DailyReportListContract.View {

    private val dataBeans = arrayListOf<DailyListBean>()
    private val listAdapter = DailyListAdapter(dataBeans)

    var pageNo = 1
    companion object {
        /**
         * 启动器
         */
        fun newInstance(): DailyReportListFragment {
            val fragment = DailyReportListFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_daily_report_list
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        uploadLog(302, 2, "查看日报记录")
        rv_daily_list.setLayoutManager(LinearLayoutManager(activity!!))
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
                        DailyReportDetailActivity.startAction(activity!!, false, dataBeans[position])
                    }

                })
        loadData()
    }

    fun loadData(reload: Boolean = false) {
        if (reload) {
            pageNo = 1
            rv_daily_list.clearStatus()
        }
        mPresenter.getDailyList(ApiParamUtil.dailyListParam(pageNo = pageNo, pageSize = pageSize))
        //personName = UserManager.getUser().currentUser!!.name
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
    }

    override fun onDailyListResult(list: NormalList<DailyListBean>?) {
        if (list != null) {
            rv_daily_list.refreshData(list.result!!, list.totalRecords)
        } else {
            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
        }
//        rv_daily_list.stopRefresh()
//        if (dailyList != null) {
//            rv_daily_list.setLoadInfo(dailyList.size)
//            dataBeans.clear()
//            dataBeans.addAll(dailyList)
//            listAdapter.notifyDataSetChanged()
//        } else {
//            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
//            rv_daily_list.setLoadInfo(0)
//            dataBeans.clear()
//            listAdapter.notifyDataSetChanged()
//        }
    }

}

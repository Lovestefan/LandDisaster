package com.zx.landdisaster.module.dailymanage.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.dailymanage.bean.ExpertDailyBean
import com.zx.landdisaster.module.dailymanage.mvp.contract.ExpertDailyListContract
import com.zx.landdisaster.module.dailymanage.mvp.model.ExpertDailyListModel
import com.zx.landdisaster.module.dailymanage.mvp.presenter.ExpertDailyListPresenter
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.fragment_expert_daily_list.*

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ExpertDailyListFragment : BaseFragment<ExpertDailyListPresenter, ExpertDailyListModel>(), ExpertDailyListContract.View {

    var pageNo = 1

    companion object {
        /**
         * 启动器
         */
        fun newInstance(): ExpertDailyListFragment {
            val fragment = ExpertDailyListFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_expert_daily_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

//        rv_info_list.setLayoutManager(LinearLayoutManager(activity))
//                .setAdapter(infoDeliveryAdapter)
//                .setPageSize(pageSize)
//                .autoLoadMore()
////                .showLoadInfo(true)
//                .setSRListener(object : ZXSRListener<ExpertDailyBean> {
//                    override fun onItemLongClick(item: ExpertDailyBean?, position: Int) {
//
//                    }
//
//                    override fun onLoadMore() {
//                        pageNo++
//                        loadData()
//                    }
//
//                    override fun onRefresh() {
//                        pageNo = 1
//                        loadData()
//                    }
//
//                    override fun onItemClick(item: ExpertDailyBean?, position: Int) {
////                        InfoReleaseInfoActivity.startAction(activity!!, false, item!!)
//                    }
//
//                })
//        loadData()
    }

    fun loadData(reload: Boolean = false) {
        if (reload) {
            pageNo = 1
            rv_info_list.clearStatus()
        }
        mPresenter.findMyReport(ApiParamUtil.findMyReport(pageNo = pageNo, pageSize = pageSize))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    override fun onFindMyReportResult(list: NormalList<ExpertDailyBean>?) {
        if (list != null) {
            rv_info_list.refreshData(list.result!!, list.totalRecords)
        } else {
//            infoDeliveryAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
        }

    }
}

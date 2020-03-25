package com.zx.landdisaster.module.disaster.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.disaster.bean.AuditListBean
import com.zx.landdisaster.module.disaster.func.adapter.AuditListAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.AuditListContract
import com.zx.landdisaster.module.disaster.mvp.model.AuditListModel
import com.zx.landdisaster.module.disaster.mvp.presenter.AuditListPresenter
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.fragment_audit_list.*

/**
 * Create By admin On 2017/7/11
 * 功能：审批列表
 */
class AuditListFragment : BaseFragment<AuditListPresenter, AuditListModel>(), AuditListContract.View {

    val dataBeans = arrayListOf<AuditListBean>()
    val listAdapter = AuditListAdapter(dataBeans)

    private var item: AuditListBean? = null

    var pageNo = 1

    companion object {
        /**
         * 启动器
         */
        fun newInstance(type: Int): AuditListFragment {
            val fragment = AuditListFragment()
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
        return R.layout.fragment_audit_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        sr_audit_list.setLayoutManager(LinearLayoutManager(activity))
                .setAdapter(listAdapter)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<AuditListBean> {
                    override fun onItemLongClick(item: AuditListBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()
                    }

                    override fun onRefresh() {
                        pageNo = 1
                        loadData()
                    }

                    override fun onItemClick(item: AuditListBean?, position: Int) {
                        if (item != null) {
                            this@AuditListFragment.item = item
                            if (UserManager.getUser().personRole.dispatch || UserManager.getUser().personRole.leader) {
                                ReportDetailActivity.startAction(activity!!, false, "", item)
                            } else {
                                val path = ApiParamUtil.auditDetailParam(item.auditKind, item.flownum, item.auditid, item.reportdataid)
                                ReportDetailActivity.startAction(activity!!, false, path, item)
                            }
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
            sr_audit_list.clearStatus()
        }
        mPresenter.getAuditList(ApiParamUtil.auditListParam(pageNo = pageNo, hazardtype = arguments!!.getInt("type")))
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

    override fun onAuditListResult(list: NormalList<AuditListBean>?) {
        if (list != null) {
            sr_audit_list.refreshData(list.result!!, list.totalRecords)
        } else {
            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
        }
//        sr_audit_list.stopRefresh()
//        if (auditList == null) {
//            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData(true) }
//            sr_audit_list.setLoadInfo(0)
//            dataBeans.clear()
//        } else if (auditList.result == null) {
//            sr_audit_list.setLoadInfo(0)
//            dataBeans.clear()
//        } else {
//            sr_audit_list.setLoadInfo(auditList.totalRecords)
//            dataBeans.clear()
//            dataBeans.addAll(auditList.result!!)
//            sr_audit_list.recyclerView.scrollToPosition(0)
//        }
//        sr_audit_list.notifyDataSetChanged()
    }
}

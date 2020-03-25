package com.zx.landdisaster.module.disaster.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.disaster.bean.AuditLogsBean
import com.zx.landdisaster.module.disaster.func.adapter.AuditLogsAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.AuditHandleLogsContract
import com.zx.landdisaster.module.disaster.mvp.model.AuditHandleLogsModel
import com.zx.landdisaster.module.disaster.mvp.presenter.AuditHandleLogsPresenter
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.fragment_audit_logs.*

/**
 * Create By admin On 2017/7/11
 * 功能：审核日志信息
 */
class AuditHandleLogsFragment : BaseFragment<AuditHandleLogsPresenter, AuditHandleLogsModel>(), AuditHandleLogsContract.View {

    val dataBeans = arrayListOf<AuditLogsBean>()
    val listAdapter = AuditLogsAdapter(dataBeans)

    companion object {
        /**
         * 启动器
         */
        fun newInstance(flowNum: String): AuditHandleLogsFragment {
            val fragment = AuditHandleLogsFragment()
            val bundle = Bundle()
            bundle.putString("flowNum", flowNum)
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_audit_logs
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
//        rv_audit_logs.apply {
//            layoutManager = LinearLayoutManager(activity!!)
//            adapter = listAdapter
//        }
        rv_audit_logs.setLayoutManager(LinearLayoutManager(activity!!))
                .setAdapter(listAdapter)
                .setPageSize(9999)
                .showLoadInfo(true)
                .setSRListener(object : ZXSRListener<AuditLogsBean> {
                    override fun onItemLongClick(item: AuditLogsBean?, position: Int) {

                    }

                    override fun onLoadMore() {

                    }

                    override fun onRefresh() {
                        loadData()
                    }

                    override fun onItemClick(item: AuditLogsBean?, position: Int) {
                    }

                })
        mPresenter.getAuditLogs(arguments!!.getString("flowNum"))

        uploadLog(303, 7, "查看灾险情审核日志列表")
    }

    fun loadData() {
        mPresenter.getAuditLogs(arguments!!.getString("flowNum"))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
    }

    /**
     * 日志回调
     */
    override fun onAuditLogsResult(logBeans: List<AuditLogsBean>?) {
//        dataBeans.clear()
//        dataBeans.addAll(logBeans)
//        listAdapter.notifyDataSetChanged()
        rv_audit_logs.stopRefresh()
        if (logBeans != null) {
            rv_audit_logs.setLoadInfo(logBeans.size)
            dataBeans.clear()
            dataBeans.addAll(logBeans)
            listAdapter.notifyDataSetChanged()
        } else {
            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
            rv_audit_logs.setLoadInfo(0)
            dataBeans.clear()
            listAdapter.notifyDataSetChanged()
        }
    }
}

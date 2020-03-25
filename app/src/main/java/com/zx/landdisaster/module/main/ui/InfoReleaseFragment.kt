package com.zx.landdisaster.module.main.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.tool.ActionLog
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean
import com.zx.landdisaster.module.main.func.adapter.InfoDelivery1Adapter
import com.zx.landdisaster.module.main.mvp.contract.InfoReleaseContract
import com.zx.landdisaster.module.main.mvp.model.InfoReleaseModel
import com.zx.landdisaster.module.main.mvp.presenter.InfoReleasePresenter
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.fragment_info_release.*

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class InfoReleaseFragment : BaseFragment<InfoReleasePresenter, InfoReleaseModel>(), InfoReleaseContract.View {
    val dataBeans = arrayListOf<InfoDeliveryBean>()
    private val infoDeliveryAdapter = InfoDelivery1Adapter(dataBeans)
    var pageNo = 1
    var servicetype = ""

    companion object {
        /**
         * 启动器
         */
        fun newInstance(): InfoReleaseFragment {
            val fragment = InfoReleaseFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_info_release
    }

    fun setType(type: String) {
        servicetype = type

    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        rv_info_list.setLayoutManager(LinearLayoutManager(activity))
                .setAdapter(infoDeliveryAdapter)
                .setPageSize(pageSize)
                .autoLoadMore()
//                .showLoadInfo(true)
                .setSRListener(object : ZXSRListener<InfoDeliveryBean> {
                    override fun onItemLongClick(item: InfoDeliveryBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()
                    }

                    override fun onRefresh() {
                        pageNo = 1
                        loadData()
                    }

                    override fun onItemClick(item: InfoDeliveryBean?, position: Int) {
                        InfoReleaseInfoActivity.startAction(activity!!, false, item!!)
                    }

                })

        var index = 0
        if (servicetype == "" || servicetype.split(",").size > 1) {
            index = 0
        } else if (servicetype == "7") {//上报率发布
            index = 4
        } else if (servicetype.toInt() < 4) {
            index = servicetype.toInt()
        } else {
            index = servicetype.toInt() + 1
        }

        uploadLog(307, index, "查看${ActionLog.id_307[index]}")
    }

    override fun onFindWeatherdecisionResult(list: NormalList<InfoDeliveryBean>?) {
        if (list != null) {
            rv_info_list.refreshData(list.result!!, list.totalRecords)
        } else {
            infoDeliveryAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
        }


//
//        rv_info_list.stopRefresh()
//        if (list != null) {
//            rv_info_list.setLoadInfo(list.result!!.size)
//            dataBeans.clear()
//            dataBeans.addAll(list.result!!)
//            infoDeliveryAdapter.notifyDataSetChanged()
//        } else {
//            infoDeliveryAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
//            rv_info_list.setLoadInfo(0)
//            dataBeans.clear()
//            infoDeliveryAdapter.notifyDataSetChanged()
//        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    fun loadData(reload: Boolean = false) {
        if (reload) {
            pageNo = 1
            rv_info_list.clearStatus()
        }
        mPresenter.getFindWeatherdecisionData(ApiParamUtil.getFindWeatherdecisionData(pageNo = pageNo, pageSize = pageSize, servicetype = servicetype))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

}

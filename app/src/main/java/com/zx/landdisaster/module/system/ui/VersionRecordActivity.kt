package com.zx.landdisaster.module.system.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.system.bean.VersionRecordBean
import com.zx.landdisaster.module.system.func.VersionRecordAdapter

import com.zx.landdisaster.module.system.mvp.contract.VersionRecordContract
import com.zx.landdisaster.module.system.mvp.model.VersionRecordModel
import com.zx.landdisaster.module.system.mvp.presenter.VersionRecordPresenter
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_version_record.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class VersionRecordActivity : BaseActivity<VersionRecordPresenter, VersionRecordModel>(), VersionRecordContract.View {

    val dataBeans = arrayListOf<VersionRecordBean>()
    private val listAdapter = VersionRecordAdapter(dataBeans)

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, VersionRecordActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_version_record
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        sr_list.setLayoutManager(LinearLayoutManager(this))
                .setAdapter(listAdapter)
                .setPageSize(9999)
                .showLoadInfo(true)
                .setSRListener(object : ZXSRListener<VersionRecordBean> {
                    override fun onItemLongClick(item: VersionRecordBean?, position: Int) {

                    }

                    override fun onLoadMore() {

                    }

                    override fun onRefresh() {
                        loadData()
                    }

                    override fun onItemClick(item: VersionRecordBean?, position: Int) {
                    }

                })
        loadData()
    }

    /**
     * 数据加载
     */
    fun loadData() {
        mPresenter.findUpdateinfoList()
    }

    override fun onVersionRecordResult(list: List<VersionRecordBean>?) {
        sr_list.stopRefresh()
        if (list != null) {
            sr_list.setLoadInfo(list.size)
            dataBeans.clear()
            dataBeans.addAll(list)
            listAdapter.notifyDataSetChanged()
        } else {
            listAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData() }
            sr_list.setLoadInfo(0)
            dataBeans.clear()
            listAdapter.notifyDataSetChanged()
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

}

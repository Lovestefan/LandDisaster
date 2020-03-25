package com.zx.landdisaster.module.groupdefense.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.groupdefense.bean.GroupDefenceBean
import com.zx.landdisaster.module.groupdefense.func.adapter.GroupDefenceAdapter
import com.zx.landdisaster.module.groupdefense.mvp.contract.GroupDefenceContract
import com.zx.landdisaster.module.groupdefense.mvp.model.GroupDefenceModel
import com.zx.landdisaster.module.groupdefense.mvp.presenter.GroupDefencePresenter
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.fragment_group_defence.*

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class GroupDefenceFragment : BaseActivity<GroupDefencePresenter, GroupDefenceModel>(), GroupDefenceContract.View {

    private val dataList = arrayListOf<GroupDefenceBean>()

    private val groupDefenceAdapter = GroupDefenceAdapter(dataList)

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, GroupDefenceFragment::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_group_defence
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        sr_group_defence.setLayoutManager(LinearLayoutManager(this))
                .setAdapter(groupDefenceAdapter)
                .setPageSize(9999)
                .showLoadInfo(true)
                .setSRListener(object : ZXSRListener<GroupDefenceBean> {
                    override fun onItemLongClick(item: GroupDefenceBean?, position: Int) {

                    }

                    override fun onLoadMore() {

                    }

                    override fun onRefresh() {
                        loadData()
                    }

                    override fun onItemClick(item: GroupDefenceBean?, position: Int) {
                    }

                })
        loadData()
    }

    private fun loadData() {
        mPresenter.findDisasterPoint()
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }


    override fun onfindDisasterPointResult(disasterList: List<GroupDefenceBean>?) {
        //disasterList!!.toMutableList().add(GroupDefenceBean(name = "测试灾害点"))
        sr_group_defence.stopRefresh()
        if (disasterList != null) {
            sr_group_defence.setLoadInfo(disasterList.size)
            dataList.clear()
            dataList.addAll(disasterList)
            sr_group_defence.notifyDataSetChanged()
        } else {
            groupDefenceAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData() }
            sr_group_defence.setLoadInfo(0)
            dataList.clear()
            sr_group_defence.notifyDataSetChanged()
        }
    }

    //RecyclerView ZXSwipeRecyView

}

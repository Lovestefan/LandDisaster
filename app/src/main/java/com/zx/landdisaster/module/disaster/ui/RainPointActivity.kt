package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.RainPointBean
import com.zx.landdisaster.module.disaster.func.adapter.RainPointListAdapter
import com.zx.landdisaster.module.disaster.func.adapter.RainPointTabAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.RainPointContract
import com.zx.landdisaster.module.disaster.mvp.model.RainPointModel
import com.zx.landdisaster.module.disaster.mvp.presenter.RainPointPresenter
import com.zx.landdisaster.module.main.bean.TimeListBean
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_rain_point.*
import java.util.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class RainPointActivity : BaseActivity<RainPointPresenter, RainPointModel>(), RainPointContract.View {

    private var pageNo = 1

    private val rainList = ArrayList<RainPointBean>()
    private var mAdapter = RainPointListAdapter(rainList)
    var type = 3
    private var timeList = arrayListOf<TimeListBean>()
    private val timeAdapter = RainPointTabAdapter(timeList)//时间选择适配器

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, type: Int) {
            val intent = Intent(activity, RainPointActivity::class.java)
            intent.putExtra("type", type)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_rain_point
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        type = intent.getIntExtra("type", 3)
        sr_point_rain.setLayoutManager(LinearLayoutManager(this))
                .setAdapter(mAdapter)
                .setPageSize(9999)
                .showLoadInfo(true)
                .setSRListener(object : ZXSRListener<RainPointBean> {
                    override fun onItemLongClick(item: RainPointBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()
                    }

                    override fun onRefresh() {
                        if (pageNo > 1) {
                            pageNo--
                        }
                        loadData()
                    }

                    override fun onItemClick(item: RainPointBean?, position: Int) {
//                        if (item != null) {
//                            HiddenPointActivity.startAction(this@RainPointActivity, false, item.pkiaa)
//                        }
                    }

                })

        rv_btn_timeList.apply {
            layoutManager = LinearLayoutManager(this@RainPointActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = timeAdapter
        }
        timeList.clear()
        timeList.add(TimeListBean(2, "1小时", "1", type == 1))
        timeList.add(TimeListBean(2, "3小时", "3", type == 3))
        timeList.add(TimeListBean(2, "6小时", "6", type == 6))
        timeList.add(TimeListBean(2, "12小时", "12", type == 12))
        timeList.add(TimeListBean(2, "24小时", "24", type == 24))
        timeList.add(TimeListBean(2, "3天", "72", type == 72))
        timeList.add(TimeListBean(2, "7天", "168", type == 168))

        loadData()
    }

    /**
     * 数据加载
     */
    fun loadData(reload: Boolean = false) {
        if (reload) {
            pageNo = 1
            sr_point_rain.clearStatus()
        }
        mPresenter.getRainPointList(ApiParamUtil.rainList(pageNo, type, ""))
    }

    override fun onRainResult(rainPointList: NormalList<RainPointBean>?) {
        sr_point_rain.stopRefresh()
        if (rainPointList == null) {
            mAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData() }
            sr_point_rain.setLoadInfo(0)
            toolbar_view.setMidText("雨量(0)")
            rainList.clear()
        } else if (rainPointList.result == null) {
            sr_point_rain.setLoadInfo(0)
            toolbar_view.setMidText("雨量(0)")
            rainList.clear()
        } else {
            sr_point_rain.setLoadInfo(rainPointList.totalRecords)
            toolbar_view.setMidText("雨量(${rainPointList.totalRecords})")
            rainList.clear()
            rainList.addAll(rainPointList.result!!)
            sr_point_rain.recyclerView.scrollToPosition(0)
        }
        sr_point_rain.notifyDataSetChanged()
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //时间列表
        timeAdapter.setOnItemClickListener { adapter, view, position ->
            val timeListBean = timeList[position]
            type = timeListBean.value.toInt()
            for (i in timeList.indices) {
                timeList[i].select = (i == position)
            }
            timeAdapter.notifyDataSetChanged()
            loadData()
        }
    }

}

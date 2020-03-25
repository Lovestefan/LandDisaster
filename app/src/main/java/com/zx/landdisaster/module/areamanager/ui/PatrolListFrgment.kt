package com.zx.landdisaster.module.areamanager.ui

import android.annotation.SuppressLint
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
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.areamanager.bean.PatrolListBean
import com.zx.landdisaster.module.areamanager.func.PatrolListAdapter
import com.zx.landdisaster.module.areamanager.mvp.contract.PatrolListContract
import com.zx.landdisaster.module.areamanager.mvp.model.PatrolListModel
import com.zx.landdisaster.module.areamanager.mvp.presenter.PatrolListPresenter
import com.zx.landdisaster.module.disaster.func.util.TextSizeUtil
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_patrol_list.*
import java.text.SimpleDateFormat


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class PatrolListFrgment : BaseFragment<PatrolListPresenter, PatrolListModel>(), PatrolListContract.View {

    val dataBeans = arrayListOf<PatrolListBean>()
    private val listAdapter = PatrolListAdapter(dataBeans)

    companion object {
        /**
         * 启动器
         */
        fun newInstance(): PatrolListFrgment {
            val fragment = PatrolListFrgment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_patrol_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val roleBean = UserManager.getUser().personRole
        if (!roleBean.groupDefense && !roleBean.areaManager) {
            TextSizeUtil.resetText(activity!!, et_pkiaa)
            TextSizeUtil.resetText(activity!!, tv_start_time)
            TextSizeUtil.resetText(activity!!, tv_end_time)
            TextSizeUtil.resetText(activity!!, tv_search)
        }

        sr_patvol_list.setLayoutManager(LinearLayoutManager(activity))
                .setAdapter(listAdapter)
                .setPageSize(9999)
                .showLoadInfo(true)
                .setSRListener(object : ZXSRListener<PatrolListBean> {
                    override fun onItemLongClick(item: PatrolListBean?, position: Int) {

                    }

                    override fun onLoadMore() {

                    }

                    override fun onRefresh() {
                        loadData()
                    }

                    override fun onItemClick(item: PatrolListBean?, position: Int) {
                        PatrolDetailActivity.startAction(activity!!, false, dataBeans[position])
                    }

                })
        loadData()
    }

    /**
     * 数据加载
     */
    @SuppressLint("SimpleDateFormat")
    fun loadData() {

        var startTime = "2017-1-1 00:00:00"
        var endTime = ZXTimeUtil.getCurrentTime(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))

        if ("开始时间" != tv_start_time.text) {
            startTime = tv_start_time.text.toString()
        }
        if ("结束时间" != tv_end_time.text) {
            endTime = tv_end_time.text.toString()
        }

        mPresenter.getPatrolListData(ApiParamUtil.getPatrolList(et_pkiaa.text.toString(), startTime, endTime))
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

            }.build().show(fragmentManager, "time_select")

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

            }.build().show(fragmentManager, "time_select")
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

    override fun onPatrolListResult(list: List<PatrolListBean>?) {
        uploadLog(302, 4, "查询巡查报告记录")
        sr_patvol_list.stopRefresh()
        if (list != null) {
            sr_patvol_list.setLoadInfo(list.size)
            dataBeans.clear()
            dataBeans.addAll(list)
            listAdapter.notifyDataSetChanged()
        } else {
            listAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
            sr_patvol_list.setLoadInfo(0)
            dataBeans.clear()
            listAdapter.notifyDataSetChanged()
        }
    }
}

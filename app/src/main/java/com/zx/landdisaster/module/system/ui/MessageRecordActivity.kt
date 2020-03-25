package com.zx.landdisaster.module.system.ui

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.jzxiang.pickerview.TimePickerDialog
import com.jzxiang.pickerview.data.Type
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.app.MyApplication
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.disaster.bean.AuditListBean
import com.zx.landdisaster.module.disaster.ui.ReportDetailActivity
import com.zx.landdisaster.module.main.bean.VersionBean
import com.zx.landdisaster.module.system.bean.MessageRecordBean
import com.zx.landdisaster.module.system.func.MessageRecordAdapter
import com.zx.landdisaster.module.system.mvp.contract.MessageRecordContract
import com.zx.landdisaster.module.system.mvp.model.MessageRecordModel
import com.zx.landdisaster.module.system.mvp.presenter.MessageRecordPresenter
import com.zx.zxutils.util.ZXAppUtil
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXSystemUtil
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_message_record.*
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：消息历史
 */
class MessageRecordActivity : BaseActivity<MessageRecordPresenter, MessageRecordModel>(), MessageRecordContract.View {

    val dataBeans = arrayListOf<MessageRecordBean>()
    private val listAdapter = MessageRecordAdapter(dataBeans)
    var pageNo = 1

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, MessageRecordActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_message_record
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        sr_list.setLayoutManager(LinearLayoutManager(this))
                .setAdapter(listAdapter)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<MessageRecordBean> {
                    override fun onItemLongClick(item: MessageRecordBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()

                    }

                    override fun onRefresh() {
                        loadData(true)
                    }

                    override fun onItemClick(item: MessageRecordBean?, position: Int) {
                        if (item?.noticeType != null) {
                            when (item.noticeType.toInt()) {
                                1 -> {//APP更新
                                    ZXDialogUtil.showYesNoDialog(this@MessageRecordActivity, item.title, item.content, "查询更新", "关闭", DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                                        mPresenter.getVerson()
                                    }, null)

                                    return
                                }
                                10 -> {//审核任务
                                    if (UserManager.canAudit()) {
                                        ZXDialogUtil.showYesNoDialog(this@MessageRecordActivity, item.title, item.content, "前往处理", "关闭", DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->

                                            if (item.reportdataid == null || item.auditid == null || item.auditKind == null || item.flownum == null) {
                                                showToast("缺少必要参数")
                                                return@OnClickListener
                                            }
                                            val listBean = AuditListBean(reportdataid = item.reportdataid, auditid = item.auditid,
                                                    auditKind = item.auditKind, flownum = item.flownum)
                                            ReportDetailActivity.startAction(this@MessageRecordActivity, false, "", listBean)
                                        }, null)
                                        return
                                    }
                                }
                                11 -> {//审阅任务
                                    if (UserManager.canPreview()) {
                                        ZXDialogUtil.showYesNoDialog(this@MessageRecordActivity, item.title, item.content, "前往处理", "关闭", DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                                            //                                            val reportdataid = if (jsonObj.has("reportdataid")) jsonObj.getString("reportdataid") else ""
//                                            val auditid = if (jsonObj.has("auditid")) jsonObj.getString("auditid") else ""
//                                            val flownum = if (jsonObj.has("flownum")) jsonObj.getString("flownum") else ""
//                                            val auditKind = if (jsonObj.has("auditKind")) jsonObj.getInt("auditKind") else 0
//                                            val listBean = AuditListBean(reportdataid = reportdataid, auditid = auditid, auditKind = auditKind, flownum = flownum)
//                                            ReportDetailActivity.startAction(this, false, "", listBean)
                                        }, null)
                                        return
                                    }
                                }
                                12 -> {//退回上报
                                    if (UserManager.canReport()) {
                                        ZXDialogUtil.showYesNoDialog(this@MessageRecordActivity, item.title, item.content, "前往处理", "关闭", DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                                            //                                            val reportdataid = jsonObj.getString("reportdataid")
//                                            ReportDetailActivity.startAction(this, false, reportdataid)
                                        }, null)
                                        return
                                    }
                                }
                            }
                        }
                        ZXDialogUtil.showInfoDialog(this@MessageRecordActivity, item!!.title, item!!.content)

                    }

                })
        loadData(true)
    }

    /**
     * 数据加载
     */
    fun loadData(refresh: Boolean = false) {
        if (refresh) {
            pageNo = 1
            sr_list.clearStatus()
        }
        mPresenter.getPushList(ApiParamUtil.getPushList(pageNo = pageNo, pageSize = pageSize, startTime = tv_startTime.text.toString(),
                endTime = tv_endTime.text.toString()))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //重置
        tv_clear.setOnClickListener {
            tv_startTime.text = ""
            tv_endTime.text = ""
            loadData(true)
        }
        //搜索
        tv_search.setOnClickListener {
            loadData(true)
        }
        tv_startTime.setOnClickListener {
            TimePickerDialog.Builder().apply {
                setCallBack { _, millseconds ->

                    if (!tv_endTime.text.toString().isEmpty()) {
                        var end = ZXTimeUtil.string2Millis(tv_endTime.text.toString(), "yyyy-MM-dd")

                        if (millseconds > end) {
                            showToast("开始时间不能大于结束时间")
                            return@setCallBack
                        }
                    }
                    tv_startTime.text = ZXTimeUtil.getTime(millseconds)
                }
                setCyclic(true)
                setMaxMillseconds(System.currentTimeMillis())
                setThemeColor(ContextCompat.getColor(mContext!!, R.color.colorPrimary))
                setType(Type.ALL)
                setWheelItemTextNormalColor(ContextCompat.getColor(mContext!!, R.color.text_color_light))
                setWheelItemTextSelectorColor(ContextCompat.getColor(mContext!!, R.color.text_color_normal))
                setTitleStringId("请选择时间")

            }.build().show(supportFragmentManager, "time_select")

        }
        tv_endTime.setOnClickListener {
            TimePickerDialog.Builder().apply {
                setCallBack { _, millseconds ->

                    if (!tv_startTime.text.toString().isEmpty()) {
                        var start = ZXTimeUtil.string2Millis(tv_startTime.text.toString(), "yyyy-MM-dd")

                        if (millseconds < start) {
                            showToast("结束时间不能小于开始时间")
                            return@setCallBack
                        }
                    }
                    tv_endTime.text = ZXTimeUtil.getTime(millseconds)
                }
                setCyclic(true)
                setMaxMillseconds(System.currentTimeMillis())
                setThemeColor(ContextCompat.getColor(mContext!!, R.color.colorPrimary))
                setType(Type.ALL)
                setWheelItemTextNormalColor(ContextCompat.getColor(mContext!!, R.color.text_color_light))
                setWheelItemTextSelectorColor(ContextCompat.getColor(mContext!!, R.color.text_color_normal))
                setTitleStringId("请选择时间")

            }.build().show(supportFragmentManager, "time_select")
        }
    }

    override fun onPushListResult(list: NormalList<MessageRecordBean>?) {
        if (list != null) {
            sr_list.refreshData(list.result!!, list.totalRecords)
        } else {
            listAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData(true) }
        }
    }

    /**
     * 版本检测
     */
    override fun onVersionResult(versionBean: VersionBean?) {
        if (versionBean == null) {
            showToast("暂未检查到更新")
        } else {
            val versionCode = ZXSystemUtil.getVersionCode()
            if (versionBean.version != null && versionCode < versionBean.version!!.toInt()) {
                ZXDialogUtil.showYesNoDialog(mContext, "提示", "当前应用需要下载更新\n版本号:${versionBean.versionName}\n内容:${versionBean.content}", "下载", "关闭", { dialog, which ->
                    getPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mPresenter.downloadApk(versionBean.path)
                    }
                }, { dialog, which ->
                    if (versionBean.isForce) {
                        showToast("请先更新后使用")
                        MyApplication.instance.exit()
                    }
                }, false)
            } else {
                showToast("暂未检查到更新")
            }
        }
    }

    /**
     * apk下载回调
     */
    override fun onApkDownloadResult(file: File) {
        ZXAppUtil.installApp(this, file)
    }
}

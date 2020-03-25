package com.zx.landdisaster.module.worklog.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.tool.LocationTool
import com.zx.landdisaster.module.other.ui.AddFileFragment
import com.zx.landdisaster.module.worklog.bean.DailyRemindBean
import com.zx.landdisaster.module.worklog.mvp.contract.DailyReportAddContract
import com.zx.landdisaster.module.worklog.mvp.model.DailyReportAddModel
import com.zx.landdisaster.module.worklog.mvp.presenter.DailyReportAddPresenter
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFragmentUtil
import kotlinx.android.synthetic.main.fragment_daily_report_add.*
import java.io.File
import java.util.*

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class DailyReportAddFragment : BaseFragment<DailyReportAddPresenter, DailyReportAddModel>(), DailyReportAddContract.View {

    private lateinit var addFileFragment: AddFileFragment

    companion object {
        /**
         * 启动器
         */
        fun newInstance(): DailyReportAddFragment {
            val fragment = DailyReportAddFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_daily_report_add
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        mPresenter.getDailyRemind()
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        ZXFragmentUtil.addFragment(childFragmentManager, AddFileFragment.newInstance().apply { addFileFragment = this }, R.id.fm_add_file)
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //提交
        btn_daily_submit.setOnClickListener {
            val pointBean = LocationTool.defaultPoint
            var longitude = ""
            var latitude = ""
            if (pointBean != null) {
                longitude = pointBean.longitude.toString()
                latitude = pointBean.latitude.toString()
            }
            var msg = "是否进行提交?"
            if (tv_daily_msg1.visibility == View.VISIBLE) {
                msg = "今日已上报，是否继续上报?"
            }
            ZXDialogUtil.showYesNoDialog(activity!!, "提示", msg) { dialog, which ->
                mPresenter.submitDaily(ApiParamUtil.dailySubmitParam(et_daily_content.text.toString(), longitude, latitude, ""))
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        addFileFragment.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 提交回调
     */
    override fun onDailySubmitResult(logid: String) {
        uploadLog(302, 1, "提交日报")
        val files = arrayListOf<File>()
        if (addFileFragment.fileBeans.isNotEmpty()) {
            addFileFragment.fileBeans.forEach {
                val path = if (it.type == 1) {
                    it.path
                } else {
                    it.vedioPath
                }
                if (!path.startsWith("http")) {//只上传本地选择的文件
                    val file = File(path)
                    files.add(file)
                }
            }
        }
        if (files.isNotEmpty()) {
            mPresenter.addFile(files, logid)
        } else {
            onDailyFileAddResult()
        }
    }

    override fun onDailyFileAddPregress(progress: Int) {
        handler.post {
            showLoading("正在上传中...", progress)
        }
    }

    override fun onDailyFileAddResult() {
        et_daily_content.setText("")
        showToast("提交成功！")
        uploadLog(302, 8, "上传日报上报附件")
        activity!!.finish()
    }

    /**
     * 上报测试
     */
    override fun onRemindResult(reminBean: DailyRemindBean) {
        if (reminBean.isReportDD || reminBean.isReportsafety || reminBean.isOtherReport) {
            tv_daily_msg1.visibility = View.VISIBLE
        }
    }
}

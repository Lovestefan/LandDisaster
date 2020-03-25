package com.zx.landdisaster.module.worklog.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zx.landdisaster.R
import com.zx.landdisaster.base.tool.ActionLog
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.other.ui.AddFileFragment
import com.zx.landdisaster.module.worklog.bean.DailyListBean

import com.zx.landdisaster.module.worklog.mvp.contract.UpdateDailyReportContract
import com.zx.landdisaster.module.worklog.mvp.model.UpdateDailyReportModel
import com.zx.landdisaster.module.worklog.mvp.presenter.UpdateDailyReportPresenter
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFragmentUtil
import kotlinx.android.synthetic.main.activity_update_daily_report.*
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class UpdateDailyReportActivity : BaseActivity<UpdateDailyReportPresenter, UpdateDailyReportModel>(), UpdateDailyReportContract.View {

    private lateinit var addFileFragment: AddFileFragment
    private lateinit var dailyBean: DailyListBean

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, dailyListBean: DailyListBean) {
            val intent = Intent(activity, UpdateDailyReportActivity::class.java)
            intent.putExtra("dailyBean", dailyListBean)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_update_daily_report
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ZXFragmentUtil.addFragment(this.supportFragmentManager, AddFileFragment.newInstance().apply { addFileFragment = this }, R.id.fm_add_file)

        dailyBean = intent.getSerializableExtra("dailyBean") as DailyListBean
        et_daily_content.setText(dailyBean.workcontent)

    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //提交
        btn_daily_submit.setOnClickListener {
            var longitude = dailyBean.longitude
            var latitude = dailyBean.latitude
            ZXDialogUtil.showYesNoDialog(this, "提示", "是否进行提交?") { dialog, which ->
                mPresenter.submitDaily(ApiParamUtil.dailySubmitParam(et_daily_content.text.toString(),
                        longitude.toString(), latitude.toString(), dailyBean.logid))
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
        finish()
    }

}

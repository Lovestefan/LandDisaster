package com.zx.landdisaster.module.worklog.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.other.ui.AddFileFragment
import com.zx.landdisaster.module.worklog.bean.WorkLogListBean
import com.zx.landdisaster.module.worklog.mvp.contract.WorkLogAddContract
import com.zx.landdisaster.module.worklog.mvp.model.WorkLogAddModel
import com.zx.landdisaster.module.worklog.mvp.presenter.WorkLogAddPresenter
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFragmentUtil
import com.zx.zxutils.util.ZXNetworkUtil
import kotlinx.android.synthetic.main.activity_work_log_add.*
import java.io.File

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class WorkLogAddActivity : BaseActivity<WorkLogAddPresenter, WorkLogAddModel>(), WorkLogAddContract.View {

    private lateinit var addFileFragment: AddFileFragment
    private var workLogListBean: WorkLogListBean? = null

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, workLogListBean: WorkLogListBean? = null) {
            val intent = Intent(activity, WorkLogAddActivity::class.java)
            if (workLogListBean != null) intent.putExtra("workLogDetail", workLogListBean)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_work_log_add
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        if (intent!!.hasExtra("workLogDetail")) {
            workLogListBean = intent!!.getSerializableExtra("workLogDetail") as WorkLogListBean
            ZXFragmentUtil.addFragment(supportFragmentManager, AddFileFragment.newInstance(if (workLogListBean!!.type == 1) AddFileFragment.FileType.JUST_SHOW else AddFileFragment.FileType.NORMAL).apply { addFileFragment = this }, R.id.fm_add_file)
            initData(workLogListBean!!)
            if (workLogListBean!!.type == 1) {
                et_worklog_worktype.isFocusableInTouchMode = false
                et_worklog_onduty.isFocusableInTouchMode = false
                et_worklog_content.isFocusableInTouchMode = false
                et_worklog_remark.isFocusableInTouchMode = false
                btn_worklog_save.visibility = View.GONE
                btn_worklog_submit.visibility = View.GONE
            } else {
                btn_worklog_save.text = "删    除"
            }
        } else {
            ZXFragmentUtil.addFragment(supportFragmentManager, AddFileFragment.newInstance(AddFileFragment.FileType.NORMAL).apply { addFileFragment = this }, R.id.fm_add_file)
            et_worklog_perosn.text = UserManager.getUser().currentUser!!.name
        }
        addFileFragment.setOnEditCallBack {
            scroll_view.fullScroll(0)
        }
    }

    private fun initData(workLogListBean: WorkLogListBean) {
        workLogListBean.apply {
            et_worklog_perosn.text = UserManager.getUser().currentUser!!.name
            et_worklog_worktype.setText(worktype)
            et_worklog_onduty.setText(onduty)
            et_worklog_content.setText(content)
            et_worklog_remark.setText(note)
            if (files != null && files!!.isNotEmpty()) {
                addFileFragment.setData(files!!)
            }
        }
    }

    override fun onSubmitErrorResult() {
        ZXDialogUtil.showYesNoDialog(this, "提示", "提交失败，是否保存信息？") { dialog, which ->
            btn_worklog_save.performClick()
        }
    }


    /**
     * View事件设置
     */
    override fun onViewListener() {
        btn_worklog_save.setOnClickListener {
            if (workLogListBean != null && workLogListBean!!.type == 0) {
                val workLogList = arrayListOf<WorkLogListBean>()
                if (mSharedPrefUtil.contains("workLogBeanList") && mSharedPrefUtil.getList<WorkLogListBean>("workLogBeanList") != null) {
                    workLogList.addAll(mSharedPrefUtil.getList<WorkLogListBean>("workLogBeanList"))
                    if (workLogList.size > workLogListBean!!.position) {
                        workLogList.removeAt(workLogListBean!!.position)
                    }
                    mSharedPrefUtil.putList("workLogBeanList", workLogList)
                    showToast("删除成功")
                    finish()
                }
            } else {
                val workLogList = arrayListOf<WorkLogListBean>()
                if (mSharedPrefUtil.contains("workLogBeanList") && mSharedPrefUtil.getList<WorkLogListBean>("workLogBeanList") != null) {
                    workLogList.addAll(mSharedPrefUtil.getList("workLogBeanList"))
                }
                val workLogBean = WorkLogListBean(et_worklog_content.text.toString(), "", et_worklog_remark.text.toString(), et_worklog_onduty.text.toString(), et_worklog_perosn.text.toString(), System.currentTimeMillis(), et_worklog_worktype.text.toString(), addFileFragment.fileBeans)
                workLogList.add(0, workLogBean)
                mSharedPrefUtil.putList("workLogBeanList", workLogList)
                showToast("保存成功")
                finish()
            }
        }

        btn_worklog_submit.setOnClickListener {
            if (!ZXNetworkUtil.isConnected()) {
                ZXDialogUtil.showYesNoDialog(this, "提示", "网络连接失败，是否保存信息？") { dialog, which ->
                    btn_worklog_save.performClick()
                }
                return@setOnClickListener
            }
            if (et_worklog_worktype.text.toString().isEmpty()) {
                showToast("请输入工作类型")
            } else if (et_worklog_onduty.text.toString().isEmpty()) {
                showToast("请输入在岗情况")
            } else if (et_worklog_content.text.toString().isEmpty()) {
                showToast("请输入日志内容")
            } else {
                ZXDialogUtil.showYesNoDialog(this, "提示", "是否进行提交？") { dialog, which ->
                    mPresenter.addWokLog(ApiParamUtil.addWorkLogParams(et_worklog_perosn.text.toString(), et_worklog_worktype.text.toString(), et_worklog_onduty.text.toString(), et_worklog_content.text.toString(), et_worklog_remark.text.toString()))
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        addFileFragment.onActivityResult(requestCode, resultCode, data)
    }

    override fun onWorkLogAddResult(logid: String) {
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
            mPresenter.uploadFile(files, logid)
        } else {
            onWorkLogFileAddResult()
        }
    }

    override fun onWorkLogFileAddPregress(progress: Int) {
        handler.post {
            showLoading("正在上传中...", progress)
        }
    }

    override fun onWorkLogFileAddResult() {
        et_worklog_worktype.setText("")
        et_worklog_onduty.setText("")
        et_worklog_content.setText("")
        et_worklog_remark.setText("")
        if (workLogListBean != null && workLogListBean!!.type == 0) {
            val workLogList = arrayListOf<WorkLogListBean>()
            if (mSharedPrefUtil.contains("workLogBeanList") && mSharedPrefUtil.getList<WorkLogListBean>("workLogBeanList") != null) {
                workLogList.addAll(mSharedPrefUtil.getList("workLogBeanList"))
                if (workLogList.size > workLogListBean!!.position) {
                    workLogList.removeAt(workLogListBean!!.position)
                }
                mSharedPrefUtil.putList("workLogBeanList", workLogList)
            }
        }
        showToast("提交成功！")
        finish()
    }
}

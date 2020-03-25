package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.zx.landdisaster.R
import com.zx.landdisaster.base.tool.ActionLog
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.disaster.bean.AuditListBean
import com.zx.landdisaster.module.disaster.bean.ReportDetailBean
import com.zx.landdisaster.module.main.ui.MapPointActivity
import com.zx.landdisaster.module.disaster.bean.AuditReviewBean
import com.zx.landdisaster.module.disaster.mvp.contract.ReportDetailContract
import com.zx.landdisaster.module.disaster.mvp.model.ReportDetailModel
import com.zx.landdisaster.module.disaster.mvp.presenter.ReportDetailPresenter
import com.zx.zxutils.util.ZXDialogUtil
import kotlinx.android.synthetic.main.activity_report_detail.*


/**
 * Create By admin On 2017/7/11
 * 功能：上报详情
 */
class ReportDetailActivity : BaseActivity<ReportDetailPresenter, ReportDetailModel>(), ReportDetailContract.View {

    private var reportAddFragment: ReportHazardAddFragment? = null
    private var auditLogsFragment: AuditHandleLogsFragment? = null

    private var reportBean: ReportDetailBean? = null
    private var reviewBean: AuditReviewBean? = null
    private var showAudit = false

    private var type: TYPE? = null

    private enum class TYPE {
        FROM_REPORT_LIST,
        FROM_AUDIT_LIST,
        FROM_DISASTER
    }

    companion object {
        /**
         * 启动器 从上报列表中来
         */
        fun startAction(activity: Activity, isFinish: Boolean, reportdataid: String) {
            val intent = Intent(activity, ReportDetailActivity::class.java)
            intent.putExtra("reportdataid", reportdataid)
            intent.putExtra("showAudit", false)
            intent.putExtra("isDisaster", false)
            intent.putExtra("type", TYPE.FROM_REPORT_LIST)
            activity.startActivityForResult(intent, 0x01)
            if (isFinish) activity.finish()
        }

        /**
         * 启动器 从审核列表中来
         */
        fun startAction(activity: Activity, isFinish: Boolean, path: String, listBean: AuditListBean) {
            val intent = Intent(activity, ReportDetailActivity::class.java)
            if (path.isNotEmpty()) {
                intent.putExtra("path", path)
            } else {
                intent.putExtra("reportdataid", listBean.reportdataid)
            }
            intent.putExtra("listBean", listBean)
            intent.putExtra("showAudit", true)
            intent.putExtra("isDisaster", false)
            intent.putExtra("type", TYPE.FROM_AUDIT_LIST)
            activity.startActivityForResult(intent, 0x01)
            if (isFinish) activity.finish()
        }

        /**
         * 启动器 从地图灾险点来
         */
        fun startAction(activity: Activity, pkidd: String, isFinish: Boolean) {
            val intent = Intent(activity, ReportDetailActivity::class.java)
            intent.putExtra("pkidd", pkidd)
            intent.putExtra("showAudit", false)
            intent.putExtra("isDisaster", true)
            intent.putExtra("type", TYPE.FROM_DISASTER)
            activity.startActivityForResult(intent, 0x01)
            if (isFinish) activity.finish()
        }

    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_report_detail
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        type = intent.getSerializableExtra("type") as TYPE

        loadDetailInfo()
    }

    private fun loadDetailInfo() {
        if (type == null) {
            return
        }
        when (type) {
            TYPE.FROM_REPORT_LIST -> {
                val reportdataid = intent.getStringExtra("reportdataid")
                mPresenter.getReportInfo(reportdataid)
            }
            TYPE.FROM_AUDIT_LIST -> {
                if (intent.hasExtra("reportdataid")) {
                    val reportdataid = intent.getStringExtra("reportdataid")
                    mPresenter.getReportInfo(reportdataid)
                } else if (intent.hasExtra("path")) {
                    val path = intent.getStringExtra("path")
                    mPresenter.getAuditDetail(path)
                }
            }
            TYPE.FROM_DISASTER -> {
                val pkidd = intent.getStringExtra("pkidd")
                mPresenter.getDisasterDetail(pkidd)
            }
        }
        uploadLog(303, 5, "查看灾险情详情")
    }

    private fun setView() {
        showAudit = intent.getBooleanExtra("showAudit", false)

        if (type == TYPE.FROM_DISASTER) {
            toolbar_view.setMidText("灾险情详情")

        } else if (UserManager.getUser().personRole.leader || UserManager.getUser().personRole.dispatch) {
            toolbar_view.setMidText("审阅")
            reportBean!!.reportdata!!.editAllAble = false
            reportBean!!.reportdata!!.editAble = false
        } else {
            if (reportBean!!.reportdata!!.editAllAble) {
                toolbar_view.setMidText("重新上报")
            } else if (reportBean!!.reportdata!!.editAble) {
                toolbar_view.setMidText("续报")
            }
        }

        tvp_report_detail.setManager(supportFragmentManager)
                .setTabScrollable(false)
                .setViewpagerCanScroll(true)
                .addTab(ReportHazardAddFragment.newInstance(reportBean, showAudit).apply { reportAddFragment = this }, "上报信息")
        if (!intent.getBooleanExtra("isDisaster", false)) {
            tvp_report_detail.addTab(AuditHandleLogsFragment.newInstance(reportBean!!.reportdata!!.flownum!!).apply { auditLogsFragment = this }, "审核日志")
        } else {
            tvp_report_detail.tabLayout.visibility = View.GONE
        }
        tvp_report_detail.setTitleColor(ContextCompat.getColor(this, R.color.text_color_normal), ContextCompat.getColor(this, R.color.colorPrimary))
                .setIndicatorHeight(10)
                .setIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setTablayoutHeight(40)
                .setTabTextSize(12, 12)
                .setTablayoutBackgroundColor(ContextCompat.getColor(this, R.color.white))
                .showDivider(ContextCompat.getColor(this, R.color.text_color_light))
                .build()

        reportAddFragment!!.setAuditListener {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_audit_handler, null, false)
            val auditNote = view.findViewById<EditText>(R.id.et_handle_auditNote)
            val btnDo = view.findViewById<Button>(R.id.btn_handle_submit)
            val auditTitle = view.findViewById<TextView>(R.id.tv_audit_handle_title)
            val rgChoice = view.findViewById<RadioGroup>(R.id.rg_handle_auditChoices)
            val llLastReview = view.findViewById<RelativeLayout>(R.id.rl_audit_hanlde_last)
            val tvLastReview = view.findViewById<TextView>(R.id.tv_audit_handle_last)
            val tvResetLastReview = view.findViewById<TextView>(R.id.tv_audit_hanlde_reset)
            var typeString = "审核"
            if (UserManager.getUser().personRole.dispatch || UserManager.getUser().personRole.leader) {
                typeString = "审阅"
                rgChoice.visibility = View.GONE
            } else {
                typeString = "审核"
                rgChoice.visibility = View.VISIBLE
            }
            btnDo.text = typeString
            auditTitle.text = typeString + "意见"

            if (reviewBean != null && reviewBean!!.hasreview == 1) {
                llLastReview.visibility = View.VISIBLE
                auditNote.setText(reviewBean!!.reviewopinion)
            } else {
                llLastReview.visibility = View.GONE
            }
            tvResetLastReview.setOnClickListener {
                auditNote.setText(reviewBean!!.reviewopinion)
            }
            auditNote.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (llLastReview.visibility == View.VISIBLE && auditNote.text.toString() != reviewBean!!.reviewopinion) {
                        tvLastReview.visibility = View.GONE
                    }
                }

            })
            btnDo.setOnClickListener {
                if (auditNote.text.isEmpty() && view.findViewById<RadioButton>(R.id.rb_handle_auditChoicesReturn).isChecked) {
                    showToast("请填写${typeString}意见")
                } else {
                    if (typeString == "审核") {
                        mPresenter.doAuditReport(ApiParamUtil.auditReportParam(reportBean!!.reportdata!!.auditKind.toInt(), reportBean!!.reportdata!!.auditid, auditNote.text.toString(),
                                if (view.findViewById<RadioButton>(R.id.rb_handle_auditChoicesPass).isChecked) {
                                    "1"
                                } else if (view.findViewById<RadioButton>(R.id.rb_handle_auditChoicesReturn).isChecked) {
                                    "-1"
                                } else {
                                    "-2"
                                }))
                    } else if (typeString == "审阅") {
                        mPresenter.doAuditLeader(ApiParamUtil.auditLeaderParam(reportBean!!.reportdata!!.auditid, auditNote.text.toString()))
                    }
                }
            }
            if (reportBean!!.elPerson) {
                view.findViewById<RadioButton>(R.id.rb_handle_auditChoicesDelete).visibility = View.VISIBLE
            }
            ZXDialogUtil.showCustomViewDialog(this, "", view, null)
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        toolbar_view.setRightClickListener {
            if (reportBean != null && reportBean!!.reportdata!!.longitude != null && reportBean!!.reportdata!!.latitude != null) {
                MapPointActivity.startAction(this, false, reportBean!!)
            } else {
                showToast("未获取到坐标信息")
            }
        }
    }

    /**
     * 审核成功
     */
    override fun onAuditResult(status: Boolean?) {
        uploadLog(303, 6, "审核灾险情")
        if (status != null && status) {
            mPresenter.transferFile(ApiParamUtil.thansferFileParam(reportBean!!.reportdata!!.flownum!!, reportBean!!.reportdata!!.pkidd!!))
        } else {
            onTransferFileResult()
        }
    }

    override fun onReviewDetailResult(reviewBean: AuditReviewBean) {
        this.reviewBean = reviewBean
    }

    override fun onReportInfoResult(reportInfoBean: ReportDetailBean) {
        reportBean = reportInfoBean
        if (type == TYPE.FROM_AUDIT_LIST) {
            reportBean!!.reportdata!!.editAllAble = false
            reportBean!!.reportdata!!.editAble = true
            val listBean = intent.getSerializableExtra("listBean") as AuditListBean
            reportBean!!.reportdata!!.auditid = listBean.auditid
            reportBean!!.reportdata!!.auditKind = listBean.auditKind.toString()
            mPresenter.getAuditReview(reportBean!!.reportdata!!.auditid)
        }
        setView()
    }

    override fun onTransferFileResult() {
        ZXDialogUtil.dismissDialog()
        showToast("操作完成")
        setResult(0x01)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (reportAddFragment != null) {
            reportAddFragment!!.onActivityResult(requestCode, resultCode, data)
        }
    }

}

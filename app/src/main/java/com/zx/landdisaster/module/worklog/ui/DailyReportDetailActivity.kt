package com.zx.landdisaster.module.worklog.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.other.ui.AddFileFragment
import com.zx.landdisaster.module.worklog.bean.DailyListBean
import com.zx.landdisaster.module.worklog.mvp.contract.DailyReportDetailContract
import com.zx.landdisaster.module.worklog.mvp.model.DailyReportDetailModel
import com.zx.landdisaster.module.worklog.mvp.presenter.DailyReportDetailPresenter
import com.zx.zxutils.util.ZXFragmentUtil
import com.zx.zxutils.util.ZXTimeUtil
import kotlinx.android.synthetic.main.activity_daily_report_detail.*


/**
 * Create By admin On 2017/7/11
 * 功能：日报详情
 */
class DailyReportDetailActivity : BaseActivity<DailyReportDetailPresenter, DailyReportDetailModel>(), DailyReportDetailContract.View {

    private lateinit var addFileFragment: AddFileFragment
    private lateinit var dailyBean: DailyListBean

    var type = "0"//1 区县日报列表进入详情

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, dailyListBean: DailyListBean, type: String = "0") {
            val intent = Intent(activity, DailyReportDetailActivity::class.java)
            intent.putExtra("dailyBean", dailyListBean)
            intent.putExtra("type", type)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_daily_report_detail
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        type = intent.getStringExtra("type")
        ZXFragmentUtil.addFragment(supportFragmentManager, AddFileFragment.newInstance(AddFileFragment.FileType.JUST_SHOW).apply { addFileFragment = this }, R.id.fm_add_file)

        tv_detail.movementMethod = ScrollingMovementMethod.getInstance()
        dailyBean = intent.getSerializableExtra("dailyBean") as DailyListBean

        if (type == "1") {//从区县日报列表进入详情
            if (dailyBean.personName != null)
                tv_name.text = "上报人：" + dailyBean.personName
            if (dailyBean.logtime != null)
                tv_time.text = "上报时间：" + ZXTimeUtil.millis2String(dailyBean.logtime)
            if (dailyBean.auditorname != null)
                tv_auditorname.text = "审核人：" + dailyBean.auditorname
            if (dailyBean.areaname != null)
                tv_area.text = "区县：" + dailyBean.areaname
            if (dailyBean.latitude != null)
                tv_latitude.text = "纬度：" + dailyBean.latitude
            if (dailyBean.longitude != null)
                tv_longitude.text = "经度：" + dailyBean.longitude
            if (dailyBean.workcontent != null)
                tv_detail.text = "上报内容\n" + dailyBean.workcontent
            tv_daily_detail.visibility = View.GONE
            ll_AreaReport.visibility = View.VISIBLE
        } else {
            tv_daily_detail.text = dailyBean.workcontent

            if (dailyBean.status.equals("-1"))//驳回
                btn_submit.visibility = View.VISIBLE

        }
        mPresenter.getDailyFile(ApiParamUtil.getDailyFile(dailyBean.logid))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        btn_submit.setOnClickListener {
            UpdateDailyReportActivity.startAction(this, false, dailyBean)
            finish()
        }
    }

    /**
     * 文件回调
     */
    override fun onDailyFileResult(fileList: List<AuditReportFileBean>) {
        val addFileList = arrayListOf<AddFileBean>()
        if (fileList.isNotEmpty()) {
            fileList.forEach {
                addFileList.add(AddFileBean.reSetType(it.type, it.name, it.path))
            }
        }
        addFileFragment.setData(addFileList)
    }
}

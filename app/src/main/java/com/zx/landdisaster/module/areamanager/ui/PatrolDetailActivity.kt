package com.zx.landdisaster.module.areamanager.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.areamanager.bean.PatrolListBean
import com.zx.landdisaster.module.areamanager.mvp.contract.PatrolDetailContract
import com.zx.landdisaster.module.areamanager.mvp.model.PatrolDetailModel
import com.zx.landdisaster.module.areamanager.mvp.presenter.PatrolDetailPresenter
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.disaster.func.util.TextSizeUtil
import com.zx.landdisaster.module.other.ui.AddFileFragment
import com.zx.zxutils.util.ZXFragmentUtil
import kotlinx.android.synthetic.main.activity_patrol_detail.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class PatrolDetailActivity : BaseActivity<PatrolDetailPresenter, PatrolDetailModel>(), PatrolDetailContract.View {

    private lateinit var addFileFragment: AddFileFragment

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, data: PatrolListBean) {
            val intent = Intent(activity, PatrolDetailActivity::class.java)
            intent.putExtra("pkiaa", data.pkiaa)
            intent.putExtra("phase", data.phase)
            intent.putExtra("content", data.content)
            intent.putExtra("id", data.id)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_patrol_detail
    }

    fun getLevel(phase: Int): String {
        if (phase == 1)
            return "雨前"
        else if (phase == 2)
            return "雨中"
        else
            return "雨后"
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val roleBean = UserManager.getUser().personRole
        if (!roleBean.groupDefense && !roleBean.areaManager) {
            TextSizeUtil.resetText(this, tv_pkiaa_name)
            TextSizeUtil.resetText(this, tv_pkiaa)
            TextSizeUtil.resetText(this, tv_hazardType_name)
            TextSizeUtil.resetText(this, tv_hazardType)
            TextSizeUtil.resetText(this, tv_content_name)
            TextSizeUtil.resetText(this, tv_patrol_content)
        }

        val pkiaa = intent.getStringExtra("pkiaa")
        val phase = intent.getIntExtra("phase", 1)
        val content = intent.getStringExtra("content")
        val id = intent.getStringExtra("id")

        tv_pkiaa.setText(pkiaa)
        tv_hazardType.setText(getLevel(phase))
        tv_patrol_content.setText(content)

        mPresenter.getPatrolFile(ApiParamUtil.getPatrolFileList(id))
        ZXFragmentUtil.addFragment(this.supportFragmentManager,
                AddFileFragment.newInstance().apply {
                    addFileFragment = this
                }, R.id.fm_add_file)
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    override fun getPatrolFileResult() {
        fm_add_file.visibility = View.GONE
    }

    override fun getPatrolFileResult(fileList: List<AuditReportFileBean>) {
        val addFileList = arrayListOf<AddFileBean>()
        if (fileList.isNotEmpty()) {
            fileList.forEach {
                addFileList.add(AddFileBean.reSetType(it.type, it.name, it.path))
            }
        }
        addFileFragment.setData(addFileList)
        addFileFragment.setCanEdit(false)
    }

}

package com.zx.landdisaster.module.main.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import cn.jpush.android.api.JPushInterface
import com.tencent.bugly.crashreport.CrashReport
import com.zx.landdisaster.BuildConfig
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.app.MyApplication
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.LocationTool
import com.zx.landdisaster.module.areamanager.ui.AreaManagerFragment
import com.zx.landdisaster.module.groupdefense.ui.GroupDefenceManageFragment
import com.zx.landdisaster.module.main.bean.VersionBean
import com.zx.landdisaster.module.main.mvp.contract.MainOtherContract
import com.zx.landdisaster.module.main.mvp.model.MainOtherModel
import com.zx.landdisaster.module.main.mvp.presenter.MainOtherPresenter
import com.zx.landdisaster.module.system.ui.UserActivity
import com.zx.zxutils.util.ZXAppUtil
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFragmentUtil
import com.zx.zxutils.util.ZXSystemUtil
import kotlinx.android.synthetic.main.activity_main_other.*
import java.io.File
import java.util.*
import kotlin.concurrent.schedule


/**
 * Create By admin On 2017/7/11
 * 功能：其他用户的主页
 */
class MainOtherActivity : BaseActivity<MainOtherPresenter, MainOtherModel>(), MainOtherContract.View {

    private var gpsTimer: TimerTask? = null

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, MainOtherActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_main_other

    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        JPushInterface.resumePush(this)
        JPushInterface.setAlias(this, 0, UserManager.getUser().currentUser!!.personid)
        JPushInterface.setTags(this, 0, setOf(if (ApiConfigModule.ISRELEASE) "regular" else "test"))
        if (UserManager.getUser().personRole.areaManager) {
            toolbar_view.setMidText("片区负责人")
            ZXFragmentUtil.addFragment(supportFragmentManager, AreaManagerFragment.newInstance(), R.id.fm_othermain)
        } else if (UserManager.getUser().personRole.groupDefense) {
            toolbar_view.setMidText("群测群防")
            ZXFragmentUtil.addFragment(supportFragmentManager, GroupDefenceManageFragment.newInstance(), R.id.fm_othermain)
        }

        if (BuildConfig.RELEASE) {
            CrashReport.setUserId(UserManager.getUser().currentUser!!.personid)
        }

        uploadLog(304, 2, "登入旧版首页")
        startLocationUpdate()

    }

    /**
     * 开启坐标上传定时器
     */
    private fun startLocationUpdate() {
        var gpsPeriod = UserManager.getUser().gpsupfreq
        if (gpsPeriod <= 0) {
            return
        } else if (gpsPeriod < 30) {
            gpsPeriod = 30
        }

        gpsTimer = Timer().schedule(0, gpsPeriod.toLong() * 1000) {
            uploadGPS()
        }
    }

    private fun uploadGPS() {
        getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationTool.getLocation(this@MainOtherActivity, true) {
                if (it != null) {
                    mPresenter.updateLocation(it.longitude.toString(), it.latitude.toString())
                }
            }
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        toolbar_view.setLeftClickListener {
            UserActivity.startAction(this, false)
        }
    }

    /**
     * 版本检测
     */
    override fun onVersionResult(versionBean: VersionBean) {
        val versionCode = ZXSystemUtil.getVersionCode()
        if (versionBean.version != null && versionCode < versionBean.version!!.toInt()) {
            ZXDialogUtil.showYesNoDialog(mContext, "提示", "当前应用需要下载更新\n版本号:${versionBean.versionName}\n内容:${versionBean.content}", "下载", "关闭", { dialog, which ->
                getPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mPresenter.downloadApk(versionBean.path)
                }
            }, { dialog, which ->
                if (versionBean.isForce) {
                    showToast("请先更新后使用")
                    handler.postDelayed({
                        MyApplication.instance.exit()
                    }, 500)
                }
            }, false)
        }
    }

    /**
     * apk下载回调
     */
    override fun onApkDownloadResult(file: File) {
        ZXAppUtil.installApp(this,file)
    }

    override fun onBackPressed() {
        startActivity(Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_HOME)
        })
    }

    override fun onDestroy() {
        if (gpsTimer != null) {
            gpsTimer!!.cancel()
        }
        super.onDestroy()
    }

}

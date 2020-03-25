package com.zx.landdisaster.module.main.ui;

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.RelativeLayout
import cn.jpush.android.api.JPushInterface
import com.tencent.bugly.crashreport.CrashReport
import com.zx.landdisaster.BuildConfig
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.app.MyApplication
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.LocationTool
import com.zx.landdisaster.module.main.bean.VersionBean
import com.zx.landdisaster.module.main.func.listener.MapListener
import com.zx.landdisaster.module.main.func.util.RemindTimeUtil
import com.zx.landdisaster.module.main.mvp.contract.MainContract
import com.zx.landdisaster.module.main.mvp.model.MainModel
import com.zx.landdisaster.module.main.mvp.presenter.MainPresenter
import com.zx.landdisaster.module.worklog.bean.DailyRemindBean
import com.zx.landdisaster.module.worklog.ui.DailyReportActivity
import com.zx.zxutils.util.*
import com.zx.zxutils.views.ZXStatusBarCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import kotlin.concurrent.schedule


/**
 * Create By admin On 2017/7/11
 * 功能：主界面
 */
class MainActivity : BaseActivity<MainPresenter, MainModel>(), MainContract.View {

    override var canSwipeBack: Boolean = false
    private lateinit var mapListener: MapListener
    private lateinit var mapFragment: MapFragment
    private lateinit var mapBtnFragment: MapBtnFragment
    private lateinit var mapSearchFragment: MapSearchFragment

    var type = 0
    private var gpsTimer: TimerTask? = null
    private val remindTimeUtil = RemindTimeUtil()

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, type: Int = 0) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("type", type)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ZXStatusBarCompat.translucent(this)
        ZXStatusBarCompat.setStatusBarLightMode(this)
        if (getUserType() == 1) {
            uploadLog(304, 3, "登入地图首页")
            JPushInterface.resumePush(this)
            JPushInterface.setAlias(this, 0, UserManager.getUser().currentUser!!.personid)
            JPushInterface.setTags(this, 0, setOf(if (ApiConfigModule.ISRELEASE) "regular" else "test"))
        }
        onConfigurationChanged(resources.configuration)
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        type = intent.getIntExtra("type", 0)
        ZXFragmentUtil.addFragment(supportFragmentManager, MapFragment.newInstance().apply {
            mapFragment = this
        }, R.id.fm_main_map)
        ZXFragmentUtil.addFragment(supportFragmentManager, MapBtnFragment.newInstance().apply {
            mapBtnFragment = this
            setMapListener(mapFragment.mapListener)
            setOnType(type.toString())
        }, R.id.fm_main_btn)
        ZXFragmentUtil.addFragment(supportFragmentManager, MapSearchFragment.newInstance().apply {
            mapSearchFragment = this
            setMapListener(mapFragment.mapListener)
        }, R.id.fm_map_search)

        if (getUserType() == 1) {
            startLocationUpdate()
            mPresenter.getVerson()
        }

        try {
            if (BuildConfig.RELEASE) {
                CrashReport.setUserId(UserManager.getUser().currentUser!!.personid)
            }
            if (mSharedPrefUtil.getBool("isGetPush")) {
                val bundle = Bundle()
                val map = mSharedPrefUtil.getMap<String, String>("jPushBundle")
                bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, map.get(JPushInterface.EXTRA_NOTIFICATION_TITLE))
                bundle.putString(JPushInterface.EXTRA_ALERT, map.get(JPushInterface.EXTRA_ALERT))
                bundle.putString(JPushInterface.EXTRA_EXTRA, map.get(JPushInterface.EXTRA_EXTRA))
                mRxManager.post("jPush", bundle)
            }
        } catch (e: Exception) {
        }
        mainLayout.setOnTouchListener { _, _ ->
            if (mapBtnFragment.popupWindowShowing())
                mapBtnFragment.popupWindowDismiss()
            return@setOnTouchListener false
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
    }


    /**
     * 开启坐标上传定时器
     */
    private fun startLocationUpdate() {
        if (intent.getIntExtra("type", 0) != 0) {
            //仅当旧界面的时候会进行上传坐标
            return
        }
        var gpsPeriod = UserManager.getUser().gpsupfreq
        if (gpsPeriod <= 0) {
            return
        } else if (gpsPeriod < 30) {
            gpsPeriod = 30
        }
        gpsTimer = Timer().schedule(0, gpsPeriod.toLong() * 1000) {
            uploadGPS()
            if (UserManager.getUser().personRole.ringStand) {
                remindTimeUtil.checkTime(UserManager.getUser().dailySafeScope) {
                    mPresenter.getDailyRemind()
                }
            }
        }

    }

    private fun uploadGPS() {
        getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationTool.getLocation(this@MainActivity, true) {
                if (it != null) {
                    mPresenter.updateLocation(it.longitude.toString(), it.latitude.toString())
                }
            }
        }
    }

    /**
     * 版本检测
     */
    override fun onVersionResult(versionBean: VersionBean) {
        val versionCode = ZXSystemUtil.getVersionCode()
        var isDownLoad = false
        if (versionBean.version != null && versionCode < versionBean.version!!.toInt()) {
            ZXDialogUtil.showYesNoDialog(mContext, "提示", "当前应用需要下载更新\n版本号:${versionBean.versionName}\n内容:${versionBean.content}", "下载", "关闭", { dialog, which ->
                isDownLoad = true
                getPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mPresenter.downloadApk(versionBean.path)
                }
            }, { _, _ ->
                if (versionBean.isForce) {
                    showToast("请先更新后使用")
                    handler.postDelayed({
                        MyApplication.instance.exit()
                    }, 500)
                }
            }, false).setOnDismissListener {
                if (!isDownLoad) {
                    showToast("请先更新后使用")
                    handler.postDelayed({
                        MyApplication.instance.exit()
                    }, 500)
                }
            }
        }
    }

    /**
     * apk下载回调
     */
    override fun onApkDownloadResult(file: File) {
        ZXAppUtil.installApp(this, file)
    }

    override fun onRemindResult(reminBean: DailyRemindBean) {
        if (!reminBean.isReportsafety && !reminBean.isReportDD && !reminBean.isOtherReport) {
            ZXDialogUtil.dismissDialog()
            ZXDialogUtil.showYesNoDialog(this, "提示", "今日还未填写日报，是否前往填写？") { _, _ ->
                DailyReportActivity.startAction(this, false)
            }

        }
    }

//    private var triggerAtTimefirst: Long = 0

    //    override fun onBackPressed() {
//        val triggerAtTimeSecond = triggerAtTimefirst
//        triggerAtTimefirst = SystemClock.elapsedRealtime()
//        if (triggerAtTimefirst - triggerAtTimeSecond <= 2000) {
//            MyApplication.instance.finishAll()
//        } else {
//            showToast("请再点击一次, 确认退出...")
//        }
//    }
//
    override fun onBackPressed() {
        if (getUserType() == 1) {
            startActivity(Intent().apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_HOME)
            })
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        if (gpsTimer != null) {
            gpsTimer!!.cancel()
        }
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig != null && fm_map_search != null && fm_main_btn != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                fm_map_search.layoutParams = fm_map_search.layoutParams.apply {
                    width = ZXSystemUtil.dp2px(if (ZXDeviceUtil.isTablet()) 400.0f else 330.0f)
                    height = if (ZXDeviceUtil.isTablet()) ZXSystemUtil.dp2px(400.0f) else RelativeLayout.LayoutParams.MATCH_PARENT
                }
                fm_main_btn.layoutParams = (fm_main_btn.layoutParams as RelativeLayout.LayoutParams).apply {
                    setMargins(0, 0, 0, 0)
                }
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
                fm_map_search.layoutParams = fm_map_search.layoutParams.apply {
                    width = RelativeLayout.LayoutParams.MATCH_PARENT
                    height = ZXSystemUtil.dp2px(400.0f)
                }
                fm_main_btn.layoutParams = (fm_main_btn.layoutParams as RelativeLayout.LayoutParams).apply {
                    setMargins(0, ZXSystemUtil.dp2px(85.0f), 0, 0)
                }
            }
        }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        setIntent(intent)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (intent.hasExtra("jpushtype")) {
//            when (intent.getIntExtra("jpushtype", 1)) {
//                1 -> {//普通消息
//                    ZXDialogUtil.showInfoDialog(this, "提示", intent.getStringExtra("msg"))
//                }
//                else -> {
//                }
//            }
//        }
//    }
}

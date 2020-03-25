package com.zx.landdisaster.base

import android.Manifest
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.view.KeyEvent
import cn.jpush.android.api.JPushInterface
import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.RxBaseActivity
import com.tencent.bugly.crashreport.CrashReport
import com.zx.landdisaster.BuildConfig
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.app.ConstStrings
import com.zx.landdisaster.app.MyApplication
import com.zx.landdisaster.base.bean.Jurisdiction
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.ActionLog
import com.zx.landdisaster.base.tool.UserActionTool
import com.zx.landdisaster.module.disaster.bean.AuditListBean
import com.zx.landdisaster.module.disaster.ui.ReportDetailActivity
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean
import com.zx.landdisaster.module.main.ui.InfoReleaseInfoActivity
import com.zx.landdisaster.module.system.ui.LoginActivity
import com.zx.landdisaster.module.system.ui.SplashActivity
import com.zx.landdisaster.module.system.ui.UserActivity
import com.zx.zxutils.util.*
import com.zx.zxutils.views.SwipeBack.ZXSwipeBackHelper
import com.zx.zxutils.views.ZXStatusBarCompat
import org.json.JSONObject
import rx.functions.Action1


/**
 * Created by Xiangb on 2019/2/26.
 * 功能：
 */
abstract class BaseActivity<T : BasePresenter<*, *>, E : BaseModel> : RxBaseActivity<T, E>() {
    var pageSize = 15
    val mSharedPrefUtil = ZXSharedPrefUtil()
    val handler = Handler()
    var sIsLoginClear = false
    open var canSwipeBack = false

    private lateinit var permessionBack: () -> Unit
    private lateinit var permissionArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (canSwipeBack) ZXSwipeBackHelper.onCreate(this)
                .setSwipeBackEnable(true)
                .setSwipeRelateEnable(true)
        MyApplication.instance.addActivity(this)
        ZXStatusBarCompat.setStatusBarDarkMode(this)
        ZXCrashUtil.init(BuildConfig.RELEASE) { _, e ->
            //            showToast("出现未知问题，请稍后再试")
            CrashReport.postCatchedException(e)
        }

        mRxManager.on("jPush", Action1<Bundle> {
            try {
                if (!isTopActivity() || this is SplashActivity || this is LoginActivity) {
                    return@Action1
                }
                mSharedPrefUtil.putBool("isGetPush", false)
                if (it.getString(JPushInterface.EXTRA_EXTRA).isNotEmpty() && JSONObject(it.getString(JPushInterface.EXTRA_EXTRA)).has("type")) {
                    val jsonObj = JSONObject(it.getString(JPushInterface.EXTRA_EXTRA))
                    when (jsonObj.getInt("type")) {
                        1 -> {//APP更新
                            UserActivity.startAction(this, false, true)
                            return@Action1
                        }
                        10 -> {//审核任务
                            if (UserManager.canAudit()) {
                                ZXDialogUtil.showYesNoDialog(this, it.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE), it.getString(JPushInterface.EXTRA_ALERT), "前往处理", "关闭", DialogInterface.OnClickListener { dialog: DialogInterface?, _: Int ->
                                    val reportdataid = if (jsonObj.has("reportdataid")) jsonObj.getString("reportdataid") else ""
                                    val auditid = if (jsonObj.has("auditid")) jsonObj.getString("auditid") else ""
                                    val flownum = if (jsonObj.has("flownum")) jsonObj.getString("flownum") else ""
                                    val auditKind = if (jsonObj.has("auditKind")) jsonObj.getInt("auditKind") else 0
                                    val listBean = AuditListBean(reportdataid = reportdataid, auditid = auditid, auditKind = auditKind, flownum = flownum)
                                    ReportDetailActivity.startAction(this, false, "", listBean)
                                }, null)
                                return@Action1
                            }
                        }
                        11 -> {//审阅任务
                            if (UserManager.canPreview()) {
                                ZXDialogUtil.showYesNoDialog(this, it.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE), it.getString(JPushInterface.EXTRA_ALERT), "前往处理", "关闭", DialogInterface.OnClickListener { dialog: DialogInterface?, _: Int ->
                                    val reportdataid = if (jsonObj.has("reportdataid")) jsonObj.getString("reportdataid") else ""
                                    val auditid = if (jsonObj.has("auditid")) jsonObj.getString("auditid") else ""
                                    val flownum = if (jsonObj.has("flownum")) jsonObj.getString("flownum") else ""
                                    val auditKind = if (jsonObj.has("auditKind")) jsonObj.getInt("auditKind") else 0
                                    val listBean = AuditListBean(reportdataid = reportdataid, auditid = auditid, auditKind = auditKind, flownum = flownum)
                                    ReportDetailActivity.startAction(this, false, "", listBean)
                                }, null)
                                return@Action1
                            }
                        }
                        12 -> {//退回上报
                            if (UserManager.canReport()) {
                                ZXDialogUtil.showYesNoDialog(this, it.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE), it.getString(JPushInterface.EXTRA_ALERT), "前往处理", "关闭", DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                                    val reportdataid = jsonObj.getString("reportdataid")
                                    ReportDetailActivity.startAction(this, false, reportdataid)
                                }, null)
                                return@Action1
                            }
                        }
                        14 -> {//信息发布
                            var data = InfoDeliveryBean(0, 0, jsonObj.optString("publishdept"),
                                    jsonObj.optString("publishtime").toLong(), jsonObj.optString("serviceid"),
                                    "", jsonObj.optString("servicetitle"), 0, -1)
                            InfoReleaseInfoActivity.startAction(this, false, data)
                            return@Action1
                        }
                        16 -> {//下线
//                            var data = InfoDeliveryBean(0, 0, jsonObj.optString("publishdept"),
//                                    jsonObj.optString("publishtime").toLong(), jsonObj.optString("serviceid"),
//                                    "", jsonObj.optString("servicetitle"), 0, -1)
//                            InfoReleaseInfoActivity.startAction(this, false, data)
                            ZXLogUtil.loge("收到单点登录消息")
                            UserActionTool.getBatch(this)
                            return@Action1
                        }
                    }
                }
                if (it.getString(JPushInterface.EXTRA_MESSAGE) != null && !it.getString(JPushInterface.EXTRA_MESSAGE).isEmpty()) {
                    ZXDialogUtil.showInfoDialog(this, it.getString(JPushInterface.EXTRA_TITLE), it.getString(JPushInterface.EXTRA_MESSAGE))
                } else {
                    ZXDialogUtil.showInfoDialog(this, it.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE), it.getString(JPushInterface.EXTRA_ALERT))
                }
            } catch (e: Exception) {
                if (it.getString(JPushInterface.EXTRA_MESSAGE) != null && !it.getString(JPushInterface.EXTRA_MESSAGE).isEmpty()) {
                    ZXDialogUtil.showInfoDialog(this, it.getString(JPushInterface.EXTRA_TITLE), it.getString(JPushInterface.EXTRA_MESSAGE))
                } else {
                    ZXDialogUtil.showInfoDialog(this, it.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE), it.getString(JPushInterface.EXTRA_ALERT))
                }
            }
        })

    }

    /**
     * 判断当前activity是否在栈顶，避免重复处理
     */
    private fun isTopActivity(): Boolean {
        var isTop = false
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val cn = am.getRunningTasks(1)[0].topActivity
        if (cn.className.contains(this.localClassName)) {
            isTop = true
        }
        return isTop
    }

    fun haveQuanXian(qx: String): Boolean {
        if (UserManager.quanxian.isNotEmpty()) {
            return UserManager.quanxian.toString().contains(qx)
        } else {
            return false
        }
    }

    fun getUserType(): Int {
        return if (haveQuanXian(Jurisdiction.new_home)) {
            2
        } else if (haveQuanXian(Jurisdiction.old_home)) {
            0//群测群防，片区负责人
        } else {
            1//地环站，专家，驻守地质队员
        }
    }

    override fun showToast(message: String) {
        ZXToastUtil.showToast(message)
    }

    fun uploadLog(id: Int, type: Int, action: String) {
        UserActionTool.updateUserAction(ActionLog.getActionLog(id, type, action))
    }

    override fun showLoading(message: String) {
        ZXDialogUtil.showLoadingDialog(this, message)
    }

    override fun dismissLoading() {
        ZXDialogUtil.dismissLoadingDialog()
    }


    override fun showLoading(message: String, progress: Int) {
        try {
            ZXDialogUtil.showLoadingDialog(this, message, progress)
        } catch (e: Exception) {
        }
    }

    override fun handleError(code: String?, message: String) {
        if (this !is SplashActivity) {
            showToast(message)
            if (code == "10002" || code == "20006") {
                mSharedPrefUtil.remove("reportBean")
                ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "ReportFile/")
                UserManager.loginOut()
                JPushInterface.stopPush(this)
                if (this !is LoginActivity) {
                    LoginActivity.startAction(this, true, outLogin = code == "20006")
                }
            } else if (code == "100000") {
                changeIp("您的网络状况不佳，是否切换网络？")
            }
        }
    }

    override fun handlerSuccess(message: String?) {

    }

    override fun initView(savedInstanceState: Bundle?) {
        onViewListener()
    }

    abstract fun onViewListener()

    fun getPermission(permissionArray: Array<String>, permessionBack: () -> Unit) {
        this.permessionBack = permessionBack
        this.permissionArray = permissionArray
        if (permissionArray.contains(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (!ZXLocationUtil.isLocationEnabled()) {
                showToast("未开启位置功能，请前往开启")
                ZXLocationUtil.openGPS(this)
                return
            }
        }
        if (!ZXPermissionUtil.checkPermissionsByArray(permissionArray)) {
            ZXPermissionUtil.requestPermissionsByArray(this)
        } else {
            this.permessionBack()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ZXPermissionUtil.checkPermissionsByArray(permissionArray)) {
            permessionBack()
        } else {
            showToast("未获取到系统权限，请先在设置中开启相应权限！")
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (canSwipeBack) ZXSwipeBackHelper.onPostCreate(this)
    }

    override fun onDestroy() {
        super.onDestroy()
//        MyApplication.instance.remove(this.javaClass)
//        if (canSwipeBack) ZXSwipeBackHelper.onDestroy(this)
    }

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    fun initRefresh(sr: SwipeRefreshLayout) {
        mSwipeRefreshLayout = sr
        mSwipeRefreshLayout!!.setColorSchemeColors(Color.BLACK, Color.BLACK, Color.BLACK)
        mSwipeRefreshLayout!!.setOnRefreshListener {
            if (mOnRefreshListener != null) {
                mOnRefreshListener!!.onRefresh()
            } // 执行刷新的操作
        }
    }

    protected var mOnRefreshListener: OnRefreshListener? = null

    interface OnRefreshListener {
        fun onRefresh()
    }

    //服务器切换
    fun changeIp(title: String = "服务器切换") {
        if (dialog != null && dialog!!.isShowing) {
            return
        }
        var items = arrayListOf<String>()
        items.add("主服务器")
        items.add("备用服务器")
        if (!ApiConfigModule.ISRELEASE) {
            items.add("测试线")
        }
        val nowIp = ZXSharedPrefUtil().getString("base_ip", ApiConfigModule.ip_1)
        var select = if (nowIp == ApiConfigModule.ip_test) 2 else if (nowIp == ApiConfigModule.ip_1) 0 else 1
        dialog = ZXDialogUtil.showListDialog(this, "$title （当前服务器：${items[select]}）", "关闭",
                items) { _: DialogInterface?, which: Int ->
            if (which != select) {
                var ip = ""
                if (which == 0) {
                    ip = ApiConfigModule.ip_1
                } else if (which == 1) {
                    ip = ApiConfigModule.ip_2
                } else if (which == 2) {
                    ip = ApiConfigModule.ip_test
                }
                ZXSharedPrefUtil().putString("base_ip", ip)
                MyApplication.instance.init()
                if (which != 2 && select == 2 || which == 2 && select != 2) {
                    //正式环境跟测试环境互相切换 需要重新登录
                    handleError("10002", "切换成功")
                } else {
                    showToast("切换成功")
                }
            }
        }
    }

    var dialog: Dialog? = null

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (isTopActivity() && (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
//            ZXDialogUtil.showInfoDialog(this, "接口", mSharedPrefUtil.getString("request_list"))
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }

    fun isPhoneNum(mobiles: String): Boolean {
        return if (TextUtils.isEmpty(mobiles))
            false
        else
            mobiles.matches("[1][345789]\\d{9}".toRegex())
    }
}
package com.zx.landdisaster.module.main.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.jpush.android.api.JPushInterface
import com.tencent.bugly.crashreport.CrashReport
import com.zx.landdisaster.BuildConfig
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.app.MyApplication
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.bean.Jurisdiction
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.LocationTool
import com.zx.landdisaster.module.dailymanage.ui.ExpertDailyActivity
import com.zx.landdisaster.module.disaster.ui.ReportActivity
import com.zx.landdisaster.module.main.bean.HomeBean
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean
import com.zx.landdisaster.module.main.bean.VersionBean
import com.zx.landdisaster.module.main.bean.WeatherBean
import com.zx.landdisaster.module.main.func.adapter.DayManageAdapter
import com.zx.landdisaster.module.main.func.adapter.InfoDeliveryAdapter
import com.zx.landdisaster.module.main.func.util.RemindTimeUtil
import com.zx.landdisaster.module.main.mvp.contract.HomeContract
import com.zx.landdisaster.module.main.mvp.model.HomeModel
import com.zx.landdisaster.module.main.mvp.presenter.HomePresenter
import com.zx.landdisaster.module.system.ui.UserActivity
import com.zx.landdisaster.module.worklog.bean.DailyRemindBean
import com.zx.landdisaster.module.worklog.ui.DailyReportActivity
import com.zx.zxutils.util.ZXAppUtil
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXSystemUtil
import com.zx.zxutils.util.ZXTimeUtil
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.concurrent.schedule


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class HomeActivity : BaseActivity<HomePresenter, HomeModel>(), HomeContract.View {

    val dataBeans = arrayListOf<InfoDeliveryBean>()
    private val infoDeliveryAdapter = InfoDeliveryAdapter(dataBeans)


    val manageList = arrayListOf<HomeBean>()
    val manageAdapter = DayManageAdapter(manageList)

    val warningList = arrayListOf<HomeBean>()
    val warningAdapter = DayManageAdapter(warningList)
    var hour = -1//当前时间（小时）

    private var gpsTimer: TimerTask? = null
    private val remindTimeUtil = RemindTimeUtil()

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, HomeActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JPushInterface.resumePush(this)
        JPushInterface.setAlias(this, 0, UserManager.getUser().currentUser!!.personid)
        JPushInterface.setTags(this, 0, setOf(if (ApiConfigModule.ISRELEASE) "regular" else "test"))
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    var servicetype = ""
    var haveInfo = true
    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val typeface = Typeface.createFromAsset(assets, "fonts/font.OTF")
        tvInfoRelease.typeface = typeface
        tvDayManage.typeface = typeface
        tvWaring.typeface = typeface
        uploadLog(304, 1, "登入新版首页")
        if (haveQuanXian(Jurisdiction.yjxx)) {
            servicetype += ",1"
        }
        if (haveQuanXian(Jurisdiction.rbzb)) {
            servicetype += ",2"
        }
        if (haveQuanXian(Jurisdiction.hcbg)) {
            servicetype += ",3"
        }
        if (haveQuanXian(Jurisdiction.hstz)) {
            servicetype += ",4"
        }
        if (haveQuanXian(Jurisdiction.zjdd)) {
            servicetype += ",5"
        }
        if (haveQuanXian(Jurisdiction.zcwj)) {
            servicetype += ",6"
        }
        if (haveQuanXian(Jurisdiction.sbtj)) {
            servicetype += ",7"
        }
        if (!servicetype.isEmpty()) {
            servicetype = servicetype.substring(1, servicetype.length)
        } else {
            //无权限
            haveInfo = false
        }
        if (servicetype == "1,2,3,4,5,6,7") {
            servicetype = ""
        }
        getDegree()
        mPresenter.getWeatherdescription()

        rv_info.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = infoDeliveryAdapter

        }
        if (haveQuanXian(Jurisdiction.xxfb_menu) && haveInfo) {
            mPresenter.getFindWeatherdecisionData(ApiParamUtil.getFindWeatherdecisionData(servicetype = servicetype))
            ll_info_manage.visibility = View.VISIBLE
        }
        if (haveQuanXian(Jurisdiction.rcgl_menu)) {
            ll_day_manage.visibility = View.VISIBLE
        }

        if (haveQuanXian(Jurisdiction.jcyj_menu)) {
            ll_yujing_manage.visibility = View.VISIBLE
        }

        addPageData()

        rv_management.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity).apply { orientation = LinearLayoutManager.HORIZONTAL }
            adapter = manageAdapter
        }
        rv_warning.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity).apply { orientation = LinearLayoutManager.HORIZONTAL }
            adapter = warningAdapter
        }
        initRefresh(swipeRefreshLayout)
        mOnRefreshListener = object : OnRefreshListener {
            override fun onRefresh() {
                getDegree()
                mPresenter.getWeatherdescription()
                mPresenter.getFindWeatherdecisionData(ApiParamUtil.getFindWeatherdecisionData(servicetype = servicetype))
            }
        }

        startLocationUpdate()

        if (BuildConfig.RELEASE) {
            CrashReport.setUserId(UserManager.getUser().currentUser!!.personid)
        }

        mPresenter.getVerson()
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
            if (UserManager.getUser().personRole.ringStand) {
                remindTimeUtil.checkTime(UserManager.getUser().dailySafeScope) {
                    mPresenter.getDailyRemind()
                }
            }
        }
    }

    private fun uploadGPS() {
        getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Thread {
                LocationTool.getLocation(this@HomeActivity, true) {
                    if (it != null) {
                        mPresenter.updateLocation(it.longitude.toString(), it.latitude.toString())
                    }
                }
            }.start()
        }
    }

    private fun addPageData() {
        if (haveQuanXian(Jurisdiction.yhd))
            manageList.add(HomeBean("隐患点", R.drawable.icon_home_address, R.drawable.shape_home1))
        if (haveQuanXian(Jurisdiction.zxq))
            manageList.add(HomeBean("灾险情", R.drawable.icon_home_zxq, R.drawable.shape_home2))
        if (haveQuanXian(Jurisdiction.qxsb))
            manageList.add(HomeBean("区县上报", R.drawable.icon_home_qxsb, R.drawable.shape_home3))
        if (haveQuanXian(Jurisdiction.scjg))
            manageList.add(HomeBean("“三查”监管", R.drawable.icon_home_scjg, R.drawable.shape_home4))
        if (haveQuanXian(Jurisdiction.zbdd))
            manageList.add(HomeBean("值班调度", R.drawable.icon_home_zbdd, R.drawable.shape_home5))
        if (haveQuanXian(Jurisdiction.zjzc))
            manageList.add(HomeBean("专家支撑", R.drawable.icon_home_zjzc, R.drawable.shape_home6))
//        if (haveQuanXian(Jurisdiction.zjrb))
//            manageList.add(HomeBean("专家日报", R.drawable.icon_home_zjzc, R.drawable.shape_home6))

        if (haveQuanXian(Jurisdiction.dzfx))
            warningList.add(HomeBean("地灾风险", R.drawable.icon_home_dzfx, R.drawable.shape_home2))
        if (haveQuanXian(Jurisdiction.yl))
            warningList.add(HomeBean("实况降雨", R.drawable.icon_home_skjy, R.drawable.shape_home3))
        if (haveQuanXian(Jurisdiction.qxyb))
            warningList.add(HomeBean("气象预报", R.drawable.icon_home_qxyb, R.drawable.shape_home1))
        if (haveQuanXian(Jurisdiction.dlyj))
            warningList.add(HomeBean("短临预警", R.drawable.icon_home_dlyj, R.drawable.shape_home6))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        tv_address.setOnClickListener {
            getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                LocationTool.getLocation(this@HomeActivity) {
                    if (it != null) {
                        tv_address.text = it.address
                    } else {
                        tv_address.text = "重庆市"
                    }
                }
            }
        }
        iv_user.setOnClickListener {
            UserActivity.startAction(this, false)
        }
        tv_more.setOnClickListener {
            InfoReleaseActivity.startAction(this, false)
        }
        infoDeliveryAdapter.setOnItemClickListener { _, _, position ->
            InfoReleaseInfoActivity.startAction(this, false, dataBeans[position])
        }

        manageAdapter.setOnItemClickListener { _, _, position ->
            when (manageList[position].title) {
                "隐患点" -> this.let {
                    MainActivity.startAction(this, false, 1)
                    uploadLog(306, 1, "查看隐患点图层")
                }
                "灾险情" -> this.let {
                    MainActivity.startAction(this, false, 2)
                    uploadLog(306, 4, "查看灾险情图层")
                }
                "区县上报" -> this.let {
                    ReportActivity.startAction(this, false)
                }
                "“三查”监管" -> this.let {
                    //                    AuditHomeActivity.startAction(this, false)
                    showToast("正在建设中")
                    uploadLog(306, 7, "查看“三查”监管")
                }
                "值班调度" -> this.let {
                    showToast("正在建设中")
                    uploadLog(306, 8, "查看值班调度")
                }
                "专家支撑" -> this.let {
                    showToast("正在建设中")
                    uploadLog(306, 9, "查看专家支撑")
                }
                "专家日报" -> this.let {
                    ExpertDailyActivity.startAction(this, false)
                    uploadLog(306, 10, "查看专家日报")
                }
            }
        }
        warningAdapter.setOnItemClickListener { _, _, position ->
            when (warningList[position].title) {
                "地灾风险" -> this.let {
                    MainActivity.startAction(this, false, 4)
                    uploadLog(305, 1, "查看风险预警图层")
                }
                "实况降雨" -> this.let {
                    MainActivity.startAction(this, false, 3)
                    uploadLog(305, 2, "查看实况降雨图层")
                }
                "气象预报" -> this.let {
                    MainActivity.startAction(this, false, 5)
                    uploadLog(305, 4, "查看气象预报图层")
                }
                "短临预警" -> this.let {
                    showToast("正在建设中")
//                    uploadLog(305, 5, "查看短临预警图层")
                }
            }
        }
    }

    var time = 1//更新频率

    override fun onResume() {
        super.onResume()
        var now = ZXTimeUtil.getCurrentTime().substring(11, 13).toInt()
        if (now == 0) {
            now = 24
        }
        if (now - hour >= time || !tv_weather.text.isEmpty() || !tv_temperature.text.isEmpty() || !tv_content.text.isEmpty()) {
            getDegree()
            mPresenter.getWeatherdescription()
        }
        mPresenter.getFindWeatherdecisionData(ApiParamUtil.getFindWeatherdecisionData(servicetype = servicetype))
    }

    override fun onDegreeResult(data: WeatherBean?) {
        if (data == null) {
            if (hour < 0) {
                getDegree()
            }
            return
        }
        val t = data.msg.split("，")
        if (t.size > 1) {
            tv_weather.text = t[0]
            var du = t[1].replace("&#8451;", "").split(".")[0]
            tv_temperature.text = "$du℃"
            var time = "(" + time + "小时更新)"
            hour = ZXTimeUtil.getCurrentTime().substring(11, 13).toInt()
            tv_frequency.text = time
        }
    }

    override fun onWeatherdescriptionResult(data: String?) {
        if (data == null) {
            tv_content.text = "暂无天气信息。"
            return
        }
        tv_content.text = JSONObject(data).optString("description")
    }

    override fun onFindWeatherdecisionResult(list: NormalList<InfoDeliveryBean>?) {
        if (list != null) {
            dataBeans.clear()
            dataBeans.addAll(list.result!!)
            if (dataBeans.size == 3) {
                rv_info.minimumHeight = 120
            }
            infoDeliveryAdapter.notifyDataSetChanged()
        }
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onBackPressed() {
        startActivity(Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_HOME)
        })
    }

    //获取天气，温度
    fun getDegree() {
        getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationTool.getLocation(this@HomeActivity) {
                try {
                    if (it != null) {
                        tv_address.text = it.address ?: "重庆市"
                        mPresenter.getDegree("https://weather.mipang.com/index/ajax?act=getPointWeather&lat=" + it.latitude + "&lon=" + it.longitude)
                    } else {
                        tv_address.text = "重庆市"
                        mPresenter.getDegree("https://weather.mipang.com/index/ajax?act=getPointWeather&lat=" + 29.873485291008127 + "&lon=" + 107.65253363384483)
                    }
                } catch (e: Exception) {

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
        ZXAppUtil.installApp(this, file.absolutePath)
    }

    override fun onDestroy() {
        if (gpsTimer != null) {
            gpsTimer!!.cancel()
        }
        super.onDestroy()
    }

    override fun onRemindResult(reminBean: DailyRemindBean) {
        if (!reminBean.isReportsafety && !reminBean.isReportDD && !reminBean.isOtherReport) {
            ZXDialogUtil.dismissDialog()
            ZXDialogUtil.showYesNoDialog(this, "提示", "今日还未填写日报，是否前往填写？") { _, _ ->
                DailyReportActivity.startAction(this, false)
            }

        }
    }
}

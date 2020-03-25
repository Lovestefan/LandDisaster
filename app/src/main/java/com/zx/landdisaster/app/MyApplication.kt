package com.zx.landdisaster.app

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.StrictMode
import cn.jpush.android.api.JPushInterface
import com.frame.zxmvp.baseapp.BaseApplication
import com.frame.zxmvp.di.component.AppComponent
import com.tencent.bugly.crashreport.CrashReport
import com.zx.landdisaster.BuildConfig
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.zxutils.ZXApp
import com.zx.zxutils.util.ZXFileUtil
import com.zx.zxutils.util.ZXSharedPrefUtil
import com.zx.zxutils.util.ZXSystemUtil
import rx.Subscription
import java.util.*

/**
 * Created by Xiangb on 2019/2/26.
 * 功能：
 */
open class MyApplication : BaseApplication() {
    companion object {
        lateinit var instance: MyApplication

//        fun getInstance(): MyApplication {
//            return
//        }
    }


    lateinit var mSharedPrefUtil: ZXSharedPrefUtil

    lateinit var mContest: Context

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()

//        ZXApp.init(this, true)
        ZXApp.init(this, !BuildConfig.RELEASE)

        //配置极光推送
        JPushInterface.setDebugMode(!BuildConfig.RELEASE)
        JPushInterface.init(this)//初始化JPush
        JPushInterface.setLatestNotificationNumber(this, 1)

//        RetrofitUrlManager.getInstance().putDomain("debug", "http://192.168.11.230:8666/")
//        RetrofitUrlManager.getInstance().putDomain("realease", "http://183.230.8.9:4001/")

        //配置Bugly
        if (BuildConfig.RELEASE) {
            CrashReport.initCrashReport(this, "06f8c8d295", false)
        }

        mSharedPrefUtil = ZXSharedPrefUtil()
        instance = this
        mContest = this

        if (ZXSharedPrefUtil().getInt("urlVerson", ApiConfigModule.urlVerson) < ApiConfigModule.urlVerson) {
            //重置为正式环境
            ZXSharedPrefUtil().putString("base_ip", ApiConfigModule.ip_1)
            ZXSharedPrefUtil().putInt("urlVerson", ApiConfigModule.urlVerson)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }

        //初始化
        ConstStrings.LOCAL_PATH = ZXSystemUtil.getSDCardPath()
        ConstStrings.INI_PATH = filesDir.path
        ZXFileUtil.deleteFiles(ConstStrings.getApkPath())
        ZXFileUtil.deleteFiles(ConstStrings.getOnlinePath())
        init()
    }

    fun init() {
        initAppDelegate(this)
        component = appComponent
    }


    var observable: Subscription? = null


    private val activityList = ArrayList<Activity>()

    fun recreateView() {
        activityList.forEach {
            it.recreate()
        }
    }

    // 添加Activity到容器中
    override fun addActivity(activity: Activity) {
        if (activityList.size > 0 && activityList[activityList.size - 1].javaClass == activity.javaClass) {
            return
        }
        activityList.add(activity)
    }

    // 遍历所有Activity并finish
    override fun exit() {
        for (activity in activityList) {
            activity.finish()
        }
        //		App.getInstance().destroyMap();
        System.exit(0)
    }


    //销毁某个activity实例
    override fun remove(t: Class<out Activity>) {
        for (i in activityList.indices.reversed()) {
            if (activityList[i].javaClass == t) {
                activityList[i].finish()
                return
            }
        }
    }

    // 遍历所有Activity并finish

    fun finishAll() {
        for (activity in activityList) {
            activity.finish()
        }
        //		App.getInstance().destroyMap();
    }

    override fun haveActivity(t: Class<out Activity>): Boolean {
        for (activity in activityList) {
            if (activity.javaClass == t) {
                return true
            }
        }
        return false
    }

    override fun clearActivityList() {
        for (activity in activityList) {
            activity.finish()
        }
        activityList.clear()
    }


    override fun getActivityList(): ArrayList<Activity> {
        return activityList
    }

}
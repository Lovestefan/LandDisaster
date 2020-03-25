package com.zx.landdisaster.module.system.ui

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import cn.jpush.android.api.JPushInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.app.ConstStrings
import com.zx.landdisaster.app.MyApplication
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.main.bean.FuncBean
import com.zx.landdisaster.module.main.bean.VersionBean
import com.zx.landdisaster.module.main.func.adapter.FuncAdapter
import com.zx.landdisaster.module.system.mvp.contract.UserContract
import com.zx.landdisaster.module.system.mvp.model.UserModel
import com.zx.landdisaster.module.system.mvp.presenter.UserPresenter
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter
import com.zx.zxutils.util.*
import kotlinx.android.synthetic.main.activity_user.*
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class UserActivity : BaseActivity<UserPresenter, UserModel>(), UserContract.View {

    private var funcBeans = arrayListOf<FuncBean>()

    private var listAdapter = FuncAdapter(funcBeans)

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, checkUpdate: Boolean = false) {
            val intent = Intent(activity, UserActivity::class.java)
            intent.putExtra("checkUpdate", checkUpdate)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_user
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        rv_user_func.apply {
            //            funcBeans.add(FuncBean("数据统计", R.drawable.func_statistcs))
//            funcBeans.add(FuncBean("审核记录", R.drawable.func_auditlist, true))
//            funcBeans.add(FuncBean("系统设置", R.drawable.func_setting, true))
            funcBeans.add(FuncBean("检查下载", R.drawable.func_checkdown))
            funcBeans.add(FuncBean("版本历史", R.drawable.func_version))
            funcBeans.add(FuncBean("消息历史", R.drawable.func_message))
            funcBeans.add(FuncBean("清除缓存", R.drawable.func_clearcache))
            funcBeans.add(FuncBean("服务热线", R.drawable.toolbar_telc))
            if (UserManager.getUser().personRole.areaManager || UserManager.getUser().personRole.groupDefense
                    || UserManager.getUser().personRole.garrison || UserManager.getUser().personRole.ringStand) {
                funcBeans.add(FuncBean("工作职责", R.drawable.func_duty))
            }
            funcBeans.add(FuncBean("意见反馈", R.drawable.func_feedback, true))

            funcBeans.add(FuncBean("服务器切换", R.drawable.func_net))
            funcBeans.add(FuncBean("修改密码", R.drawable.func_changepwd))
            funcBeans.add(FuncBean("注销登录", R.drawable.func_logout))
            layoutManager = LinearLayoutManager(this@UserActivity) as RecyclerView.LayoutManager?
            adapter = listAdapter
        }
        Glide.with(this)
                .load(ApiConfigModule.BASE_IP + ApiConfigModule.URL_FILE + "person/personHeader/" + UserManager.getUser().currentUser?.personid)
                .apply(RequestOptions().error(R.drawable.default_headicon))
                .into(iv_user_headicon)
        tv_user_accunt.text = UserManager.getUser().currentUser?.name
        tv_user_tel.text = UserManager.getUser().currentUser?.telephone
        tv_user_dept.text = UserManager.getUser().currentUser?.orgName

        if (intent.getBooleanExtra("checkUpdate", false)) {
            mPresenter.getVerson()
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        listAdapter.onItemClickListener = object : ZXQuickAdapter.OnItemClickListener {
            override fun onItemClick(adapter: ZXQuickAdapter<*, *>?, view: View?, position: Int) {
                when (funcBeans[position].title) {
//                    "数据统计"->{
//                        CountActivity.startAction(this@AccountActivity,false)
//                    }
                    "系统设置" -> {
                        SettingActivity.startAction(this@UserActivity, false)
                    }
                    "版本历史" -> {
                        VersionRecordActivity.startAction(this@UserActivity, false)
                    }
                    "消息历史" -> {
                        uploadLog(301, 8, "查看了消息历史")
                        MessageRecordActivity.startAction(this@UserActivity, false)
                    }
                    "清除缓存" -> {
                        showLoading("正在清理")
                        ZXAppUtil.cleanInternalCache()
                        ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "ReportFile/")
                        ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "Download/")
                        ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "Pdf_file/")
                        handler.postDelayed({
                            dismissLoading()
                            showToast("清理完成")
                            uploadLog(301, 5, "清除了缓存")
                        }, 1000)
                    }
                    "工作职责" -> {
                        uploadLog(301, 9, "查看了工作职责")
                        WorkDutyActivity.startAction(this@UserActivity, false)
                    }
                    "修改密码" -> {
                        ChangePwdActivity.startAction(this@UserActivity, false)
                    }
                    "服务热线" -> {
                        uploadLog(301, 6, "点击了服务热线")
                        ZXDialogUtil.showYesNoDialog(this@UserActivity, "提示", "是否要发起电话帮助?") { dialog, which ->
                            ZXSystemUtil.callTo(this@UserActivity, resources.getString(R.string.toolbar_tel_string))
                        }
                    }
                    "服务器切换" -> {
//                        ChangeIPActivity.startAction(this@UserActivity, false, true)
                        changeIp()
                    }
                    "注销登录" -> {
                        ZXDialogUtil.showYesNoDialog(this@UserActivity, "提示", "是否注销登录？") { dialog, which ->
                            uploadLog(301, 2, "注销登录")
                            mPresenter.loginOut()
                        }
                    }
                    "检查下载" -> {
                        uploadLog(301, 4, "检查了是否能更新")
                        mPresenter.getVerson()
                    }
                    else -> {
                        ZXToastUtil.showToast("功能正在开发中")
                    }
                }
            }
        }
    }

    /**
     * 版本检测
     */
    override fun onVersionResult(versionBean: VersionBean?) {
        if (versionBean == null) {
            showToast("暂未检查到更新")
        } else {
            val versionCode = ZXSystemUtil.getVersionCode()
            if (versionBean.version != null && versionCode < versionBean.version!!.toInt()) {
                ZXDialogUtil.showYesNoDialog(mContext, "提示", "当前应用需要下载更新\n版本号:${versionBean.versionName}\n内容:${versionBean.content}", "下载", "关闭", { dialog, which ->
                    getPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mPresenter.downloadApk(versionBean.path)
                    }
                }, { dialog, which ->
                    if (versionBean.isForce) {
                        showToast("请先更新后使用")
                        MyApplication.instance.exit()
                    }
                }, false)
            } else {
                showToast("暂未检查到更新")
            }
        }
    }

    /**
     * apk下载回调
     */
    override fun onApkDownloadResult(file: File) {
        ZXAppUtil.installApp(this, file)
    }

    override fun onLoginOutResult() {
        JPushInterface.stopPush(this)
        mSharedPrefUtil.remove("reportBean")
        ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "ReportFile/")
        UserManager.loginOut()
        LoginActivity.startAction(this, true)
    }
}

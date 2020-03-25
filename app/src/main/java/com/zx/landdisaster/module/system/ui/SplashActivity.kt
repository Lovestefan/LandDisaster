package com.zx.landdisaster.module.system.ui;

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.bean.Jurisdiction
import com.zx.landdisaster.base.bean.UserBean
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.ImeiCodeTool
import com.zx.landdisaster.base.tool.UserActionTool
import com.zx.landdisaster.module.disaster.bean.PersonRoleBean
import com.zx.landdisaster.module.main.ui.HomeActivity
import com.zx.landdisaster.module.main.ui.MainActivity
import com.zx.landdisaster.module.main.ui.MainOtherActivity
import com.zx.landdisaster.module.system.mvp.contract.SplashContract
import com.zx.landdisaster.module.system.mvp.model.SplashModel
import com.zx.landdisaster.module.system.mvp.presenter.SplashPresenter
import com.zx.zxutils.util.ZXMD5Util
import com.zx.zxutils.util.ZXSystemUtil
import kotlinx.android.synthetic.main.activity_splash.*


/**
 * Create By admin On 2017/7/11
 * 功能：欢迎页
 */
class SplashActivity : BaseActivity<SplashPresenter, SplashModel>(), SplashContract.View {

    override var canSwipeBack: Boolean = false

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, SplashActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        val user = UserManager.getUser()

        if (UserManager.userName.isEmpty() || UserManager.passWord.isEmpty()) {
            LoginActivity.startAction(this, true)
        } else {
            UserActionTool.getBatch(this)
        }

        tv_splash_version.text = "版本号 V${ZXSystemUtil.getVersionName()}"
    }

    override fun handleError(code: String?, message: String) {
        super.handleError(code, message)
        if (code == "10002") {
            //自动登录
            getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE)) {
                mPresenter.doLogin(ApiParamUtil.loginParam(UserManager.userName, if (UserManager.passWord.length == 32) UserManager.passWord else ZXMD5Util.getMD5(UserManager.passWord), ImeiCodeTool.getIMEI()))
            }
        } else {
            LoginActivity.startAction(this, true, showDialog = code == "100000")
        }
    }

    override fun handlerSuccess(message: String?) {
        super.handlerSuccess(message)
        //直接进入首页
        if (haveQuanXian(Jurisdiction.new_home)) {
            getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                HomeActivity.startAction(this, false)
            }
        } else if (haveQuanXian(Jurisdiction.old_home)) {
            MainOtherActivity.startAction(this, false)//群测群防，片区负责人
        } else {
            //地环站，专家，驻守地质队员
            MainActivity.startAction(this, false)
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    /**
     * 登录返回
     */
    override fun onLoginResult(userBean: UserBean) {
        UserManager.setUser(userBean)
    }

    /**
     * 用户角色回调
     */
    override fun onPersonRoleResult(roleBean: PersonRoleBean) {
        UserManager.getUser().personRole = roleBean
//                .apply {
//            expert = UserManager.getUser()!!.currentUser!!.identities.contains(",5,")
//        }
        uploadLog(301, 1, "登录到系统")

        mPresenter.checkPassword(ApiParamUtil.checkPassword(UserManager.passWord))
    }

    override fun onCheckResult(data: String) {
        if (data.toInt() < 1) {
            //强制改密码
            ChangePwdActivity.startAction(this, false)
//                finish()
        } else {
            mPresenter.currentAuthMenu(ApiParamUtil.currentAuthMenu())
        }
    }

    override fun onCurrentAuthMenuResult(list: List<String>) {
        UserManager.quanxian = list
        //TODO:
        //跳过权限 直接将进入群测群防角色相关功能
        if (haveQuanXian(Jurisdiction.new_home)) {
            getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                HomeActivity.startAction(this, false)
            }
        } else if (haveQuanXian(Jurisdiction.old_home)) {
            MainOtherActivity.startAction(this, false)//群测群防，片区负责人
        } else {
            //地环站，专家，驻守地质队员
            MainActivity.startAction(this, false)
        }

    }

    override fun onLoginError(code: String?) {
        UserManager.loginOut()
        LoginActivity.startAction(this, true, showDialog = code == "100000")
    }
}

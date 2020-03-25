package com.zx.landdisaster.module.system.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import com.frame.zxmvp.baseapp.BaseApplication
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.app.MyApplication
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.bean.Jurisdiction
import com.zx.landdisaster.base.bean.UserBean
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.ImeiCodeTool
import com.zx.landdisaster.module.disaster.bean.PersonRoleBean
import com.zx.landdisaster.module.main.ui.HomeActivity
import com.zx.landdisaster.module.main.ui.MainActivity
import com.zx.landdisaster.module.main.ui.MainOtherActivity
import com.zx.landdisaster.module.system.mvp.contract.LoginContract
import com.zx.landdisaster.module.system.mvp.model.LoginModel
import com.zx.landdisaster.module.system.mvp.presenter.LoginPresenter
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter
import com.zx.zxutils.util.ZXMD5Util
import com.zx.zxutils.util.ZXSystemUtil
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


/**
 * Create By admin On 2017/7/11
 * 功能：登录
 */
class LoginActivity : BaseActivity<LoginPresenter, LoginModel>(), LoginContract.View {

    override var canSwipeBack: Boolean = false

    private var showUsernameList = arrayListOf<String>()

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, outLogin: Boolean = false, showDialog: Boolean = false) {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.putExtra("outLogin", outLogin)
            intent.putExtra("showDialog", showDialog)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        et_login_username.setText(UserManager.userName)
        et_login_username.setSelection(et_login_username.text.length)
        if (intent.getBooleanExtra("outLogin", false)) {
            mPresenter.loginOut()
        }
        UserManager.passWord = ""
        tv_login_version.text = "版本号 V${ZXSystemUtil.getVersionName()}"

        rv_login_userlist.layoutManager = LinearLayoutManager(this)
        rv_login_userlist.adapter = object : ZXQuickAdapter<String, ZXBaseHolder>(R.layout.item_login_usernamepop, showUsernameList) {
            override fun convert(helper: ZXBaseHolder?, item: String?) {
                helper!!.setText(R.id.tv_login_username_pop, item!!)
            }
        }.apply {
            setOnItemClickListener { _, _, position ->
                et_login_username.setText(showUsernameList[position])
                et_login_username.setSelection(et_login_username.text.length)
                rv_login_userlist.visibility = View.GONE
                et_login_pwd.requestFocus()
            }
        }
        tv_code_login.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        tv_change_pwd.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        tv_register.paint.flags = Paint.UNDERLINE_TEXT_FLAG

        BaseApplication.baseApplication.removeOther(this)


        if (intent.getBooleanExtra("showDialog", false)) {
            changeIp("您的网络状况不佳，是否切换网络？")
        }
    }

    var time = 60
    //验证码回调
    override fun onSendLoginCheckResult(data: String) {
        if (data == "error") {
            tv_sendCode.isEnabled = true
            return
        } else if (data != "djs") showToast("发送成功")
        if (time == -1) {
            tv_sendCode.isEnabled = true
            return
        }
        if (time > 0) {
            tv_sendCode.text = "重新发送 $time s"
            tv_sendCode.isEnabled = false
            Handler().postDelayed({
                time--
                onSendLoginCheckResult("djs")
            }, 1000)
        } else {
            tv_sendCode.text = "重新发送"
            tv_sendCode.isEnabled = true
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        iv_logo.setOnClickListener {
//            changeIp()
        }
        //验证码/用户名 登录
        tv_code_login.setOnClickListener {
            if (tv_code_login.text == "验证码登录") {
                tv_code_login.text = "用户名登录"
                et_login_username.hint = "请输入手机号"
                ll_name_login.visibility = View.GONE
                ll_code_login.visibility = View.VISIBLE
            } else {
                tv_code_login.text = "验证码登录"
                et_login_username.hint = "请输入用户名"
                ll_name_login.visibility = View.VISIBLE
                ll_code_login.visibility = View.GONE

            }
        }
        //发送验证码
        tv_sendCode.setOnClickListener {
            if (et_login_username.text.isNotEmpty()) {
                tv_sendCode.isEnabled = false
                time = 60
                mPresenter.sendLoginCheck(ApiParamUtil.sendUpdatePwd(et_login_username.text.toString()))
            } else {
                showToast("请输入手机号")
                tv_sendCode.isEnabled = true
            }
        }
        //忘记密码
        tv_change_pwd.setOnClickListener {
            ChangePwdActivity.startAction(this, false, true)
        }
        //注册
        tv_register.setOnClickListener {
            RegisterActivity.startAction(this, false)
        }
        //登录
        btn_login_do.setOnClickListener {
            rv_login_userlist.visibility = View.GONE
            if (tv_code_login.text == "验证码登录") {
                if (et_login_username.text.isEmpty()) {
                    showToast("请输入用户名")
                } else if (et_login_pwd.text.isEmpty()) {
                    showToast("请输入密码")
                } else {
                    getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE)) {
                        mPresenter.doLogin(ApiParamUtil.loginParam(et_login_username.text.toString(), ZXMD5Util.getMD5(et_login_pwd.text.toString()), ImeiCodeTool.getIMEI()))
                    }
                }
            } else {
                if (et_login_username.text.isEmpty()) {
                    showToast("请输入手机号")
                } else if (!isPhoneNum(et_login_username.text.toString())) {
                    showToast("请输入正确的手机号")
                } else if (et_login_code.text.isEmpty()) {
                    showToast("请输入验证码")
                } else {
                    getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE)) {
                        mPresenter.loginAppBySms(ApiParamUtil.loginAppBySms(et_login_username.text.toString(), et_login_code.text.toString(), ImeiCodeTool.getIMEI()))
                    }
                }
            }
            try {
                ZXSystemUtil.closeKeybord(this)
            } catch (e: Exception) {
            }
        }

        //设置密码可见按钮点击事件
        ll_login_showpwd.setOnTouchListener { v, event ->
            rv_login_userlist.visibility = View.GONE
            if (event.action == MotionEvent.ACTION_DOWN) {
                et_login_pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
                et_login_pwd.setSelection(et_login_pwd.text.toString().length)
            } else if (event.action == MotionEvent.ACTION_UP) {
                et_login_pwd.transformationMethod = PasswordTransformationMethod.getInstance()
                et_login_pwd.setSelection(et_login_pwd.text.toString().length)
            }
            return@setOnTouchListener true
        }

        et_login_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1 || before == 1) {
                    showUsernamePop()
                }
            }
        })
        et_login_username.setOnTouchListener { v, event ->
            showUsernamePop()
            return@setOnTouchListener false
        }
        ll_contentView.setOnTouchListener { v, event ->
            rv_login_userlist.visibility = View.GONE
            try {
                ZXSystemUtil.closeKeybord(this)
            } catch (e: Exception) {

            }
            return@setOnTouchListener false
        }
    }

    private fun showUsernamePop() {
        if (!mSharedPrefUtil.contains("usernameList")) {
            rv_login_userlist.visibility = View.GONE
            return
        }
        showUsernameList.clear()
        val usernameList = mSharedPrefUtil.getList<String>("usernameList")
        usernameList.forEach {
            if (it.startsWith(et_login_username.text.toString())) {
                showUsernameList.add(it)
            }
        }
        if (showUsernameList.isEmpty()) {
            rv_login_userlist.visibility = View.GONE
            return
        }
        rv_login_userlist.adapter?.notifyDataSetChanged()
        rv_login_userlist.visibility = View.VISIBLE
    }

    /**
     * 登录返回
     */
    override fun onLoginResult(userBean: UserBean) {
        if (tv_code_login.text == "验证码登录") {
            userBean.currentUser!!.account = et_login_username.text.toString()
            userBean.currentUser!!.pwd = ZXMD5Util.getMD5(et_login_pwd.text.toString())
        }
        UserManager.userName = userBean.currentUser!!.account
        UserManager.passWord = userBean.currentUser!!.pwd
        UserManager.setUser(userBean)
        time = -1
    }

    /**
     * 用户角色回调
     */
    override fun onPersonRoleResult(roleBean: PersonRoleBean) {
        UserManager.getUser().personRole = roleBean.apply {
            expert = UserManager.getUser().currentUser!!.identities.contains(",5,")
        }
        val usernameList: ArrayList<String>
        if (mSharedPrefUtil.contains("usernameList")) {
            usernameList = mSharedPrefUtil.getList<String>("usernameList") as ArrayList<String>
        } else {
            usernameList = arrayListOf()
        }
        if (!usernameList.contains(et_login_username.text.toString())) {
            usernameList.add(et_login_username.text.toString())
        }
        mSharedPrefUtil.putList("usernameList", usernameList)

        if (tv_code_login.text == "验证码登录") {
            uploadLog(301, 1, "用“用户名登录”的方式登录到系统")
            mPresenter.checkPassword(ApiParamUtil.checkPassword(et_login_pwd.text.toString()))
        } else {
            uploadLog(301, 1, "用“验证码登录”的方式登录到系统")
            mPresenter.currentAuthMenu(ApiParamUtil.currentAuthMenu())
        }
//        if (UserManager.getUser().password.equals("123456")) {
//            //强制改密码
//            ChangePwdActivity.startAction(this, false)
//            return
//        }
//        if (roleBean.groupDefense || roleBean.areaManager) {
//            MainOtherActivity.startAction(this, false)
//        } else {
//            MainActivity.startAction(this, true)
//        }
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
        if (list != null)
            UserManager.quanxian = list
//        handler.postDelayed({
        //TODO:
        //跳过权限 直接将进入群测群防角色相关功能
        if (haveQuanXian(Jurisdiction.new_home)) {
            getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                HomeActivity.startAction(this, true)
            }
        } else if (haveQuanXian(Jurisdiction.old_home)) {
            MainOtherActivity.startAction(this, true)//群测群防，片区负责人
        } else {
            //地环站，专家，驻守地质队员
            MainActivity.startAction(this, true)
        }

//        }, 100)
    }

    override fun onLoginError() {
        UserManager.loginOut()
//        et_login_pwd.setText("")
    }

    override fun onBackPressed() {
        MyApplication.instance.finishAll()
    }

    override fun onLoginOutResult() {

    }
}

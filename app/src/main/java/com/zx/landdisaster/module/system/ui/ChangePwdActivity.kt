package com.zx.landdisaster.module.system.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.tool.ActionLog
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.system.mvp.contract.ChangePwdContract
import com.zx.landdisaster.module.system.mvp.model.ChangePwdModel
import com.zx.landdisaster.module.system.mvp.presenter.ChangePwdPresenter
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXMD5Util
import kotlinx.android.synthetic.main.activity_change_pwd.*


/**
 * Create By admin On 2019/3/6
 * 功能：修改密码
 */
class ChangePwdActivity : BaseActivity<ChangePwdPresenter, ChangePwdModel>(), ChangePwdContract.View {

    val views = arrayListOf<View>()
    var isSubmit = false

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, isFindPwd: Boolean = false) {
            val intent = Intent(activity, ChangePwdActivity::class.java)
            intent.putExtra("isFindPwd", isFindPwd)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_change_pwd
    }

    var isFindPwd = false
    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        if (intent.getBooleanExtra("isFindPwd", false)) {
            isFindPwd = true
            tbv_title.setMidText("找回密码")
            btn_change_next.text = "获取验证码"
            ll_findPwd.visibility = View.VISIBLE
            ll_updatePwd.visibility = View.GONE
        } else if (UserManager.passWord == ZXMD5Util.getMD5("123456")) {
            tvTip.visibility = View.VISIBLE
        }
        tv_updatePwd.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        tv_findPwd.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        views.add(v1)
        views.add(v2)
        views.add(v3)
        views.add(v4)
        views.add(v5)
        views.add(v6)
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //修改密码
        tv_updatePwd.setOnClickListener {
            isFindPwd = false
            tbv_title.setMidText("密码修改")
            btn_change_next.text = "确认修改"
            ll_findPwd.visibility = View.GONE
            ll_updatePwd.visibility = View.VISIBLE
        }
        //忘记密码
        tv_findPwd.setOnClickListener {
            isFindPwd = true
            tbv_title.setMidText("找回密码")
            btn_change_next.text = "获取验证码"
            ll_findPwd.visibility = View.VISIBLE
            ll_updatePwd.visibility = View.GONE
        }
        //设置删除按钮的可见性
        et_change_pwd1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (et_change_pwd1.text.toString().isEmpty()) {
                    ll_change_deletepwd1.visibility = View.GONE
                } else {
                    ll_change_deletepwd1.visibility = View.VISIBLE
                }
            }
        })
        et_change_pwd2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (et_change_pwd2.text.toString().isEmpty()) {
                    ll_change_deletepwd2.visibility = View.GONE
                } else {
                    ll_change_deletepwd2.visibility = View.VISIBLE
                }
                if (s.toString().length >= 6) {
                    llPwd.visibility = View.VISIBLE
                    isSubmit = false
                    mPresenter.checkPassword(ApiParamUtil.checkPassword(s.toString()))
                } else {
                    llPwd.visibility = View.GONE
                    setColor(0, 0)
                }

                if (!et_change_pwd3.text.toString().isEmpty() && !s.toString().isEmpty() && s.toString() != et_change_pwd3.text.toString()) {
                    tv_tip_pwd.visibility = View.VISIBLE
                } else {
                    tv_tip_pwd.visibility = View.GONE
                }
            }
        })
        et_change_pwd3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!et_change_pwd2.text.toString().isEmpty() && !s.toString().isEmpty() && s.toString() != et_change_pwd2.text.toString()) {
                    tv_tip_pwd.visibility = View.VISIBLE
                } else {
                    tv_tip_pwd.visibility = View.GONE
                }
            }
        })

        ll_change_showpwd1.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                et_change_pwd1.transformationMethod = HideReturnsTransformationMethod.getInstance()
                et_change_pwd1.setSelection(et_change_pwd1.text.toString().length)
            } else if (event.action == MotionEvent.ACTION_UP) {
                et_change_pwd1.transformationMethod = PasswordTransformationMethod.getInstance()
                et_change_pwd1.setSelection(et_change_pwd1.text.toString().length)
            }
            return@setOnTouchListener true
        }
        ll_change_showpwd2.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                et_change_pwd2.transformationMethod = HideReturnsTransformationMethod.getInstance()
                et_change_pwd2.setSelection(et_change_pwd2.text.toString().length)
            } else if (event.action == MotionEvent.ACTION_UP) {
                et_change_pwd2.transformationMethod = PasswordTransformationMethod.getInstance()
                et_change_pwd2.setSelection(et_change_pwd2.text.toString().length)
            }
            return@setOnTouchListener true
        }
        //设置删除按钮点击事件
        ll_change_deletepwd1.setOnClickListener { et_change_pwd1.setText("") }
        ll_change_deletepwd2.setOnClickListener { et_change_pwd2.setText("") }
        //下一步按钮点击事件
        btn_change_next.setOnClickListener {
            if (!isFindPwd && et_change_pwd1.text.toString().isEmpty()) {
                showToast("请输入原密码")
            } else if (isFindPwd && et_account.text.isEmpty()) {
                showToast("请输入手机号")
            } else if (et_change_pwd2.text.toString().isEmpty()) {
                showToast("请输入新密码")
            } else if (et_change_pwd3.text.toString().isEmpty()) {
                showToast("请输入确认密码")
            } else if (!isFindPwd && et_change_pwd1.text.toString() == et_change_pwd2.text.toString()) {
                showToast("原密码与新密码相同")
            } else if (et_change_pwd2.text.toString().length < 6) {
                showToast("密码长度至少为6位")
//            } else if (!isFindPwd && UserManager.passWord != "" && et_change_pwd1.text.toString() != UserManager.passWord) {
//                showToast("原密码输入错误")
            } else {
                if (isFindPwd) {
                    val codeView = LayoutInflater.from(this).inflate(R.layout.dialog_verification_code, null, false)

                    val etPhone = codeView.findViewById<EditText>(R.id.et_phone)
                    val etCode = codeView.findViewById<EditText>(R.id.et_code)
                    tvSend = codeView.findViewById<TextView>(R.id.tv_send)
                    val btnSubmit = codeView.findViewById<Button>(R.id.btn_submit)

                    etPhone.text = et_account.text
                    //获取验证码
                    tvSend.setOnClickListener {
                        if (etPhone.text.isNotEmpty()) {
                            tvSend.isEnabled = false
                            time = 60
                            mPresenter.sendUpdatePwd(ApiParamUtil.sendUpdatePwd(etPhone.text.toString()))
                        } else {
                            showToast("请输入手机号")
                            tvSend.isEnabled = true
                        }
                    }
                    btnSubmit.text = "提交"
                    btnSubmit.setOnClickListener {
                        if (etPhone.text.isEmpty())
                            showToast("请输入手机号")
                        else if (etCode.text.isEmpty())
                            showToast("请输入验证码")
                        else
                            mPresenter.updatePasswordByCode(ApiParamUtil.updatePasswordByCode(etCode.text.toString(), etPhone.text.toString(), et_change_pwd2.text.toString()))
                    }
                    ZXDialogUtil.showCustomViewDialog(this, "", codeView, null, { _, _ ->
                        time = -1
                    })
                } else {
                    ZXDialogUtil.showYesNoDialog(this, "提示", "是否确认修改密码？") { dialog, which ->
                        isSubmit = true
                        mPresenter.checkPassword(ApiParamUtil.checkPassword(et_change_pwd2.text.toString()))
                    }
                }
            }
        }
    }

    var time = 60
    lateinit var tvSend: TextView
    //验证码回调
    override fun onSendUpdatePwdResult(data: String) {
        if (data == "error") {
            tvSend.isEnabled = true
            return
        } else if (data != "djs") showToast("发送成功")
        if (time == -1) {
            tvSend.isEnabled = true
            return
        }
        if (time > 0) {
            tvSend.text = "重新发送 $time s"
            tvSend.isEnabled = false
            Handler().postDelayed({
                time--
                onSendUpdatePwdResult("djs")
            }, 1000)
        } else {
            tvSend.text = "重新发送"
            tvSend.isEnabled = true
        }
    }

    fun setColor(level: Int, color: Int) {
        views.forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        }
        for (i in 0..level) {
            views.get(i).setBackgroundColor(color)
        }
    }

    override fun onCheckResult(data: String) {
        val level = data.toInt()
        if (level < 1) {//太简单
            setColor(0, ContextCompat.getColor(this, R.color.level_1))
        } else if (level in 1..20) {//简单
            setColor(1, ContextCompat.getColor(this, R.color.level_2))
        } else if (level in 21..40) {//中
            setColor(2, ContextCompat.getColor(this, R.color.level_3))
        } else if (level in 41..60) {//较强
            setColor(3, ContextCompat.getColor(this, R.color.level_4))
        } else if (level in 61..80) {//强
            setColor(4, ContextCompat.getColor(this, R.color.level_5))
        } else if (level > 80) {//很强
            setColor(5, ContextCompat.getColor(this, R.color.level_6))
        }
        if (isSubmit) {
            if (level < 1)
                showToast("密码强度太低，请重新设置密码")
            else
                mPresenter.changePwd(ApiParamUtil.changePwdParam(et_change_pwd1.text.toString(), et_change_pwd2.text.toString()))
        }
    }

    override fun onBackPressed() {
        UserManager.loginOut()
        super.onBackPressed()
    }

    /**
     * 密码修改成功
     */
    override fun onChangeResult() {
        showToast("密码修改成功，请重新登录")
        uploadLog(301, 3, "修改了密码")
        UserManager.loginOut()
        LoginActivity.startAction(this, true)
    }

    override fun onUpdatePwdResult(data: String) {
        showToast("密码重置成功，请重新登录")
        uploadLog(301, 3, "找回了密码")
        finish()
    }
}

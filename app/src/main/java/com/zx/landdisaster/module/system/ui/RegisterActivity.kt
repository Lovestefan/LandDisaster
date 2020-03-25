package com.zx.landdisaster.module.system.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esri.core.geometry.Point
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.disaster.func.util.FileUriUtil
import com.zx.landdisaster.module.main.ui.LocationSelectActivity
import com.zx.landdisaster.module.other.ui.CameraVedioActivity
import com.zx.landdisaster.module.system.bean.AreaBean
import com.zx.landdisaster.module.system.bean.RegisterData
import com.zx.landdisaster.module.system.mvp.contract.RegisterContract
import com.zx.landdisaster.module.system.mvp.model.RegisterModel
import com.zx.landdisaster.module.system.mvp.presenter.RegisterPresenter
import com.zx.zxutils.entity.KeyValueEntity
import com.zx.zxutils.util.ZXDialogUtil
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class RegisterActivity : BaseActivity<RegisterPresenter, RegisterModel>(), RegisterContract.View {

    private var selectPoint: Point? = null
    var filePath = ""
    var gender = "男"
    var identities = "2"//身份
    var nation = ""//民族
    var culture = ""//文化程度
    var polics = ""//政治面貌
    var areacode = ""//所属区县
    var areaname = ""
    var managearea = ""//负责乡镇
    val areaList = arrayListOf<KeyValueEntity>()
    var haveArea = false
    var xiangzhenList = JSONArray()
    val views = arrayListOf<View>()

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, RegisterActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        views.add(v1)
        views.add(v2)
        views.add(v3)
        views.add(v4)
        views.add(v5)
        views.add(v6)
        zxs_sex.apply {
            setItemHeightDp(30)
            setData(mutableListOf(KeyValueEntity("", ""), KeyValueEntity("男", "1"), KeyValueEntity("女", "2")))
            setDefaultItem(0)
            setItemTextSizeSp(17)
            showSelectedTextColor(true, R.color.colorPrimary)
            showUnderineColor(false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    gender = selectedEntity.getKey()
                }
            }
        }.build()
        zxs_userType.apply {
            setItemHeightDp(30)
            setData(mutableListOf(KeyValueEntity("地环站人员", "2"), KeyValueEntity("驻守地质队员", "4")
                    , KeyValueEntity("专家", "5")))
            setDefaultItem(0)
            setItemTextSizeSp(17)
            showSelectedTextColor(true, R.color.colorPrimary)
            showUnderineColor(false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    identities = selectedEntity.getValue().toString()
                    if (position == 0) {
                        ll_fzxz.visibility = View.VISIBLE
                        fzxz_dhz.visibility = View.VISIBLE//负责乡镇-非必填
                        fzxz_zsdzdy.visibility = View.GONE
                        ll_dhzLayout.visibility = View.VISIBLE//值班电话-职位-地址
                        ll_zsdzdyLayout.visibility = View.GONE//真实手机号-单位名称
                        ll_zhuanjia.visibility = View.GONE
                    } else if (position == 1) {
                        ll_fzxz.visibility = View.VISIBLE
                        fzxz_dhz.visibility = View.GONE//负责乡镇-必填
                        fzxz_zsdzdy.visibility = View.VISIBLE
                        ll_dhzLayout.visibility = View.GONE
                        ll_zsdzdyLayout.visibility = View.VISIBLE
                        ll_zhuanjia.visibility = View.GONE
                    } else {
                        ll_zhuanjia.visibility = View.VISIBLE
                        ll_fzxz.visibility = View.GONE//负责乡镇-非必填
                        ll_dhzLayout.visibility = View.GONE//值班电话-职位-地址
                        ll_zsdzdyLayout.visibility = View.GONE//真实手机号-单位名称
                    }
                }
            }
        }.build()
        zxs_education.apply {
            setItemHeightDp(30)
            setData(RegisterData.getEducation())
            setDefaultItem(0)
            setItemTextSizeSp(17)
            showSelectedTextColor(true, R.color.colorPrimary)
            showUnderineColor(false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    culture = selectedEntity.getKey()
                }
            }
        }.build()
        zxs_political.apply {
            setItemHeightDp(30)
            setData(RegisterData.getPolitical())
            setDefaultItem(0)
            setItemTextSizeSp(17)
            showSelectedTextColor(true, R.color.colorPrimary)
            showUnderineColor(false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    polics = selectedEntity.getKey()
                }
            }
        }.build()
        zxs_nation.apply {
            setItemHeightDp(30)
            setData(RegisterData.getNation())
            setDefaultItem(0)
            setItemTextSizeSp(17)
            showSelectedTextColor(true, R.color.colorPrimary)
            showUnderineColor(false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    nation = selectedEntity.getKey()

                }
            }
        }.build()

        mPresenter.getFindByParent(ApiParamUtil.findByParent("500"))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        iv_head.setOnClickListener {
            ZXDialogUtil.showListDialog(this, "选取方式", "关闭", arrayOf("拍摄", "本地文件选择")) { _, which ->
                if (which == 0) {
                    getPermission(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                        CameraVedioActivity.startAction(this, false, 1)
                    }
                } else {
                    getPermission(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "*/*"//设置类型，我这里是任意类型，任意后缀的可以这样写。
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        startActivityForResult(intent, 0x03)
                    }
                }
            }
        }
        et_account.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == 11) {
                    mPresenter.getCheckAccountData(ApiParamUtil.checkAccount(s.toString()))
                } else {
                    tv_tip_account.visibility = View.GONE
                }
                if (s.toString() == "") {
                    tv_tip_phone.visibility = View.GONE
                } else if (isPhoneNum(s.toString())) {
                    tv_tip_phone.visibility = View.GONE
                } else {
                    tv_tip_phone.visibility = View.VISIBLE
                }

            }
        })
        et_pwd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length >= 6) {
                    llPwd.visibility = View.VISIBLE
                    mPresenter.checkPassword(ApiParamUtil.checkPassword(s.toString()))
                } else {
                    llPwd.visibility = View.GONE
                    tv_tip_pwdLever.visibility = View.GONE
                    setColor(0, 0)
                }

                if (!et_pwd1.text.toString().isEmpty() && !s.toString().isEmpty() && s.toString() != et_pwd1.text.toString()) {
                    tv_tip_pwd.visibility = View.VISIBLE
                } else {
                    tv_tip_pwd.visibility = View.GONE
                }
                setBtnEnb()
            }
        })
        et_pwd1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!et_pwd.text.toString().isEmpty() && !s.toString().isEmpty() && s.toString() != et_pwd.text.toString()) {
                    tv_tip_pwd.visibility = View.VISIBLE
                } else {
                    tv_tip_pwd.visibility = View.GONE
                }

                setBtnEnb()
            }
        })


        tv_latitude.setOnClickListener {
            LocationSelectActivity.startAction(this, false, selectPoint)
        }
        tv_longitude.setOnClickListener {
            LocationSelectActivity.startAction(this, false, selectPoint)
        }
        iv_add_location.setOnClickListener {
            LocationSelectActivity.startAction(this, false, selectPoint)
        }
        btn_register.setOnClickListener {
            if (filePath.isEmpty()) {
                showToast("请上传头像照片")
            } else if (et_account.text.isEmpty()) {
                showToast("请输入电话")
            } else if (et_name.text.isEmpty()) {
                showToast("请输入姓名")
            } else if (et_pwd.text.isEmpty()) {
                showToast("请输入密码")
            } else if (et_pwd.text.length < 6) {
                showToast("密码不能小于6位")
            } else if (areacode == "" && areaname == "") {
                showToast("请选择所属区县")
            } else if (identities == "4" && tv_xiangzhen.text.isEmpty()) {
                showToast("请选择负责乡镇")
            } else if (tv_latitude.text.isEmpty() || tv_longitude.text.isEmpty()) {
                showToast("请选择经纬度")
            } else {
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
                        mPresenter.sendRegister(ApiParamUtil.sendRegister(et_name.text.toString(), etPhone.text.toString(), areacode, areaname))
                    } else {
                        showToast("请输入手机号")
                        tvSend.isEnabled = true
                    }
                }
                btnSubmit.text = "提交并注册"
                btnSubmit.setOnClickListener {
                    if (etPhone.text.isEmpty())
                        showToast("请输入手机号")
                    else if (etCode.text.isEmpty())
                        showToast("请输入验证码")
                    else
                        register(etCode.text.toString())
                }
                ZXDialogUtil.showCustomViewDialog(this, "", codeView, null, { _, _ ->
                    time = -1
                })
            }
        }
    }

    var time = 60
    lateinit var tvSend: TextView
    //验证码回调
    override fun onSendRegisterResult(data: String) {
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
                onSendRegisterResult("djs")
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
            views[i].setBackgroundColor(color)
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
        if (level < 1)
            tv_tip_pwdLever.visibility = View.VISIBLE
        else
            tv_tip_pwdLever.visibility = View.GONE
        setBtnEnb()
    }

    override fun onCheckAccountResult(data: String) {
        if (data.toInt() > 0) {
            tv_tip_account.visibility = View.VISIBLE
        } else {
            tv_tip_account.visibility = View.GONE
        }
        setBtnEnb()
    }

    fun setBtnEnb() {
        btn_register.isEnabled = tv_tip_account.visibility == View.GONE && tv_tip_pwd.visibility == View.GONE
                && tv_tip_pwdLever.visibility == View.GONE
    }


    override fun onFindByParentResult(areaBean: List<AreaBean>?) {
        if (areaBean != null) {
            if (!haveArea) {
                areaList.add(KeyValueEntity("", ""))
                for (i in 0 until areaBean.size) {
                    areaList.add(KeyValueEntity(areaBean[i].name, areaBean[i].code))
                }
                zxs_area.apply {
                    setItemHeightDp(30)
                    setData(areaList)
                    setDefaultItem(0)
                    setItemTextSizeSp(17)
                    showSelectedTextColor(true, R.color.colorPrimary)
                    showUnderineColor(false)
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            tv_xiangzhen.text = ""
                            managearea = ""
                            areacode = areaList[position].getValue().toString()
                            areaname = areaList[position].getKey().toString()
                            if (areacode != "")
                                mPresenter.getFindByParent(ApiParamUtil.findByParent(areacode))
                        }
                    }
                }.build()
                haveArea = true
                mPresenter.getFindByParent(ApiParamUtil.findByParent(areaBean[0].code))
            } else {
                xiangzhenList = JSONArray()
                for (i in 0 until areaBean.size) {
                    val kb = JSONObject()
                    kb.put("name", areaBean[i].name)
                    kb.put("code", areaBean[i].code)
                    xiangzhenList.put(kb)
                }
                tv_xiangzhen.setOnClickListener {
                    if (areacode == "") {
                        showToast("请选择所属区县")
                        return@setOnClickListener
                    }
                    SelectMoreView(this, xiangzhenList, tv_xiangzhen, "管辖乡镇", ",", "name", "code").setClickOK {
                        managearea = it
                    }
                }
            }
        }
    }

    //注册
    fun register(code: String) {
        val map = hashMapOf<String, String>()
        map["identities"] = identities
        map["account"] = et_account.text.toString()
        map["name"] = et_name.text.toString()
        map["pwd"] = et_pwd.text.toString()
        map["gender"] = gender
        map["telephone"] = et_account.text.toString()
        map["idcardnumber"] = et_ID.text.toString()
        map["birthday"] = getBirthday()
        map["nation"] = nation
        map["email"] = et_email.text.toString()
        map["culture"] = culture
        map["polics"] = polics
        map["fax"] = et_fax.text.toString()
        map["managearea"] = managearea
        map["longitude"] = tv_longitude.text.toString()
        map["latitude"] = tv_latitude.text.toString()
        map["areacode"] = areacode

        if (identities == "2") {//地环站
            map["keepword1"] = et_zbPhone.text.toString()
            map["keepword2"] = et_position.text.toString()
        } else if (identities == "4") {//驻守地质队员
            map["keepword2"] = et_phone.text.toString()
            map["keepword4"] = et_companyName.text.toString()
        }else if (identities == "5") {//专家
            map["keepword1"] = et_zjbh.text.toString()//专家编号
            map["keepword2"] = et_gzdw.text.toString()//工作单位
            map["keepword3"] = et_zc.text.toString()//职称
            map["keepword4"] = et_xzzw.text.toString()//行政职务
            map["keepword5"] = et_zylb.text.toString()//专业类别

            map["keepword6"] = et_zytcms.text.toString()//专业特长描述
            map["keepword7"] = et_jg.text.toString()//籍贯
            map["keepword8"] = et_cjgzsj.text.toString()//参加工作时间
            map["keepword9"] = et_jkzk.text.toString()//健康状况
            map["keepword10"] = et_byyx.text.toString()//毕业院校

            map["keepword11"] = et_dwzgbm.text.toString()//单位主管部门
            map["keepword12"] = et_hjszd.text.toString()//户籍所在地
            map["keepword13"] = et_gzjlgs.text.toString()//工作简历概述
            map["keepword14"] = et_hjjs.text.toString()//获奖简述
            map["keepword15"] = et_zycgjs.text.toString()//主要成果简述

            map["keepword16"] = et_bz.text.toString()//备注
            map["keepword17"] = et_ly.text.toString()//领域
            map["keepword18"] = et_grjl.text.toString()//个人简历
            map["keepword19"] = et_zyyjfx.text.toString()//主要研究方向
            map["keepword20"] = et_tjbm.text.toString()//推荐部门

            map["keepword21"] = et_cj.text.toString()//层级
        }
        map["code"] = code

        mPresenter.register(map)
    }

    override fun onRegisterResult(hearUrl: String) {
        //注册
        if (!filePath.isEmpty()) {
            mPresenter.hearUpload(File(filePath), hearUrl)
        } else {
            onHearUploadResult()
        }
    }

    override fun onUploadFilePregress(progress: Int) {
        handler.post {
            showLoading("正在上传中...", progress)
        }
    }

    override fun onHearUploadResult() {
        uploadLog(301, 10, "提交注册")
        btn_register.isEnabled = false
        ZXDialogUtil.showInfoDialog(this, "注册成功", "注册信息已提交审核，请于一个工作日后尝试登录。") { _, _ ->
            finish()
        }
        handler.postDelayed({
            time = -1
            finish()
        }, 3000)
    }


    fun getBirthday(): String {
        if (et_ID.text != null && et_ID.text.length == 18) {
            var sr = et_ID.text
            return sr.substring(6, 10) + "-" + sr.substring(10, 12) + "-" + sr.substring(12, 14) + " 00:00:00"
        } else {
            return ""
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0x01) {//坐标选取
            selectPoint = data?.getSerializableExtra("point") as Point
            if (selectPoint != null) {
                if (selectPoint!!.x < 105 || selectPoint!!.x > 110.5) {
                    showToast("经度不符合规范，请重新选择")
                    tv_longitude.text = ""
                    tv_latitude.text = ""
                } else if (selectPoint!!.y < 28 || selectPoint!!.y > 32.5) {
                    showToast("纬度不符合规范，请重新选择")
                    tv_longitude.text = ""
                    tv_latitude.text = ""
                } else {
                    tv_longitude.text = selectPoint!!.x.toString().substring(0, 10)
                    tv_latitude.text = selectPoint!!.y.toString().substring(0, 9)
                }
            }
        } else if (requestCode == 0x03) {//文件选取
            if (data != null) {
                val uri = data.data
                val filePath = FileUriUtil.getFileAbsolutePath(this, uri)
                if (filePath == null) {
                    showToast("获取文件路径失败，请重试")
                } else {
                    val fileName = filePath.substring(filePath.lastIndexOf("/") + 1)

                    val f = AddFileBean.reSetType(null, fileName, filePath)
                    setImg(f.path)
                }
            }
        } else if (resultCode == 0x02) {//拍摄
            if (data != null) {
                val fileBean = AddFileBean(data.getStringExtra("name"), data.getIntExtra("type", 1), data.getStringExtra("path"), data.getStringExtra("vedioPath"))
                setImg(fileBean.path)
            }
        }
    }

    fun setImg(path: String) {
        filePath = path
        Glide.with(mContext)
                .load(path)
                .apply(RequestOptions().placeholder(R.drawable.report_file_image))
                .into(iv_head)
    }
}

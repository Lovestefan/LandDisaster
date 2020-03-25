package com.zx.landdisaster.module.disaster.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.esri.core.geometry.Point
import com.jzxiang.pickerview.TimePickerDialog
import com.jzxiang.pickerview.data.Type
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.app.ConstStrings
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.disaster.bean.DisasterPkiaaBean
import com.zx.landdisaster.module.disaster.bean.ReportDetailBean
import com.zx.landdisaster.module.disaster.bean.ReportResultbean
import com.zx.landdisaster.module.disaster.func.adapter.DisasterPkiaaListAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.ReportHazardAddContract
import com.zx.landdisaster.module.disaster.mvp.model.ReportHazardAddModel
import com.zx.landdisaster.module.disaster.mvp.presenter.ReportHazardAddPresenter
import com.zx.landdisaster.module.main.ui.LocationSelectActivity
import com.zx.landdisaster.module.other.ui.AddFileFragment
import com.zx.zxutils.entity.KeyValueEntity
import com.zx.zxutils.util.*
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import com.zx.zxutils.views.SwipeRecylerView.ZXSwipeRecyler
import com.zx.zxutils.views.ZXSpinner
import kotlinx.android.synthetic.main.fragment_report_hazard_add.*
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：新增/编辑灾险情
 */
class ReportHazardAddFragment : BaseFragment<ReportHazardAddPresenter, ReportHazardAddModel>(), ReportHazardAddContract.View {

    private lateinit var addFileFragment: AddFileFragment

    private var srPkiaaSearchList: ZXSwipeRecyler? = null
    private var pkiaaList: ArrayList<DisasterPkiaaBean>? = null
    private var pkiaaAdapter: DisasterPkiaaListAdapter? = null
    private var pkiaaPageNo = 1

    private var reportBean = ReportDetailBean(reportflow = ReportDetailBean.Reportflow(), auditDetail = ReportDetailBean.AuditDetail(), reportdata = ReportDetailBean.Reportdata(), leaderreview = ReportDetailBean.Leaderreview())
    private var selectPoint: Point? = null

    private var showAudit = false
    private var doAudit: () -> Unit = {}

    companion object {
        /**
         * 启动器
         */
        fun newInstance(reportBean: ReportDetailBean? = null, showAudit: Boolean = false): ReportHazardAddFragment {
            val fragment = ReportHazardAddFragment()
            val bundle = Bundle()
            bundle.putSerializable("reportInfo", reportBean)
            bundle.putBoolean("showAudit", showAudit)
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_report_hazard_add
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        ZXFragmentUtil.addFragment(childFragmentManager, AddFileFragment.newInstance().apply { addFileFragment = this }, R.id.fm_add_file)

        addFileFragment.setOnEditCallBack {
            scroll_view1.fullScroll(0)
        }

        if (arguments!!.getBoolean("showAudit")) {
            btn_report_audit.visibility = View.VISIBLE
            if (UserManager.getUser().personRole.leader || UserManager.getUser().personRole.dispatch) {
                btn_report_audit.text = "审阅"
            }
        }

        //page1 灾害类型
        sp_add1_disasterType.apply {
            setItemHeightDp(30)
            setData(mutableListOf(KeyValueEntity("滑坡", "滑坡"), KeyValueEntity("泥石流", "泥石流"), KeyValueEntity("地面沉降", "地面沉降"),
                    KeyValueEntity("斜坡", "斜坡"), KeyValueEntity("地面塌陷", "地面塌陷"), KeyValueEntity("地裂缝", "地裂缝"),
                    KeyValueEntity("崩塌", "崩塌"), KeyValueEntity("库岸调查", "库岸调查"), KeyValueEntity("其它", "其它")))
            setDefaultItem("请选择...")
            showSelectedTextColor(true, R.color.colorPrimary)
            showUnderineColor(false)
        }.build()
        //page1 规模
        sp_add1_scaleLevel.apply {
            setItemHeightDp(30)
            setData(mutableListOf(KeyValueEntity("中型", "D"), KeyValueEntity("小型", "C"), KeyValueEntity("大型", "B"), KeyValueEntity("特大型", "A")))
            setDefaultItem("请选择...")
            showSelectedTextColor(true, R.color.colorPrimary)
            showUnderineColor(false)
        }.build()
        reportBean.reportdata!!.areaname = if (UserManager.getUser().currentUser!!.areaName == null) "" else UserManager.getUser().currentUser!!.areaName!!
        reportBean.reportdata!!.areacode = if (UserManager.getUser().currentUser!!.areaCode == null) "" else UserManager.getUser().currentUser!!.areaCode
        reportBean.reportdata!!.happentime = System.currentTimeMillis().toString()
        reportBean.reportdata!!.reportorphone = if (UserManager.getUser().currentUser!!.telephone == null) "" else UserManager.getUser().currentUser!!.telephone!!
        reportBean.reportdata!!.reportdeptDesc = if (UserManager.getUser().currentUser!!.orgName == null) "" else UserManager.getUser().currentUser!!.orgName!!

        if (arguments!!.getSerializable("reportInfo") != null) {//判断是否为上报记录
            reportBean = arguments!!.getSerializable("reportInfo") as ReportDetailBean
            resetView()
            resetData()
        } else if (mSharedPrefUtil.contains(ConstStrings.getReportLocalData()) && mSharedPrefUtil.getObject<ReportDetailBean>(ConstStrings.getReportLocalData()) != null) {//判断是否存在保存的数据
            ZXDialogUtil.showYesNoDialog(activity!!, "提示", "存在未提交的上报信息，是否恢复？", "确认", "取消", { dialog, which ->
                reportBean = mSharedPrefUtil.getObject(ConstStrings.getReportLocalData())
                resetView()
                resetData()
            }, { _, _ ->
                mSharedPrefUtil.remove(ConstStrings.getReportLocalData())
                ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "ReportFile/")
            })
        } else {
            resetView()
            resetData()
        }
    }

    override fun onSubmitErrorResult() {
        ZXDialogUtil.showYesNoDialog(activity!!, "提示", "提交失败，是否保存信息？") { dialog, which ->
            btn_report_save.performClick()
        }
    }

    fun setAuditListener(doAudit: () -> Unit) {
        this.doAudit = doAudit
    }

    var isSubmit = false
    override fun onExistInRegionResult(data: String) {
        if (isSubmit) {
            mPresenter.submitReport(reportBean)
        } else if (data == "0") {
            showToast("经纬度可能不在您所属的行政区内，请核实")
        }
//        if (data != "0") {
//            if (isSubmit) {
//                mPresenter.submitReport(reportBean)
//            }
//        } else {
//            showToast("经纬度不在您所属的行政区内")
//            et_add1_latitude.setText("")
//            et_add1_longitude.setText("")
//        }
        isSubmit = false
    }

    fun isExistInRegion() {
        if (UserManager.getUser().currentUser!!.areaCode != null) {
            mPresenter.getIsExistInRegion(ApiParamUtil.getExistInRegion(et_add1_longitude.text.toString(),
                    et_add1_latitude.text.toString(), if (UserManager.getUser().currentUser!!.areaCode != null) UserManager.getUser().currentUser!!.areaCode!! else ""))
        } else {
            onExistInRegionResult("1")
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //暂存点击事件
        btn_report_save.setOnClickListener {
            ZXDialogUtil.showYesNoDialog(activity!!, "提示", "是否暂存上报信息并返回上一级？") { dialog, which ->
                saveData(true)
                mSharedPrefUtil.putObject(ConstStrings.getReportLocalData(), reportBean)
                activity!!.finish()
            }
        }
        //提交
        btn_report_submit.setOnClickListener {
            if (!ZXNetworkUtil.isConnected()) {
                ZXDialogUtil.showYesNoDialog(activity!!, "提示", "网络连接失败，是否暂存上报信息并返回上一级？") { dialog, which ->
                    btn_report_save.performClick()
                }
                return@setOnClickListener
            }
            val saveString = saveData()
            if (saveString.isNotEmpty()) {
                showToast(saveString)
                return@setOnClickListener
            }
            ZXDialogUtil.showYesNoDialog(activity!!, "提示", "是否进行提交？") { dialog, which ->
                isSubmit = true
                isExistInRegion()
            }
        }
        //审核
        btn_report_audit.setOnClickListener { doAudit() }
        //page1 发生时间点击事件
        tv_add1_happenTime.setOnClickListener {
            TimePickerDialog.Builder().apply {
                setCallBack { _, millseconds -> tv_add1_happenTime.text = ZXTimeUtil.getTime(millseconds) }
                setCyclic(true)
                setMaxMillseconds(System.currentTimeMillis())
                setThemeColor(ContextCompat.getColor(activity!!, R.color.colorPrimary))
                setType(Type.ALL)
                setWheelItemTextNormalColor(ContextCompat.getColor(activity!!, R.color.text_color_light))
                setWheelItemTextSelectorColor(ContextCompat.getColor(activity!!, R.color.text_color_normal))
                setTitleStringId("请选择时间")
                if (tv_add1_happenTime.text.toString().isNotEmpty()) setCurrentMillseconds(ZXTimeUtil.string2Millis(tv_add1_happenTime.text.toString()))
            }.build().show(fragmentManager, "time_select")
        }
        rg_add1_hazardType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == rb_add1_hazradType1.id) {
                ll_add2_injuredNum.visibility = View.VISIBLE
                ll_add2_dieNum.visibility = View.VISIBLE
                ll_add2_missingNum.visibility = View.VISIBLE
                tv_add2_econmicLoss.text = "直接经济损失"
            } else if (checkedId == rb_add1_hazradType2.id) {
                ll_add2_injuredNum.visibility = View.GONE
                ll_add2_dieNum.visibility = View.GONE
                ll_add2_missingNum.visibility = View.GONE
                tv_add2_econmicLoss.text = "潜在经济损失"
            }
        }
        //page1 获取经纬度
        iv_add1_location.setOnClickListener {
            LocationSelectActivity.startAction(activity!!, false, selectPoint)
        }
        //隐患点类型
        rg_add1_hiddenType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == rb_add1_hiddenType1.id) {
                ll_add2_pkiaa.visibility = View.GONE
                et_add2_pkiaa.setText("")
                reportBean.reportdata!!.pkiaaType = 0
            } else if (checkedId == rb_add1_hiddenType2.id) {
                ll_add2_pkiaa.visibility = View.VISIBLE
                reportBean.reportdata!!.pkiaaType = 1
            }
        }
        //page2 诱因选择事件
        tv_add2_cause.setOnClickListener {
            var causeString = tv_add2_cause.text.toString()
            val itemList = arrayOf("人为工程活动", "降雨", "风化", "其它")
            val checkList = booleanArrayOf(false, false, false, false)
            if (causeString.isNotEmpty()) {
                val causeList = causeString.split(",")
                for (i in itemList.indices) {
                    if (causeList.contains(itemList[i])) {
                        checkList[i] = true
                    }
                }
            }
            ZXDialogUtil.showCheckListDialog(activity!!, "诱因", itemList, checkList,
                    DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                        checkList[which] = isChecked
                    }, DialogInterface.OnClickListener { dialog, which ->
                causeString = ""
                for (i in checkList.indices) {
                    if (checkList[i]) {
                        causeString += itemList[i] + ","
                    }
                }
                if (causeString.isNotEmpty()) {
                    causeString = causeString.substring(0, causeString.length - 1)
                }
                tv_add2_cause.text = causeString
                ll_add2_causeNote.visibility = if (causeString.contains("其它")) View.VISIBLE else View.GONE
            })
        }
        //page2 隐患点编号查询
        tv_add2_pkiaaSearch.setOnClickListener {
            showPikkaDialog()
        }

        et_add1_longitude.addTextWatch {
            val textLongitude = et_add1_longitude.text.toString().toDoubleOrNull()
            if (textLongitude != null && (textLongitude < 105 || textLongitude > 110.5)) {
                showToast("经度不符合规范，请重新输入")
            }
        }

        et_add1_latitude.addTextWatch {
            val textLatitude = et_add1_latitude.text.toString().toDoubleOrNull()
            if (textLatitude != null && (textLatitude < 28 || textLatitude > 32.5)) {
                showToast("纬度不符合规范，请重新输入")
            }
        }
        //拨打填报人电话
        iv_callPhone.setOnClickListener {
            ZXSystemUtil.callTo(activity, tv_add3_reportPhone.text.toString())
        }
    }

    /**
     * 保存数据
     */
    private fun saveData(justSave: Boolean = false): String {
        uploadLog(302, 6, "暂存灾险情")
        reportBean.reportdata!!.apply {
            if (et_add1_hazardName.text.toString().isNotEmpty()) hazardname = et_add1_hazardName.text.toString() else if (!justSave) return "请填写灾险情名称"
            if (tv_add1_happenTime.text.isNotEmpty()) happentime = ZXTimeUtil.string2Millis(tv_add1_happenTime.text.toString()).toString() else if (!justSave) return "请填写发生时间"
            hazardtype = if (rb_add1_hazradType1.isChecked) "1" else if (rb_add1_hazradType2.isChecked) "2" else ""
            if (!rb_add1_isAreaYes.isChecked && !rb_add1_isAreaNo.isChecked) return "请选择是否库区"
            isarea = if (rb_add1_isAreaYes.isChecked) "1" else if (rb_add1_isAreaNo.isChecked) "0" else ""
            if (sp_add1_disasterType.selectedValue.toString().isNotEmpty()) disastertype = sp_add1_disasterType.selectedValue.toString() else if (!justSave) return "请选择灾害类型"
            if (sp_add1_scaleLevel.selectedValue.toString().isNotEmpty()) scalelevel = sp_add1_scaleLevel.selectedValue.toString() else if (!justSave) return "请选择规模"
            scalelevel = sp_add1_scaleLevel.selectedValue.toString()
            if (et_add1_disvolume.text.toString().isEmpty()) return "请输入方量"
            disvolume = if (et_add1_disvolume.text.isEmpty()) "" else et_add1_disvolume.text.toString()
            areacode = if (UserManager.getUser().currentUser!!.areaCode == null) "" else UserManager.getUser().currentUser!!.areaCode!!
            areaname = tv_add1_areacode.text.toString()

            val textLongitude = et_add1_longitude.text.toString().toDoubleOrNull()
            val textLatitude = et_add1_latitude.text.toString().toDoubleOrNull()
            if (!justSave) {
                if (et_add1_longitude.text.isEmpty()) {
                    return "请选择或填写经纬度"
                } else if (et_add1_latitude.text.isEmpty()) {
                    return "请选择或填写经纬度"
                } else if (textLongitude == null || textLatitude == null) {
                    et_add1_longitude.setText("")
                    et_add1_latitude.setText("")
                    return "经纬度不符合规范"
                } else if (textLongitude < 105 || textLongitude > 110.5) {
                    return "经度不符合规范，请重新输入"
                } else if (textLatitude < 28 || textLatitude > 32.5) {
                    return "纬度不符合规范，请重新输入"
                } else {
                    longitude = et_add1_longitude.text.toString()
                    latitude = et_add1_latitude.text.toString()
                }
            } else {
                longitude = et_add1_longitude.text.toString()
                latitude = et_add1_latitude.text.toString()
            }
            if (et_add1_address.text.toString().isNotEmpty()) address = et_add1_address.text.toString() else if (!justSave) return "请输入详细地址"
            if (ll_add2_pkiaa.visibility == View.VISIBLE && et_add2_pkiaa.text.isNotEmpty()) pkiaa = et_add2_pkiaa.text.toString() else if (ll_add2_pkiaa.visibility == View.VISIBLE && !justSave) return "请选择或填写隐患点编号"
            pkiaaType = if (pkiaa.isNullOrEmpty()) 0 else 1
            if (et_add2_treatObject.text.toString().isNotEmpty()) threatenobj = et_add2_treatObject.text.toString() else if (!justSave) return "请输入威胁对象"
            if (ll_add2_pkiaa.visibility == View.VISIBLE && et_add2_pkiaa.text.toString().isNotEmpty()) pkiaa = et_add2_pkiaa.text.toString() else if (ll_add2_pkiaa.visibility == View.VISIBLE && !justSave) return "请填写或选择隐患点编号"

            if (et_add2_treatNum.text.toString().isEmpty()) return "请输入威胁人数"
            threatnum = if (et_add2_treatNum.text.isEmpty()) "" else et_add2_treatNum.text.toString()
            if (et_add2_leaveNum.text.toString().isEmpty()) return "请输入撤离人数"
            leavenum = if (et_add2_leaveNum.text.isEmpty()) "" else et_add2_leaveNum.text.toString()
            if (et_add2_injureNum.text.toString().isEmpty() && ll_add2_injuredNum.visibility == View.VISIBLE) return "请输入受伤人数"
            injuredperson = if (et_add2_injureNum.text.isEmpty()) "" else et_add2_injureNum.text.toString()
            if (et_add2_dieNum.text.toString().isEmpty() && ll_add2_dieNum.visibility == View.VISIBLE) return "请输入死亡人数"
            dienum = if (et_add2_dieNum.text.isEmpty()) "" else et_add2_dieNum.text.toString()
            if (et_add2_missingNum.text.toString().isEmpty() && ll_add2_missingNum.visibility == View.VISIBLE) return "请输入失踪人数"
            missingnum = if (et_add2_missingNum.text.isEmpty()) "" else et_add2_missingNum.text.toString()
            economicloss = if (et_add2_economicLoss.text.isEmpty()) "" else et_add2_economicLoss.text.toString()
            if (tv_add2_cause.text.isNotEmpty()) cause = tv_add2_cause.text.toString() else if (!justSave) return "请选择诱因"
            causenote = et_add2_causeNote.text.toString()
            if (et_add2_dealidea.text.toString().isEmpty()) return "请输入已开展处置措施"
            dealidea = et_add2_dealidea.text.toString()
            if (et_add2_nextjobplan.text.toString().isEmpty()) return "请输入下一步工作安排"
            nextjobplan = et_add2_nextjobplan.text.toString()
            if (tv_add3_reportPhone.text.toString().isNotEmpty()) reportorphone = tv_add3_reportPhone.text.toString() else if (!justSave) return "请输入填报人手机号"
            if (tv_add3_reportDept.text.toString().isNotEmpty()) reportdept = tv_add3_reportDept.text.toString() else if (!justSave) return "请输入填报单位"
            reportdeptDesc = tv_add3_reportDept.text.toString()
            reportnote = et_add3_reportNote.text.toString()
            files.clear()
            files = addFileFragment.fileBeans
        }
        return ""
    }

    /**
     * 根据当前页码设置界面
     */
    private fun resetView() {
        btn_report_submit.text = "上报"
        btn_report_save.visibility = View.VISIBLE
        btn_report_submit.visibility = View.VISIBLE

        if (arguments!!.getSerializable("reportInfo") != null) {
            var editType = 0//0可以全部编辑   1 部分编辑   2 不可编辑
            if (reportBean.reportdata!!.editAllAble) {//此时为重新提交
                btn_report_save.visibility = View.GONE
                editType = 0
                btn_report_submit.text = "上报"
            } else if (reportBean.reportdata!!.editAble) {//此时为编辑
                btn_report_save.visibility = View.GONE
                editType = 1
                btn_report_submit.text = "续报"
            } else {//此时只能查看
                btn_report_save.visibility = View.GONE
                btn_report_submit.visibility = View.GONE
                editType = 2
            }
            reportBean.reportdata!!.apply {
                et_add1_hazardName.setStatus(editType, hazardname)
                tv_add1_happenTime.setStatus(editType, if (happentime == null) "" else if (ZXStringUtil.isAllNum(happentime)) ZXTimeUtil.getTime(happentime!!.toLong()) else happentime!!)
                rb_add1_hazradType1.isEnabled = editType == 0
                rb_add1_hazradType2.isEnabled = editType == 0
                rb_add1_isAreaYes.isEnabled = if (editType == 2) false else if (editType == 0) true else isarea.isNullOrEmpty()
                rb_add1_isAreaNo.isEnabled = if (editType == 2) false else if (editType == 0) true else isarea.isNullOrEmpty()
                sp_add1_disasterType.setStatus(editType, disastertype, rl_add1_disasterType)
                sp_add1_scaleLevel.setStatus(editType, scalelevel, rl_add1_scaleLevel)
                et_add1_disvolume.setStatus(editType, disvolume)
                tv_add1_areacode.setStatus(editType, reportBean.areaname)
                et_add1_longitude.setStatus(editType, longitude)
                et_add1_latitude.setStatus(editType, latitude)
                iv_add1_location.visibility = if (editType == 2) View.GONE else if (editType == 0) View.VISIBLE else if (longitude.isNullOrEmpty()) View.VISIBLE else View.GONE
                et_add1_address.setStatus(editType, address)
                et_add2_treatObject.setStatus(editType, getThreatObject())
                et_add2_pkiaa.setStatus(editType, pkiaa)
                if (pkiaa.isNullOrEmpty()) {
                    rb_add1_hiddenType1.isChecked = true
                } else {
                    rb_add1_hiddenType2.isChecked = true
                }
                tv_add2_pkiaaSearch.visibility = if (editType == 0 && rb_add1_hiddenType2.isChecked) View.VISIBLE else View.GONE
                rb_add1_hiddenType1.isEnabled = editType == 0
                rb_add1_hiddenType2.isEnabled = editType == 0
                et_add2_treatNum.setStatus(editType, threatnum)
                et_add2_leaveNum.setStatus(editType, leavenum)
                et_add2_injureNum.setStatus(editType, injuredperson)
                et_add2_dieNum.setStatus(editType, dienum)
                et_add2_missingNum.setStatus(editType, missingnum)
                et_add2_economicLoss.setStatus(editType, economicloss)
                tv_add2_cause.setStatus(editType, cause)
                if (cause != null && cause!!.contains("其它")) ll_add2_causeNote.visibility = View.VISIBLE else ll_add2_causeNote.visibility = View.GONE
                et_add2_causeNote.setStatus(editType, causenote)
                et_add2_dealidea.setStatus(editType, dealidea)
                et_add2_nextjobplan.setStatus(editType, nextjobplan)
                tv_add3_reportPhone.setStatus(editType, reportorphone)
                tv_add3_reportDept.setStatus(editType, reportdeptDesc)
                et_add3_reportNote.setStatus(editType, reportnote)
                addFileFragment.setCanEdit(editType != 2)
            }
            if (reportBean.leaderreview != null && reportBean.leaderreview!!.hasreview == "1") {
                tv_add1_reviewopinion.visibility = View.VISIBLE
            } else {
                tv_add1_reviewopinion.visibility = View.GONE
            }
        } else {
            if (reportBean.reportdata!!.pkiaaType == 0) {
                rb_add1_hiddenType1.isChecked = true
            } else {
                rb_add1_hiddenType2.isChecked = true
            }
        }
    }

    /**
     * 设置数据
     */
    private fun resetData() {
        reportBean.reportdata!!.apply {
            et_add1_hazardName.setText(hazardname)
            tv_add1_happenTime.text = if (happentime.isNullOrEmpty()) "" else if (ZXStringUtil.isAllNum(happentime!!.toString())) ZXTimeUtil.getTime(happentime!!.toLong()) else ZXTimeUtil.millis2String(ZXTimeUtil.string2Millis(happentime!!.replace("+0000", "+0800"), "yyyy-MM-dd'T'HH:mm:ss+SSSS"))
            if (hazardtype == "1") rb_add1_hazradType1.isChecked = true else if (hazardtype == "2") rb_add1_hazradType2.isChecked = true
            if (isarea == "1") rb_add1_isAreaYes.isChecked = true else if (isarea == "0") rb_add1_isAreaNo.isChecked = true
            sp_add1_disasterType.selectPositionOfValue(disastertype)
            sp_add1_scaleLevel.selectPositionOfValue(scalelevel)
            et_add1_disvolume.setText(disvolume)
            var quxian = areaname
            if (quxian!!.isEmpty()) {
                quxian = reportBean.areaname
            }
            tv_add1_areacode.text = quxian
            et_add1_longitude.setText(longitude)
            et_add1_latitude.setText(latitude)
//            isExistInRegion()
            et_add1_address.setText(address)
            et_add2_treatObject.setText(getThreatObject())
            et_add2_pkiaa.setText(pkiaa)
            pkiaaType = if (pkiaa.isNullOrEmpty()) 0 else 1
            et_add2_treatNum.setText(threatnum)
            et_add2_leaveNum.setText(leavenum)
            et_add2_injureNum.setText(injuredperson)
            et_add2_dieNum.setText(dienum)
            et_add2_missingNum.setText(missingnum)
            et_add2_economicLoss.setText(economicloss)
            tv_add2_cause.text = cause
            et_add2_causeNote.setText(causenote)
            et_add2_dealidea.setText(dealidea)
            et_add2_nextjobplan.setText(nextjobplan)
            tv_add3_reportPhone.setText(reportorphone)
            if (reportorphone!!.isNotEmpty()) iv_callPhone.visibility = View.VISIBLE
            if (reportdeptDesc.isNullOrEmpty())
                tv_add3_reportDept.setText(reportdept)
            else
                tv_add3_reportDept.setText(reportdeptDesc)
            et_add3_reportNote.setText(reportnote)
            addFileFragment.setData(files)
        }
        if (tv_add1_reviewopinion.visibility == View.VISIBLE) {
            tv_add1_reviewopinion.text = "领导审阅意见:${reportBean.leaderreview!!.reviewopinion}"
        }
    }

    fun loadPkiaaData(searchText: String, refresh: Boolean = false) {
        if (refresh && srPkiaaSearchList != null) {
            pkiaaPageNo = 1
            srPkiaaSearchList!!.clearStatus()
        }
//        if (searchText.isNotEmpty()) {
        mPresenter.getPkiaaList(ApiParamUtil.disasterPkiaaParam(pkiaaPageNo, searchText))
//        } else {
//            srPkiaaSearchList!!.stopRefresh()
//        }
    }

    fun onBackPressed() {
        if (arguments!!.getSerializable("reportInfo") != null) {
            activity!!.finish()
        } else {
            ZXDialogUtil.showWithOtherBtnDialog(activity!!, "提示", "还未保存信息，是否直接退出？", "保存",
                    { _: DialogInterface, i: Int ->
                        if (mSharedPrefUtil.contains(ConstStrings.getReportLocalData()) && mSharedPrefUtil.getObject<ReportDetailBean>(ConstStrings.getReportLocalData()) != null) {
                            mSharedPrefUtil.remove(ConstStrings.getReportLocalData())
                            ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "ReportFile/")
                        }
                        activity!!.finish()
                    }, { _: DialogInterface, i: Int ->
                saveData(true)
                mSharedPrefUtil.putObject(ConstStrings.getReportLocalData(), reportBean)
                activity!!.finish()
            })
        }
    }

    //隐患点查询
    private fun showPikkaDialog() {
        pkiaaList = arrayListOf()
        pkiaaAdapter = DisasterPkiaaListAdapter(pkiaaList!!)
        pkiaaPageNo = 1
        val pikkaView = LayoutInflater.from(activity!!).inflate(R.layout.layout_hazard_search_pkiaa, null, false)
        val etText = pikkaView!!.findViewById<EditText>(R.id.et_pkiaa_searchTxet)
        val ivSearch = pikkaView.findViewById<ImageView>(R.id.iv_pkiaa_searchBtn)
        val tvClose = pikkaView.findViewById<TextView>(R.id.tv_pkiaa_close)
        srPkiaaSearchList = pikkaView.findViewById(R.id.sr_pkiaa_searchList)
        srPkiaaSearchList!!.setLayoutManager(LinearLayoutManager(activity))
                .setAdapter(pkiaaAdapter)
                .autoLoadMore()
                .setPageSize(10)
                .setSRListener(object : ZXSRListener<DisasterPkiaaBean> {
                    override fun onItemLongClick(item: DisasterPkiaaBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pkiaaPageNo++
                        loadPkiaaData(etText.text.toString())
                    }

                    override fun onRefresh() {
                        loadPkiaaData(etText.text.toString(), true)
                    }

                    override fun onItemClick(item: DisasterPkiaaBean?, position: Int) {
                        if (item != null) {
                            if (item.longitude != null && !item.longitude.equals("")) {
                                et_add1_longitude.setText(item.longitude)
                                et_add1_latitude.setText(item.latitude)
                                isExistInRegion()
                            }
                            et_add2_pkiaa.setText(item.pkiaa)
                            ZXDialogUtil.dismissDialog()
                        }
                    }

                })
        etText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loadPkiaaData(etText.text.toString(), true)
                return@setOnEditorActionListener true
            }
            false
        }
        ivSearch.setOnClickListener { loadPkiaaData(etText.text.toString()) }
        tvClose.setOnClickListener { ZXDialogUtil.dismissDialog() }
        loadPkiaaData(etText.text.toString(), true)
        ZXDialogUtil.showCustomViewDialog(activity!!, "", pikkaView, null)
    }

    override fun onPkiaaListResult(pkiaaList: NormalList<DisasterPkiaaBean>?) {
        if (this.pkiaaList == null || this.pkiaaAdapter == null || this.srPkiaaSearchList == null) {
            return
        }
        srPkiaaSearchList!!.refreshData(pkiaaList!!.result, pkiaaList.totalRecords)

//        srPkiaaSearchList!!.stopRefresh()
//        if (pkiaaList?.result == null) {
//            srPkiaaSearchList!!.setLoadInfo(0)
//            this.pkiaaList!!.clear()
//        } else {
//            srPkiaaSearchList!!.setLoadInfo(pkiaaList.totalRecords)
//            this.pkiaaList!!.clear()
//            this.pkiaaList!!.addAll(pkiaaList.result!!)
//            srPkiaaSearchList!!.recyclerView.scrollToPosition(0)
//        }
//        srPkiaaSearchList!!.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0x01) {//坐标选取
            selectPoint = data?.getSerializableExtra("point") as Point
            if (selectPoint != null) {
                if (selectPoint!!.x < 105 || selectPoint!!.x > 110.5) {
                    showToast("经度不符合规范，请重新选择")
                    et_add1_longitude.setText("")
                    et_add1_latitude.setText("")
                } else if (selectPoint!!.y < 28 || selectPoint!!.y > 32.5) {
                    showToast("纬度不符合规范，请重新选择")
                    et_add1_longitude.setText("")
                    et_add1_latitude.setText("")
                } else {
                    et_add1_longitude.setText(selectPoint!!.x.toString().substring(0, 10))
                    et_add1_latitude.setText(selectPoint!!.y.toString().substring(0, 9))
                    isExistInRegion()
                }
            }
        } else {
            addFileFragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * 上报回调
     */
    override fun onReportSubmitResult(reportResultbean: ReportResultbean) {
        uploadLog(302, 5, "灾险情上报")
        val files = arrayListOf<File>()
        if (reportBean.reportdata!!.files.isNotEmpty()) {
            reportBean.reportdata!!.files.forEach {
                val path = if (it.type == 1) {
                    it.path
                } else {
                    it.vedioPath
                }
                if (!path.startsWith("http")) {//只上传本地选择的文件
                    val file = File(path)
                    files.add(file)
                }
            }
        }
        if (files.size > 0) {
            mPresenter.uploadFile(files, reportResultbean.flowNum)
        } else {
            onFileUploadResult(reportResultbean.flowNum)
        }
    }

    /**
     * 加载进度
     */
    override fun onFileUploadPregress(progress: Int) {
        handler.post {
            showLoading("正在上传中...", progress)
        }
    }

    /**
     * 上传回调
     */
    override fun onFileUploadResult(flowNum: String) {
        if (addFileFragment.deleteFiles.size > 0) {//如果存在删除的文件
            val fileNames = arrayOfNulls<String>(addFileFragment.deleteFiles.size)
            for (i in addFileFragment.deleteFiles.indices) {
                fileNames[i] = addFileFragment.deleteFiles[i].name
            }
            mPresenter.deleteFile(ApiParamUtil.deleteFilesParam(flowNum, fileNames))
        } else {
            onFileDeleteResult()
        }
    }

    /**
     * 删除回调
     */
    override fun onFileDeleteResult() {
        showToast("提交成功")
        uploadLog(302, 8, "提交灾险情上报附件")
        mSharedPrefUtil.remove(ConstStrings.getReportLocalData())
        ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "ReportFile/")
        activity!!.finish()
    }

    /**
     * 扩展Spinner
     */
    private fun ZXSpinner.setStatus(editType: Int, text: String?, rlBg: RelativeLayout) {
        if (text == null) {
            rlBg.isSelected = if (editType == 2) true else if (editType == 0) false else false
            this.isEnabled = if (editType == 2) false else if (editType == 0) true else true
        } else {
            rlBg.isSelected = if (editType == 2) true else if (editType == 0) false else text.isNotEmpty()
            this.isEnabled = if (editType == 2) false else if (editType == 0) true else text.isEmpty()
        }
    }

    /**
     * 扩展TextView
     */
    private fun TextView.setStatus(editType: Int, text: String?) {
        if (text == null) {
            this.isSelected = if (editType == 2) true else if (editType == 0) false else false
            this.isClickable = if (editType == 2) false else if (editType == 0) true else true
        } else {
            this.isSelected = if (editType == 2) true else if (editType == 0) false else text.isNotEmpty()
            this.isClickable = if (editType == 2) false else if (editType == 0) true else text.isEmpty()
        }
    }

    /**
     * 扩展Edittext
     */
    private fun EditText.setStatus(editType: Int, text: String?) {
        if (text == null) {
            this.isSelected = if (editType == 2) true else if (editType == 0) false else false
            this.isFocusableInTouchMode = if (editType == 2) false else if (editType == 0) true else true
        } else {
            this.isSelected = if (editType == 2) true else if (editType == 0) false else text.isNotEmpty()
            this.isFocusableInTouchMode = if (editType == 2) false else if (editType == 0) true else text.isEmpty()
        }
    }

    /**
     * 扩展ZXSpinner函数
     */
    private fun ZXSpinner.setItemSelectCall(onItemSelect: (KeyValueEntity) -> Unit) {
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onItemSelect((parent as ZXSpinner).selectedEntity)
            }
        }
    }

    /**
     * 扩展ZXSpinner函数，获取所选项的position
     */
    private fun ZXSpinner.selectPositionOfValue(value: String?) {
        if (value.isNullOrEmpty()) {
            setSelection(0)
            return
        }
        var text = value
        if (text!!.contains(",")) {
            text = text.split(",")[0]
        }
        for (i in dataList.indices) {
            if (text == dataList[i].value) {
                setSelection(i)
                return
            }
        }
        setSelection(0)
    }

    private fun EditText.addTextWatch(onTextWatch: () -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextWatch()
            }

        })
    }

}

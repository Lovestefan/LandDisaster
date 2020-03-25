package com.zx.landdisaster.module.system.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.other.UserManager

import com.zx.landdisaster.module.system.mvp.contract.WorkDutyContract
import com.zx.landdisaster.module.system.mvp.model.WorkDutyModel
import com.zx.landdisaster.module.system.mvp.presenter.WorkDutyPresenter
import kotlinx.android.synthetic.main.activity_work_duty.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class WorkDutyActivity : BaseActivity<WorkDutyPresenter, WorkDutyModel>(), WorkDutyContract.View {

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, WorkDutyActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_work_duty
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        var title = "工作职责"
        var content = ""
        if (UserManager.getUser().personRole.areaManager) {
            title += "(片区专管)"
            content = "(一)片区负责人由乡镇(街道)分管地质灾害工作的领导担任。\n\n" +
                    "(二)指导、管理和监督群测群防人员做好群测群防各项工作。\n\n" +
                    "(三)组织做好地质灾害汛前排查、汛中巡查和汛后核查工作。\n\n" +
                    "(四)组织做好地质灾害宣传、培训和避险演练工作。\n\n" +
                    "(五)及时组织开展地质灾害应急先期处置工作。"
        } else if (UserManager.getUser().personRole.groupDefense) {
            title += "(群测群防)"
            content = "(一)负责地质灾害隐患点及周边区域的巡查和日常监测预警工作，认真做好监测数据记录汇总，利用群测群防专用手机定点定人定" +
                    "时上传地质灾害群测群防信息系统\n\n" +
                    "(二)向受地质灾害威胁群众宣传隐患点规模、类型和有关重要事项，宣传防灾避险知识，向受灾害威胁的村民协助发放避灾明白卡，" +
                    "保管使用预警器具，熟悉并告知受威胁群众临灾报警信号、应急转移路线和避灾安置地点。\n\n" +
                    "(三)灾险情发生时立即发出警报并组织群众及时撤，并及时向上级汇报。\n\n" +
                    "(四)保护好地质灾害隐患点安全警示牌、标识牌、监 测标志和隔离带(周界桩)等设施，积极协助其它地质灾害防治工作。"
        } else if (UserManager.getUser().personRole.garrison) {
            title += "(驻守队员)"
            content = "(一)配合驻守乡镇(街道)开展地质灾害隐患排查、巡查和核查工作。\n\n" +
                    "(二)配合驻守乡镇(街道)进一步完善监测预警、防灾预案和群测群防体系建设，对乡镇(街道)群测群防工作进行技术指导\n\n" +
                    "(三)配合区县国土资源管理部门开展地质灾害防治知识培训工作。\n\n" +
                    "(四)指导驻守乡镇( 街道)开展地质灾害应急演练工作。\n\n" +
                    "(五)配合驻守乡镇(街道)做好地质灾害应急处置与救援工作，为应急救灾工作提供支持。"
        } else if (UserManager.getUser().personRole.ringStand) {
            title += "(地环站人员)"
            content = "(一)负责对群测群防员或乡镇(街道)上报的地质灾害隐患点进行实地核实，并形成调查报告。\n" +
                    "(二)参与地质灾害防治规划的编制，负责拟定本区县地质灾害防灾预案和应急抢险预案。\n" +
                    "(三)负责本行政区域内地质灾害的监测工作，参与地质灾害预测预报工作，在政府的领导下，建立地质灾害“群测群防、群专结合”的监测、预警、预报体系，组织地质灾害防治及“群测群防”的宣传、培训工作。\n" +
                    "(四)负责地质灾害抢险救灾技术指导工作。\n" +
                    "(五)做好片区负责人、驻守地质队员的的监督、管理和考核工作。\n" +
                    "(六)做好本行政区域地质灾害数据库、应急指挥系统维护和地质灾害信息报送工作。"
            tvContent.setTextSize(14f)
        }
        tool_bar.setMidText(title)
        tvContent.setText(content)
        tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

}

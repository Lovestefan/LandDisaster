package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.disaster.func.adapter.RainAndPointAdapter

import com.zx.landdisaster.module.disaster.mvp.contract.RainAndPointContract
import com.zx.landdisaster.module.disaster.mvp.model.RainAndPointModel
import com.zx.landdisaster.module.disaster.mvp.presenter.RainAndPointPresenter
import com.zx.landdisaster.module.main.bean.AreaFromGisBean
import com.zx.landdisaster.module.main.bean.DisasterFromGisBean
import com.zx.landdisaster.module.main.bean.LegendBean
import com.zx.landdisaster.module.main.bean.RainStationFromGisBean
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_rain_and_point.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class RainAndPointActivity : BaseActivity<RainAndPointPresenter, RainAndPointModel>(), RainAndPointContract.View {

    var fenxiStartTime = ""
    var fenxiEndTime = ""

    var type = 1
    var level = 4

    val dataBeans = arrayListOf<LegendBean>()
    private val rapAdapter = RainAndPointAdapter(dataBeans)
    var yhdList = ArrayList<DisasterFromGisBean>()
    var xzList = ArrayList<AreaFromGisBean>()
    var ylzList = ArrayList<RainStationFromGisBean>()
    //默认保存所有列表，一共调3次接口，后续本地筛选

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, start: String, end: String) {
            val intent = Intent(activity, RainAndPointActivity::class.java)
            intent.putExtra("sTime", start)
            intent.putExtra("eTime", end)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_rain_and_point
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        fenxiStartTime = intent.getStringExtra("sTime")
        fenxiEndTime = intent.getStringExtra("eTime")

        rv_list.apply {
            layoutManager = LinearLayoutManager(this@RainAndPointActivity)
            adapter = rapAdapter
        }
        mPresenter.getDisasterFromGis(ApiParamUtil.getFenXi(fenxiStartTime, fenxiEndTime))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        tool_bar_back.setOnClickListener {
            finish()
        }
        tv_point.setOnClickListener {
            type = 1
            setPageData()
        }
        tv_towns.setOnClickListener {
            type = 2
            setPageData()
        }
        tv_rainstation.setOnClickListener {
            type = 3
            setPageData()
        }
        tv_type4.setOnClickListener {
            level = 4
            tv_type4.setBackgroundColor(Color.parseColor("#F0BB5B"))
            tv_type5.setBackgroundColor(Color.parseColor("#808080"))
            tv_type6.setBackgroundColor(Color.parseColor("#808080"))
            setPageData()
        }
        tv_type5.setOnClickListener {
            level = 5
            tv_type4.setBackgroundColor(Color.parseColor("#808080"))
            tv_type5.setBackgroundColor(Color.parseColor("#F0BB5B"))
            tv_type6.setBackgroundColor(Color.parseColor("#808080"))
            setPageData()
        }
        tv_type6.setOnClickListener {
            level = 6
            tv_type4.setBackgroundColor(Color.parseColor("#808080"))
            tv_type5.setBackgroundColor(Color.parseColor("#808080"))
            tv_type6.setBackgroundColor(Color.parseColor("#F0BB5B"))
            setPageData()
        }

        rapAdapter.setOnItemClickListener { _, _, position ->
            if (type == 1) {
                HiddenPointActivity.startAction(this, false, yhdList[position].pkiaa)
            }
        }
    }

    //隐患点
    override fun onDisasterFromGisResult(data: List<DisasterFromGisBean>?) {
        if (data != null && data.isNotEmpty())
            yhdList.addAll(data)
        mPresenter.getAreaFromGis(ApiParamUtil.getFenXi(fenxiStartTime, fenxiEndTime))
    }

    //乡镇
    override fun onAreaFromGisResult(data: List<AreaFromGisBean>?) {
        if (data != null && data.isNotEmpty())
            xzList.addAll(data)
        mPresenter.getRainStationFromGis(ApiParamUtil.getFenXi(fenxiStartTime, fenxiEndTime))
    }

    //雨量站
    override fun onRainStationFromGisResult(data: List<RainStationFromGisBean>?) {
        if (data != null && data.isNotEmpty())
            ylzList.addAll(data)
        setPageData()
    }

    fun setPageData() {
        dataBeans.clear()
        var num1 = 0
        for (i in 0 until yhdList.size) {
            if (yhdList[i].gridcode == level) {
                if (type == 1) {
                    dataBeans.add(LegendBean(yhdList[i].name, when (yhdList[i].type) {
                        "崩塌" -> R.drawable.normal_icon_dis_1
                        "滑坡" -> R.drawable.normal_icon_dis_2
                        "地面沉降" -> R.drawable.normal_icon_dis_3
                        "地裂缝" -> R.drawable.normal_icon_dis_4
                        "泥石流" -> R.drawable.normal_icon_dis_5
                        "斜坡" -> R.drawable.normal_icon_dis_6
                        "地面塌陷" -> R.drawable.normal_icon_dis_7
                        "库岸调查" -> R.drawable.normal_icon_dis_8
                        else -> R.drawable.normal_icon_1
                    }))
                }
                num1++
            }
        }
        tv_point.text = num1.toString()

        var num2 = 0
        for (i in 0 until xzList.size) {
            if (xzList[i].gridcode == level) {
                if (type == 2)
                    dataBeans.add(LegendBean(xzList[i].qxmc + " " + xzList[i].xzmc, R.drawable.icon_fenxi_xiangzhen))
                num2++
            }
        }
        tv_towns.text = num2.toString()

        var num3 = 0
        for (i in 0 until ylzList.size) {
            if (ylzList[i].gridcode == level) {
                if (type == 3) {
                    dataBeans.add(LegendBean(ylzList[i].areaname + " " + ylzList[i].townsname + " "
                            + ylzList[i].sensorname + "(" + ylzList[i].sensornumber + " " + ylzList[i].rainfall
                            + "mm)", R.drawable.icon_fenxi_yuliangzhan))
                }
                num3++
            }
        }
        tv_rainstation.text = num3.toString()

        rapAdapter.notifyDataSetChanged()
        if (dataBeans.size == 0)
            tvNotData.visibility = View.VISIBLE
        else
            tvNotData.visibility = View.GONE
    }

}

package com.zx.landdisaster.module.main.ui

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.esri.android.map.GraphicsLayer
import com.esri.core.geometry.Point
import com.esri.core.map.Graphic
import com.esri.core.symbol.PictureMarkerSymbol
import com.esri.core.symbol.TextSymbol
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.bean.Jurisdiction
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.OpenNaviUtil
import com.zx.landdisaster.module.disaster.bean.LiveInfoMationBean
import com.zx.landdisaster.module.disaster.bean.RainPointBean
import com.zx.landdisaster.module.disaster.ui.*
import com.zx.landdisaster.module.main.bean.*
import com.zx.landdisaster.module.main.func.adapter.MapBtnAdapter
import com.zx.landdisaster.module.main.func.adapter.MapLegendAdapter
import com.zx.landdisaster.module.main.func.adapter.TimeListAdapter
import com.zx.landdisaster.module.main.func.listener.MapListener
import com.zx.landdisaster.module.main.func.util.ClusterPointUtil
import com.zx.landdisaster.module.main.func.util.RainLayerUtil
import com.zx.landdisaster.module.main.func.util.ZXPopupWindow
import com.zx.landdisaster.module.main.mvp.contract.MapBtnContract
import com.zx.landdisaster.module.main.mvp.model.MapBtnModel
import com.zx.landdisaster.module.main.mvp.presenter.MapBtnPresenter
import com.zx.landdisaster.module.worklog.ui.DailyAuditListActivity
import com.zx.zxutils.util.*
import kotlinx.android.synthetic.main.fragment_map_btn.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MapBtnFragment : BaseFragment<MapBtnPresenter, MapBtnModel>(), MapBtnContract.View {

    val mGraphicsLayer = GraphicsLayer()
    val suofang = 1000000// suo fang
    val fenxiLayer = GraphicsLayer()

    private lateinit var mapListener: MapListener
    private var layerBubble: ZXPopupWindow? = null
    private var moreBubble: ZXPopupWindow? = null//更多

    private var disasterClusterUtil: ClusterPointUtil? = null//灾险情
    private var hiddenClusterUtil: ClusterPointUtil? = null//隐患点
    private var rainLayerUtil: RainLayerUtil? = null//隐患点

    private val legends = arrayListOf<LegendBean>()
    private val legendAdapter = MapLegendAdapter(legends, true)//图例适配器

    private var timeList = arrayListOf<TimeListBean>()
    private val timeAdapter = TimeListAdapter(timeList)//时间选择适配器

    private var disasterList: List<DisasterPointBean>? = null//灾险情
    private var hiddenList: List<HiddenPointBean>? = null//隐患点

    private val btnList = arrayListOf<FuncBean>()
    private val btnAdapter = MapBtnAdapter(btnList)

    private lateinit var defaultResetScaleParams: RelativeLayout.LayoutParams
    private lateinit var defaultLocationParams: RelativeLayout.LayoutParams
    private lateinit var defaultListParams: RelativeLayout.LayoutParams
    private lateinit var defaultTimesParams: RelativeLayout.LayoutParams
    private lateinit var defaultInfoParams: RelativeLayout.LayoutParams
    private lateinit var defaultTucengInfoParams: RelativeLayout.LayoutParams
    private lateinit var defaultTucengTimeInfoParams: RelativeLayout.LayoutParams

    //从 隐患点1，灾险情2，雨量3，地灾风险4，气象预报5 进入
    var onType = 0
    var lookListType = 0//隐患点1，灾险情2，雨量3。查看列表 类型

    var lastType = arrayListOf<Int>()//1=灾险情/隐患点，2=雨量，3=风险预警/气象预报
    var showFxyj = false//是否显示了风险预警
    var showQxyb = false//是否显示了气象预报

    var showZxq = false//是否显示了灾险情
    var showYhd = false//是否显示了隐患点

    var showYl = false//是否显示了雨量

    var zxqType = "3"//灾险情默认type
    var yuliangType = "3"//雨量默认type

    var isOneShowZxq = false//是否进入地图就显示了灾险情

    var fxyjAlpha = 50//风险预警 透明度
    var qxybAlpha = 50//气象预报 透明度

    companion object {
        /**
         * 启动器
         * 0 默认  1地图选点  2主体查看
         */
        fun newInstance(type: Int = 0, address: String = "", longitude: Double = 0.0, latitude: Double = 0.0): MapBtnFragment {
            val fragment = MapBtnFragment()
            val bundle = Bundle()
            bundle.putInt("type", type)
            bundle.putString("address", address)
            bundle.putDouble("longitude", longitude)
            bundle.putDouble("latitude", latitude)
            fragment.arguments = bundle
            return fragment
        }
    }

    fun popupWindowShowing(): Boolean {
        return (layerBubble != null && layerBubble!!.isShowing) || (moreBubble != null && moreBubble!!.isShowing)
    }

    fun popupWindowDismiss() {
        if (layerBubble != null)
            layerBubble!!.dismiss()

        if (moreBubble != null)
            moreBubble!!.dismiss()
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_map_btn
    }

    fun setOnType(t: String) {
        onType = t.toInt()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onConfigurationChanged(resources.configuration)
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mapListener.getMapView().addLayer(mGraphicsLayer)
        if (arguments!!.getInt("type") == 0 && onType != 3 && onType != 4 && onType != 5) {
            btnList.add(FuncBean("图层", R.drawable.iv_map_layer))
//            if (!UserManager.getUser().personRole.expert) {
//            else if (UserManager.canAddNew()){
//                btnList.add(FuncBean("新增", R.drawable.iv_map_report))
//            }
            if (onType == 0) {//非新首页
                if (UserManager.canReport()) {
                    if (haveQuanXian(Jurisdiction.qxsb))
                        btnList.add(FuncBean("上报", R.drawable.iv_map_report))
                }
                if (haveQuanXian(Jurisdiction.sh)) {
                    if (UserManager.canAudit()) {
                        btnList.add(FuncBean("审核", R.drawable.iv_map_audit))
                    } else if (UserManager.canPreview()) {
                        btnList.add(FuncBean("审阅", R.drawable.iv_map_audit))
                    }
                }
            }
            if (haveQuanXian(Jurisdiction.zxq))
                btnList.add(FuncBean("灾险情", R.drawable.iv_map_disaster))
            if (haveQuanXian(Jurisdiction.yhd))
                btnList.add(FuncBean("隐患点", R.drawable.iv_map_hidden))
//            }
            if (haveQuanXian(Jurisdiction.yl))
                btnList.add(FuncBean("雨量", R.drawable.iv_map_rain))
            if (haveQuanXian(Jurisdiction.cl))
                btnList.add(FuncBean("测量", R.drawable.iv_map_measure))
//            if (!UserManager.getUser().personRole.expert) {
            if (haveQuanXian(Jurisdiction.gd))
                btnList.add(FuncBean("更多", R.drawable.iv_map_more))
//            }
        } else {
            if (onType != 3)
                btnList.add(FuncBean("图层", R.drawable.iv_map_layer))
            if (haveQuanXian(Jurisdiction.cl) && onType != 3)
                btnList.add(FuncBean("测量", R.drawable.iv_map_measure))
            if (arguments!!.getInt("type") == 2)
                btnList.add(FuncBean("导航", R.drawable.iv_map_navi))
        }

        if (onType == 3) {
            btnList.add(FuncBean("列表", R.drawable.iv_map_more))
            btnList.add(FuncBean("分析", R.drawable.iv_map_fenxi))
            tv_map_legend_list.visibility = View.GONE
            mapListener.getMapView().addLayer(fenxiLayer)
            fenxiLayer.isVisible = false
        }
        rv_map_legend.apply {
            layoutManager = LinearLayoutManager(activity!!)
            adapter = legendAdapter
        }

        rv_btn_timeList.apply {
            layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
            adapter = timeAdapter
        }

        rv_mapBtn_list.apply {
            layoutManager = LinearLayoutManager(activity!!)
            adapter = btnAdapter
        }

        disasterClusterUtil = ClusterPointUtil(activity!!, ClusterPointUtil.ClusterType.DISASTER, mapListener)
        hiddenClusterUtil = ClusterPointUtil(activity!!, ClusterPointUtil.ClusterType.HIDDEN, mapListener)
        rainLayerUtil = RainLayerUtil()
        disasterClusterUtil!!.setSymbolClickCall { ReportDetailActivity.startAction(activity!!, it, false) }
        hiddenClusterUtil!!.setSymbolClickCall { HiddenPointActivity.startAction(activity!!, false, it) }

        defaultResetScaleParams = ll_mapBtn_resetscale.layoutParams as RelativeLayout.LayoutParams
        defaultLocationParams = ll_mapBtn_location.layoutParams as RelativeLayout.LayoutParams
        defaultListParams = rv_mapBtn_list.layoutParams as RelativeLayout.LayoutParams
        defaultTimesParams = tv_rain_times.layoutParams as RelativeLayout.LayoutParams
        defaultInfoParams = tv_rain_info.layoutParams as RelativeLayout.LayoutParams
        defaultTucengInfoParams = tv_tuceng_info.layoutParams as RelativeLayout.LayoutParams
        defaultTucengTimeInfoParams = tv_tuceng_times.layoutParams as RelativeLayout.LayoutParams

        sb_map_tucengAlpha.progress = 100
        if (onType == 1) {//隐患点
            showYhd = true
            lastType.clear()
            lastType.add(1)
            if (hiddenList == null || hiddenList!!.isEmpty()) {
                mPresenter.getHiddenList(ApiParamUtil.hiddenListParam())
            } else {
                showHiddenLayer()
            }
        } else if (onType == 2) {//灾险情
            isOneShowZxq = true
            showZxq = true
            lastType.clear()
            lastType.add(1)
            if (disasterList == null || disasterList!!.isEmpty()) {
                mPresenter.getDisasterList(ApiParamUtil.disasterListParam(type = zxqType))
            } else {
                showDisaserLayer()
            }
        } else if (onType == 3) {//雨量
            showYl = true
            lastType.clear()
            lastType.add(2)
            if (rainLayerUtil!!.rainLayer == null || (!rainLayerUtil!!.isShowRain() && rainLayerUtil!!.isOutOfDate())) {
                rainLayerUtil!!.rainType = "3"
                mPresenter.getRainList(ApiParamUtil.rainListParam("3"))
            } else {
                showRainLayer()
            }
//            mPresenter.getMaxRainstation(ApiParamUtil.rainList(type = rainLayerUtil!!.rainType.toInt()))//获取所有区县雨量最大值
        } else if (onType == 4) {//风险预警
            tcType = "riskWarn"
            lastType.clear()
            lastType.add(3)
            mPresenter.getUpdateTime(ApiParamUtil.getUpdateTime(tcType))
            mapListener.changeMap("fengxianyujin")
            showFxyj = true
        } else if (onType == 5) {//气象预报
            tcType = "weatherForcast"
            lastType.clear()
            lastType.add(3)
            mPresenter.getUpdateTime(ApiParamUtil.getUpdateTime(tcType))
            mapListener.changeMap("qixiangyubao")
            showQxyb = true
        }

        measure_view.defaultListener = mapListener.getOnTouchListener()

    }

    var tcType = ""//

    override fun onUpdateTimeResult(data: String) {
        var date = data.split(",")
        var dateFormat = SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒")
        if (tcType == "weatherForcast") {//气象预报
            var sTime = ""
            var eTime = ""
            var hour = 48
            if (date.size == 2) {
                sTime = ZXTimeUtil.getTime(ZXTimeUtil.string2Millis(date[0]), dateFormat).substring(5, 14)
                eTime = ZXTimeUtil.getTime(ZXTimeUtil.string2Millis(date[1]), dateFormat).substring(5, 14)

                hour = ((ZXTimeUtil.string2Millis(date[1]) - ZXTimeUtil.string2Millis(date[0])) / 3600000).toInt()
            } else {
                var nowTime = ZXTimeUtil.string2Millis(data)
                sTime = ZXTimeUtil.getTime(nowTime, dateFormat).substring(5, 14)
                eTime = ZXTimeUtil.getTime(nowTime + (48 * 3600000), dateFormat).substring(5, 14)
            }
            tv_tuceng_info.text = "未来" + hour + "小时降雨预报图"
            tv_tuceng_times.text = "( $sTime - $eTime )\n数据来源：市气象局格点预报数据"

            addLegends(4)
        } else if (tcType == "riskWarn") {//风险预警
            tv_tuceng_info.text = "全市地质灾害风险预警图"
            tv_tuceng_times.text = "( $data )\n数据来源：市地环总站风险预警数据"

            addLegends(3)
        }
        showTip()
    }

    //重新排序
    fun addLastType(type: Int) {
        var lastType1 = arrayListOf<Int>()
        for (i in lastType.indices) {
            if (lastType[i] != type) {
                lastType1.add(lastType[i])
            }
        }
        lastType1.add(type)
        lastType = lastType1
    }

    //去除隱藏
    fun removeLastType(type: Int) {
        var lastType1 = arrayListOf<Int>()
        for (i in lastType.indices) {
            if (lastType[i] != type) {
                lastType1.add(lastType[i])
            }
        }
        lastType = lastType1
    }

    private fun showTip() {
        if (showFxyj || showQxyb) {
            tv_tuceng_info.visibility = View.VISIBLE
            tv_tuceng_times.visibility = View.VISIBLE

            tv_tcName.visibility = View.VISIBLE
            sb_map_tucengAlpha.visibility = View.VISIBLE
            if (showFxyj) {
                addLegends(3)
            }
            if (showQxyb) {
                addLegends(4)
            }
            addLastType(3)
        } else {
            removeLastType(3)
            tv_tuceng_info.visibility = View.GONE
            tv_tuceng_times.visibility = View.GONE

            tv_tcName.visibility = View.GONE
            sb_map_tucengAlpha.visibility = View.GONE

            if (disasterClusterUtil!!.isShowLayer() && showZxq && lastType.last() == 1) {
                lookListType = 2
                addLegends(0)

            } else if (hiddenClusterUtil!!.isShowLayer() && showYhd && lastType.last() == 1) {
                lookListType = 1
                addLegends(1)
            } else {
                lookListType = 0
                cv_map_legend.visibility = View.GONE
                if (rainLayerUtil != null && rainLayerUtil!!.isShowRain()) {
                    addLegends(2)
                }
            }
        }
    }


    /**
     * View事件设置
     */
    override fun onViewListener() {
        //定位
        ll_mapBtn_location.setOnClickListener {
            val isLocation = mapListener.doLocation()
            if (hiddenClusterUtil != null && isLocation) {
                hiddenClusterUtil!!.locationChange()
            }
            if (onType == 3)
                fenxiLayer.isVisible = mapListener.getMapView().scale <= suofang
        }
        //复原
        ll_mapBtn_resetscale.setOnClickListener {
            mapListener.resetScale()
            if (hiddenClusterUtil != null) {
                hiddenClusterUtil!!.resetScaleChange()
            }
            if (onType == 3)
                fenxiLayer.isVisible = false
        }
        //图层透明度
        sb_map_tucengAlpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var tcType = 1
                if (showFxyj) {//风险预警
                    tcType = 1
                    fxyjAlpha = progress
                } else if (showQxyb) {//气象预报
                    tcType = 2
                    qxybAlpha = progress
                }
                mapListener.setAlpha(tcType, progress / 100.0f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        //雨量透明度
        sb_map_rainalpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                rainLayerUtil!!.rainLayer!!.opacity = progress / 100.0f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        //查看列表
        tv_map_legend_list.setOnClickListener {
            if (lookListType == 2 || (lookListType == 0 && disasterClusterUtil!!.isShowLayer())) {
                if (!haveZxqList) {
                    showToast("未获取到灾险情信息，暂时无法查询。")
                } else {
                    DisasterPointListActivity.startAction(activity!!, false, zxqType)
                    uploadLog(306, 5, "查看灾险情列表")
                }

            } else if (lookListType == 1 || (lookListType == 0 && hiddenClusterUtil!!.isShowLayer())) {
                HiddenPointListActivity.startAction(activity!!, false)
                uploadLog(306, 2, "查看隐患点列表")
            } else if (lookListType == 3 || (lookListType == 0 && rainLayerUtil!!.isShowRain())) {
                if (!haveRainList) {
//                    showToast("当前实况降雨数据有延迟，暂时无法查询。")
                } else {
                    RainPointActivity.startAction(activity!!, false, rainLayerUtil!!.rainType.toInt())
                    uploadLog(305, 3, "查看雨量站列表：${rainLayerUtil!!.rainType.toInt()}")
                }
            }
        }
        //时间列表
        timeAdapter.setOnItemClickListener { adapter, view, position ->
            val timeListBean = timeList[position]
            when (timeListBean.type) {
                0 -> {//灾险情
                    if (zxqType != timeListBean.value) {
                        zxqType = timeListBean.value
                        mPresenter.getDisasterList(ApiParamUtil.disasterListParam(type = zxqType))
                    }
                }
                2 -> {//雨量
                    yuliangType = timeListBean.value
                    if (rainLayerUtil!!.rainType != timeListBean.value) {
                        rainLayerUtil!!.rainType = timeListBean.value
                        mPresenter.getRainList(ApiParamUtil.rainListParam(timeListBean.value))
//                        mPresenter.getMaxRainstation(ApiParamUtil.rainList(type = timeListBean.value.toInt()))//获取所有区县雨量最大值
                    }
                }
            }
            for (i in timeList.indices) {
                timeList[i].select = (i == position)
            }
            timeAdapter.notifyDataSetChanged()
        }

        btnAdapter.setOnItemClickListener { _, view, position ->
            when (btnList[position].title) {
                "图层" -> showLayerBubble(view)
                "上报" -> activity?.let { it1 -> ReportActivity.startAction(it1, false) }
                "新增" -> activity?.let { it1 -> ReportHazardActivity.startAction(it1, false) }
                "审核" -> activity?.let { it1 -> AuditHomeActivity.startAction(it1, false) }
                "审阅" -> activity?.let { it1 -> AuditActivity.startAction(it1, false) }
                "灾险情" -> {
                    if (disasterList == null || disasterList!!.isEmpty()) {
                        mPresenter.getDisasterList(ApiParamUtil.disasterListParam(type = zxqType))
                    } else {
                        showDisaserLayer(true)
                    }
                }
                "隐患点" -> {
                    if (hiddenList == null || hiddenList!!.isEmpty()) {
                        mPresenter.getHiddenList(ApiParamUtil.hiddenListParam())
                    } else {
                        showHiddenLayer()
                    }
                }
                "雨量" -> {
                    if (rainLayerUtil!!.rainLayer == null || (!rainLayerUtil!!.isShowRain() && rainLayerUtil!!.isOutOfDate())) {
                        rainLayerUtil!!.rainType = yuliangType
                        mPresenter.getRainList(ApiParamUtil.rainListParam(yuliangType))
//                        mPresenter.getMaxRainstation(ApiParamUtil.rainList(type = yuliangType.toInt()))//获取所有区县雨量最大值
                    } else {
                        showRainLayer()
                    }
                }
                "更多" -> {
//                    if(moreBubble!=null&&moreBubble!!.isShowing){
//                         moreBubble!!.dismiss()
//                    }else{
//
//                    }
//                    if (UserManager.getUser().personRole.ringStandAudit)
//                        showMoreBubble(view)
//                    else
                    ZXToastUtil.showToast("功能正在开发中")
                }
                "测量" -> {
                    if (measure_view.mIsShowMeasure) {
                        measure_view.closeMeasure()
                    } else {
                        measure_view.showMeasure(mapListener.getMapView())
                    }
                }
                "导航" -> {
                    OpenNaviUtil.startNavi(activity!!, arguments!!.getString("address"), arguments!!.getDouble("longitude"), arguments!!.getDouble("latitude"))
                }
                "列表" -> {
                    RainPointActivity.startAction(activity!!, false, rainLayerUtil!!.rainType.toInt())
                    uploadLog(305, 3, "查看雨量站列表：${rainLayerUtil!!.rainType.toInt()}")
                }
                "分析" -> {
                    if (rainLayerUtil!!.rainType == "3")
                        mPresenter.getLiveInfomation(ApiParamUtil.getFenXi(fenxiStartTime, fenxiEndTime))
                    else
                        showToast("请选择3小时再查询")
                }
                else -> {
                    showToast("功能正在开发中")
                }
            }
        }
        //收起/展开
        tv_map_legend_list_open.setOnClickListener {
            legendAdapter.isOpen = !legendAdapter.isOpen
            legendAdapter.notifyDataSetChanged()
            if (legendAdapter.isOpen)
                tv_map_legend_list_open.text = "收起"
            else
                tv_map_legend_list_open.text = "展开"
        }
        mapListener.addSingleTagListener(object : MapFragment.MySingleTapListener {
            override fun onSingleTap(x: Float, y: Float) {
                val ids = mGraphicsLayer.getGraphicIDs(x, y, 20)
                if (ids != null && ids.isNotEmpty()) {
                    for (i in ids.indices) {
                        val graphic = mGraphicsLayer.getGraphic(ids[i])
                        if (graphic != null) {
                            val attr = graphic.attributes
                            if (attr["index"] != null) {
                                var index = attr["index"] as Int
                                showCallout(index, 1)
                                return
                            }
                        }
                    }
                } else {
                    var fenxis: IntArray? = null
                    if (onType == 3)
                        fenxis = fenxiLayer.getGraphicIDs(x, y, 20)

                    if (fenxis != null && fenxis.isNotEmpty() && mapListener.getMapView().scale <= suofang) {//分析
                        for (i in fenxis.indices) {
                            val graphic = fenxiLayer.getGraphic(fenxis[i])
                            if (graphic != null) {
                                val attr = graphic.attributes
                                if (attr["index"] != null) {
                                    var index = attr["index"] as Int
                                    showCallout(index, attr["type"] as Int)
                                    return
                                }
                            }
                        }
                    } else {
                        mapListener.getMapView().callout.hide()
                    }
                }
            }
        })
    }

    /**
     * 打开图层弹窗
     */
    private fun showLayerBubble(showView: View) {
        if (layerBubble == null) {
            val view = LayoutInflater.from(activity!!).inflate(R.layout.layout_layer_bubble, null, false)
            if (arguments!!.getInt("type") != 1 && (onType != 3 && onType != 5) && haveQuanXian(Jurisdiction.dzfx)) {
                view.findViewById<LinearLayout>(R.id.ll_fxyj).visibility = View.VISIBLE
                view.findViewById<LinearLayout>(R.id.ll_layer_fxyj).visibility = View.VISIBLE

            }
            if (arguments!!.getInt("type") != 1 && (onType != 3 && onType != 4) && haveQuanXian(Jurisdiction.qxyb)) {
                view.findViewById<LinearLayout>(R.id.ll_fxyj).visibility = View.VISIBLE
                view.findViewById<LinearLayout>(R.id.ll_layer_qxyb).visibility = View.VISIBLE
            }

            view.findViewById<LinearLayout>(R.id.ll_layer_vector).setOnClickListener {
                mapListener.changeMap("vector")
                if (layerBubble != null)
                    layerBubble!!.dismiss()
            }
            view.findViewById<LinearLayout>(R.id.ll_layer_image).setOnClickListener {
                mapListener.changeMap("image")
                if (layerBubble != null)
                    layerBubble!!.dismiss()
            }
//            view.findViewById<LinearLayout>(R.id.ll_layer_fangfan).setOnClickListener {
//                mapListener.changeMap("fangfan")
//                if (layerBubble != null)
//                    layerBubble!!.dismiss()
//            }
//            view.findViewById<LinearLayout>(R.id.ll_layer_yifa).setOnClickListener {
//                mapListener.changeMap("yifa")
//                if (layerBubble != null)
//                    layerBubble!!.dismiss()
//            }}
            view.findViewById<LinearLayout>(R.id.ll_layer_fxyj).setOnClickListener {
                mapListener.changeMap("fengxianyujin")
                showFxyj = !showFxyj
                if (showFxyj) showQxyb = false
                if (tcType != "riskWarn") {//风险预警
                    tcType = "riskWarn"
                    mPresenter.getUpdateTime(ApiParamUtil.getUpdateTime(tcType))
                } else {
                    showTip()
                }
                if (layerBubble != null)
                    layerBubble!!.dismiss()
            }
            view.findViewById<LinearLayout>(R.id.ll_layer_qxyb).setOnClickListener {
                mapListener.changeMap("qixiangyubao")
                showQxyb = !showQxyb
                if (showQxyb) showFxyj = false
                if (tcType != "weatherForcast") {//气象预报
                    tcType = "weatherForcast"
                    mPresenter.getUpdateTime(ApiParamUtil.getUpdateTime(tcType))
                } else {
                    showTip()
                }
                if (layerBubble != null)
                    layerBubble!!.dismiss()
            }
            layerBubble = ZXPopupWindow(activity!!).setBubbleView(view)
        }
        layerBubble!!.show(showView, Gravity.LEFT)
    }

    /**
     * 打开更多弹窗
     */
    private fun showMoreBubble(showView: View) {
        if (moreBubble == null) {
            val view = LayoutInflater.from(activity!!).inflate(R.layout.layout_more_bubble, null, false)
            view.findViewById<LinearLayout>(R.id.ll_more_railyReport).setOnClickListener {
                DailyAuditListActivity.startAction(activity!!, false)
                moreBubble!!.dismiss()
            }
            moreBubble = ZXPopupWindow(activity!!)
                    .setBubbleView(view)
        }
        moreBubble!!.show(showView, Gravity.LEFT)
    }

    fun setMapListener(mapListener: MapListener) {
        this.mapListener = mapListener
    }

    /**
     * 展示灾险情
     */
    private fun showDisaserLayer(showCluster: Boolean = false) {
        uploadLog(306, 4, "查看灾险情图层")

        tv_map_legend_list.visibility = View.VISIBLE
        tv_map_legend_list_open.visibility = View.GONE
        if (hiddenClusterUtil!!.isShowLayer()) {
            cv_map_legend.visibility = View.GONE
            hiddenClusterUtil!!.showCluster()
        }
//        if (rainLayerUtil!!.isShowRain()) {
//            cv_map_legend.visibility = View.GONE
//            ll_map_rainalpha.visibility = View.GONE
//        ll_rain_times_select.visibility=View.GONE
//        tv_rain_times.visibility = View.GONE
//            rainLayerUtil!!.showRainLayer()
//        }

        disasterClusterUtil!!.setDatas(disasterList = disasterList)
        if (showCluster) {
            disasterClusterUtil!!.showCluster()
        }
        if (disasterClusterUtil!!.isShowLayer()) {
            lookListType = 2
            addLegends(0)
            showZxq = true//是否显示了灾险情
            showYhd = false//是否显示了隐患点
            cv_map_legend.visibility = View.VISIBLE
            rv_btn_timeList.visibility = View.VISIBLE
            downLayoutView.visibility = View.VISIBLE

            addLastType(1)
        } else {
            lookListType = 0
            removeLastType(1)
            cv_map_legend.visibility = View.GONE
            rv_btn_timeList.visibility = View.GONE
            downLayoutView.visibility = View.GONE
            if (rainLayerUtil != null && rainLayerUtil!!.isShowRain() && showYl && lastType.last() == 2) {
                addLegends(2)
            } else if (showFxyj) {
                addLegends(3)
            } else if (showQxyb) {
                addLegends(4)
            }
        }
    }

    /**
     * 展示隐患点
     */
    private fun showHiddenLayer() {
        uploadLog(306, 1, "查看隐患点图层")
        tv_map_legend_list.visibility = View.VISIBLE
        tv_map_legend_list_open.visibility = View.GONE
        if (disasterClusterUtil!!.isShowLayer()) {
            cv_map_legend.visibility = View.GONE
            disasterClusterUtil!!.showCluster()
        }

        if (hiddenClusterUtil!!.datas.isEmpty()) {
            hiddenClusterUtil!!.setDatas(hiddenPointBean = hiddenList)
        }
//        if (rainLayerUtil!!.isShowRain()) {
//            cv_map_legend.visibility = View.GONE
//            ll_map_rainalpha.visibility = View.GONE
//        ll_rain_times_select.visibility=View.GONE
//        tv_rain_times.visibility = View.GONE
//            rainLayerUtil!!.showRainLayer()
//        }
        hiddenClusterUtil!!.showCluster()
        if (hiddenClusterUtil!!.isShowLayer()) {
            if (lookListType == 2 && showYl) {
                addTimeList(2)
                rv_btn_timeList.visibility = View.VISIBLE
                var fl = FrameLayout.LayoutParams(ZXSystemUtil.dp2px(timeList.size * 41f), ZXSystemUtil.dp2px(30f))
                fl.gravity = Gravity.CENTER
                downLayoutView.layoutParams = fl
                downLayoutView.visibility = View.VISIBLE
            }
            lookListType = 1
            addLegends(1)

            showZxq = false//是否显示了灾险情
            showYhd = true//是否显示了隐患点

            addLastType(1)
        } else {
            lookListType = 0
            removeLastType(1)
            cv_map_legend.visibility = View.GONE
            if (rainLayerUtil != null && rainLayerUtil!!.isShowRain() && showYl && lastType.last() == 2) {
                addLegends(2)
            } else if (showFxyj) {
                addLegends(3)
            } else if (showQxyb) {
                addLegends(4)
            }
        }
    }

    //雨量图层是否已经显示
    var rainIsShow = false

    /**
     * 展示雨量
     */
    private fun showRainLayer() {
        if (onType != 3)
            tv_map_legend_list.visibility = View.VISIBLE
        else
            tv_map_legend_list.visibility = View.GONE

        uploadLog(305, 2, "查看实况降雨图层：${rainLayerUtil!!.rainType}")

        if (rainLayerUtil!!.rainType == "168") {
            tv_map_legend_list_open.text = "收起"
            tv_map_legend_list_open.visibility = View.GONE
            legendAdapter.isOpen = true
            legendAdapter.notifyDataSetChanged()
        } else {
//            tv_map_legend_list_open.visibility = View.VISIBLE
        }
//        if (disasterClusterUtil!!.isShowLayer()) {
//            cv_map_legend.visibility = View.GONE
//            disasterClusterUtil!!.showCluster()
//        }
//        if (hiddenClusterUtil!!.isShowLayer()) {
//            cv_map_legend.visibility = View.GONE
//            hiddenClusterUtil!!.showCluster()
//        }

        rainLayerUtil!!.showRainLayer()
        if (rainLayerUtil!!.isShowRain()) {
            lookListType = 3
            ll_map_rainalpha.visibility = View.VISIBLE

            cv_map_legend.visibility = View.VISIBLE
            cv_map_legend.visibility = View.VISIBLE
            tv_rain_times.visibility = View.VISIBLE
            tv_rain_info.visibility = View.VISIBLE

            addTimeList(2)
            rv_btn_timeList.visibility = View.VISIBLE
            var fl = FrameLayout.LayoutParams(ZXSystemUtil.dp2px(timeList.size * 41f), ZXSystemUtil.dp2px(30f))
            fl.gravity = Gravity.CENTER
            downLayoutView.layoutParams = fl
            downLayoutView.visibility = View.VISIBLE

            addLegends(2)
            showYl = true

            addLastType(2)

            rainIsShow = true
        } else {
            showYl = false
            rainIsShow = false
            lookListType = 0
            ll_map_rainalpha.visibility = View.GONE
            rv_btn_timeList.visibility = View.GONE
            downLayoutView.visibility = View.GONE

            cv_map_legend.visibility = View.GONE
            tv_rain_times.visibility = View.GONE
            tv_rain_info.visibility = View.GONE

            removeLastType(2)
            if (hiddenClusterUtil != null && hiddenClusterUtil!!.isShowLayer() && showYhd && lastType.last() == 1) {//隐患点
                addLegends(1)
            } else if (disasterClusterUtil != null && disasterClusterUtil!!.isShowLayer() && showZxq && lastType.last() == 1) {//灾险情
                addLegends(0)
            } else if (showFxyj) {
                addLegends(3)
            } else if (showQxyb) {
                addLegends(4)
            }
        }

        mGraphicsLayer.isVisible = rainIsShow
    }

    //根据type添加时间列表
    private fun addTimeList(type: Int) {
        when (type) {
            0 -> {//灾险情
                if (timeList.size == 4) return
                timeList.clear()
                timeList.add(TimeListBean(0, "今日", "1", zxqType == "1"))
                timeList.add(TimeListBean(0, "本周", "2", zxqType == "2"))
                timeList.add(TimeListBean(0, "本月", "3", zxqType == "3"))
                timeList.add(TimeListBean(0, "三个月", "4", zxqType == "4"))
                timeList.add(TimeListBean(0, "今年", "5", zxqType == "5"))
                timeList.add(TimeListBean(0, "全部", "6", zxqType == "6"))
                timeAdapter.notifyDataSetChanged()
            }
            2 -> {//雨量
                if (timeList.size == 7) return
                timeList.clear()
                timeList.add(TimeListBean(2, "1小时", "1", yuliangType == "1"))
                timeList.add(TimeListBean(2, "3小时", "3", yuliangType == "3"))
                timeList.add(TimeListBean(2, "6小时", "6", yuliangType == "6"))
                timeList.add(TimeListBean(2, "12小时", "12", yuliangType == "12"))
                timeList.add(TimeListBean(2, "24小时", "24", yuliangType == "24"))
                timeList.add(TimeListBean(2, "3天", "72", yuliangType == "72"))
                timeList.add(TimeListBean(2, "7天", "168", yuliangType == "168"))
                timeAdapter.notifyDataSetChanged()
            }
        }
    }

    //根据type添加图例
    private fun addLegends(type: Int) {
        when (type) {
            0 -> {//灾险情
                legends.clear()
                legends.add(LegendBean("灾情", R.drawable.normal_icon_2))
                legends.add(LegendBean("险情", R.drawable.normal_icon_3))
                legendAdapter.notifyDataSetChanged()
                cv_map_legend.visibility = View.VISIBLE

                tv_map_legend_list.visibility = View.VISIBLE
                tv_map_legend_list_open.visibility = View.GONE
                addTimeList(0)

                rv_btn_timeList.visibility = View.VISIBLE
                var fl = FrameLayout.LayoutParams(ZXSystemUtil.dp2px(timeList.size * 41f), ZXSystemUtil.dp2px(30f))
                fl.gravity = Gravity.CENTER
                downLayoutView.layoutParams = fl
                downLayoutView.visibility = View.VISIBLE



                if (!rainIsShow) {
                    tv_rain_times.visibility = View.GONE
                    tv_rain_info.visibility = View.GONE
                    ll_map_rainalpha.visibility = View.GONE
                }
                if (!showFxyj && !showQxyb) ll_map_tucengAlpha.visibility = View.GONE
            }
            1 -> {//隐患点
                legends.clear()
                legends.add(LegendBean("崩塌", R.drawable.normal_icon_dis_1))
                legends.add(LegendBean("滑坡", R.drawable.normal_icon_dis_2))
                legends.add(LegendBean("地面沉降", R.drawable.normal_icon_dis_3))
                legends.add(LegendBean("地裂缝", R.drawable.normal_icon_dis_4))
                legends.add(LegendBean("泥石流", R.drawable.normal_icon_dis_5))
                legends.add(LegendBean("斜坡", R.drawable.normal_icon_dis_6))
                legends.add(LegendBean("地面塌陷", R.drawable.normal_icon_dis_7))
                legends.add(LegendBean("库岸调查", R.drawable.normal_icon_dis_8))
                legendAdapter.notifyDataSetChanged()
                cv_map_legend.visibility = View.VISIBLE

                tv_map_legend_list.visibility = View.VISIBLE
                tv_map_legend_list_open.visibility = View.GONE
                if (!rainIsShow) {
                    tv_rain_times.visibility = View.GONE
                    tv_rain_info.visibility = View.GONE
                    ll_map_rainalpha.visibility = View.GONE
                    rv_btn_timeList.visibility = View.GONE
                    downLayoutView.visibility = View.GONE

                }
                if (!showFxyj && !showQxyb) ll_map_tucengAlpha.visibility = View.GONE
            }
            2 -> {//雨量
                legends.clear()
                if (rainLayerUtil!!.rainType == "1") {
                    legends.add(LegendBean("小雨", R.drawable.rain_1, "1-1.6"))
                    legends.add(LegendBean("中雨", R.drawable.rain_2, "1.6-7"))
                    legends.add(LegendBean("大雨", R.drawable.rain_3, "7-15"))
                    legends.add(LegendBean("暴雨", R.drawable.rain_4, "15-40"))
                    legends.add(LegendBean("大暴雨", R.drawable.rain_5, "40-50"))
                    legends.add(LegendBean("特大暴雨", R.drawable.rain_6, ">50"))
                } else if (rainLayerUtil!!.rainType == "3") {
                    legends.add(LegendBean("小雨", R.drawable.rain_1, "1-3"))
                    legends.add(LegendBean("中雨", R.drawable.rain_2, "3-10"))
                    legends.add(LegendBean("大雨", R.drawable.rain_3, "10-20"))
                    legends.add(LegendBean("暴雨", R.drawable.rain_4, "20-50"))
                    legends.add(LegendBean("大暴雨", R.drawable.rain_5, "50-70"))
                    legends.add(LegendBean("特大暴雨", R.drawable.rain_6, ">70"))
                } else if (rainLayerUtil!!.rainType == "6") {
                    legends.add(LegendBean("小雨", R.drawable.rain_1, "1-4"))
                    legends.add(LegendBean("中雨", R.drawable.rain_2, "4-13"))
                    legends.add(LegendBean("大雨", R.drawable.rain_3, "13-25"))
                    legends.add(LegendBean("暴雨", R.drawable.rain_4, "25-60"))
                    legends.add(LegendBean("大暴雨", R.drawable.rain_5, "60-120"))
                    legends.add(LegendBean("特大暴雨", R.drawable.rain_6, ">120"))
                } else if (rainLayerUtil!!.rainType == "12") {
                    legends.add(LegendBean("小雨", R.drawable.rain_1, "1-5"))
                    legends.add(LegendBean("中雨", R.drawable.rain_2, "5-15"))
                    legends.add(LegendBean("大雨", R.drawable.rain_3, "15-30"))
                    legends.add(LegendBean("暴雨", R.drawable.rain_4, "30-70"))
                    legends.add(LegendBean("大暴雨", R.drawable.rain_5, "70-140"))
                    legends.add(LegendBean("特大暴雨", R.drawable.rain_6, ">140"))
                } else if (rainLayerUtil!!.rainType == "24" || rainLayerUtil!!.rainType == "72") {
                    legends.add(LegendBean("小雨", R.drawable.rain_1, "1-10"))
                    legends.add(LegendBean("中雨", R.drawable.rain_2, "10-25"))
                    legends.add(LegendBean("大雨", R.drawable.rain_3, "25-50"))
                    legends.add(LegendBean("暴雨", R.drawable.rain_4, "50-100"))
                    legends.add(LegendBean("大暴雨", R.drawable.rain_5, "100-250"))
                    legends.add(LegendBean("特大暴雨", R.drawable.rain_6, ">250"))
                } else {
                    legends.add(LegendBean("", R.drawable.rain_10, "1-10"))
                    legends.add(LegendBean("", R.drawable.rain_11, "10-25"))
                    legends.add(LegendBean("", R.drawable.rain_12, "25-50"))
                    legends.add(LegendBean("", R.drawable.rain_13, "50-100"))
                    legends.add(LegendBean("", R.drawable.rain_14, "100-200"))
                    legends.add(LegendBean("", R.drawable.rain_15, "200-400"))
                    legends.add(LegendBean("", R.drawable.rain_16, "400-800"))
                    legends.add(LegendBean("", R.drawable.rain_17, ">800"))
                }
                legendAdapter.notifyDataSetChanged()
                cv_map_legend.visibility = View.VISIBLE

                tv_rain_times.visibility = View.VISIBLE
                tv_rain_info.visibility = View.VISIBLE

                if (onType != 3)
                    tv_map_legend_list.visibility = View.VISIBLE
//                if (rainLayerUtil!!.rainType != "168")
//                    tv_map_legend_list_open.visibility = View.VISIBLE
                ll_map_rainalpha.visibility = View.VISIBLE
                addTimeList(2)

                rv_btn_timeList.visibility = View.VISIBLE
                var fl = FrameLayout.LayoutParams(ZXSystemUtil.dp2px(timeList.size * 41f), ZXSystemUtil.dp2px(30f))
                fl.gravity = Gravity.CENTER
                downLayoutView.layoutParams = fl
                downLayoutView.visibility = View.VISIBLE

                if (!showFxyj && !showQxyb) ll_map_tucengAlpha.visibility = View.GONE

            }
            3 -> {//风险预警
                legends.clear()
                legends.add(LegendBean("风险很高", R.drawable.fxyj_1))
                legends.add(LegendBean("风险高", R.drawable.fxyj_2))
                legends.add(LegendBean("风险较高", R.drawable.fxyj_3))
                legends.add(LegendBean("一定风险", R.drawable.fxyj_4))
                legendAdapter.notifyDataSetChanged()
                cv_map_legend.visibility = View.VISIBLE

                tv_tcName.text = "风险透明度："
                sb_map_tucengAlpha.progress = fxyjAlpha
                mapListener.setAlpha(1, fxyjAlpha / 100f)
                ll_map_tucengAlpha.visibility = View.VISIBLE

                tv_map_legend_list.visibility = View.GONE
                tv_map_legend_list_open.visibility = View.GONE
                if (!rainIsShow) {
                    tv_rain_times.visibility = View.GONE
                    tv_rain_info.visibility = View.GONE
                    ll_map_rainalpha.visibility = View.GONE
                    rv_btn_timeList.visibility = View.GONE
                    downLayoutView.visibility = View.GONE

                }
            }
            4 -> {//气象预报
                legends.clear()
                legends.add(LegendBean("小雨", R.drawable.rain_1, "1-10毫米"))
                legends.add(LegendBean("中雨", R.drawable.rain_2, "10-25毫米"))
                legends.add(LegendBean("大雨", R.drawable.rain_3, "25-50毫米"))
                legends.add(LegendBean("暴雨", R.drawable.rain_4, "50-100毫米"))
                legends.add(LegendBean("大暴雨", R.drawable.rain_5, "100-250毫米"))
                legends.add(LegendBean("特大暴雨", R.drawable.rain_6, ">250毫米"))
                legendAdapter.notifyDataSetChanged()
                cv_map_legend.visibility = View.VISIBLE
                tv_tcName.text = "气象透明度："
                sb_map_tucengAlpha.progress = qxybAlpha
                mapListener.setAlpha(2, qxybAlpha / 100f)
                ll_map_tucengAlpha.visibility = View.VISIBLE

                tv_map_legend_list.visibility = View.GONE
                tv_map_legend_list_open.visibility = View.GONE
                if (!rainIsShow) {
                    tv_rain_times.visibility = View.GONE
                    tv_rain_info.visibility = View.GONE
                    ll_map_rainalpha.visibility = View.GONE
                    rv_btn_timeList.visibility = View.GONE
                    downLayoutView.visibility = View.GONE

                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (onType == 0)
            mPresenter.getTaskNum()
    }

    var haveZxqList = false

    /**
     * 灾险情列表回调
     */
    override fun onDisasterResult(disasterList: List<DisasterPointBean>) {
//        if (disasterList.isNotEmpty()) {
        this.disasterList = disasterList
        haveZxqList = true
        if (!isOneShowZxq && !disasterClusterUtil!!.isShowLayer()) {
            disasterClusterUtil!!.showCluster()
        }
        showDisaserLayer(isOneShowZxq)

//        } else {
//            this.disasterList = arrayListOf()
//            showToast("未获取到灾险情信息")
//            if (disasterClusterUtil!!.isShowLayer()) {
//                disasterClusterUtil!!.showCluster()
//            }
//            haveZxqList = false
//        }
        isOneShowZxq = false
    }

    /**
     * 隐患点列表回调
     */
    override fun onHiddenResult(hiddenList: List<HiddenPointBean>) {
        if (hiddenList.isNotEmpty()) {
            this.hiddenList = hiddenList
            showHiddenLayer()
        } else {
            showToast("未获取到隐患点信息")
        }
    }

    var haveRainList = false

    override fun onRainListResult(rainDataBean: RainDataBean?) {
        mPresenter.getMaxRainstation(ApiParamUtil.rainList(type = rainLayerUtil!!.rainType.toInt()))//获取所有区县雨量最大值
        if (rainDataBean == null) {
//            showToast("当前实况降雨数据有延迟，暂时无法查询。")
            tv_rain_times.text = ""
            tv_rain_info.text = ""
            if (rainLayerUtil!!.rainLayer != null) {
                rainLayerUtil!!.rainLayer!!.removeAll()
            }
            haveRainList = false

            return
        }
        var time = rainLayerUtil!!.rainType.toInt()
        var dateFormat = SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒")
        var oldTime = ZXTimeUtil.getTime(rainDataBean.date - (time * 3600000), dateFormat).substring(0, 14)
        var nowTime = ZXTimeUtil.getTime(rainDataBean.date, dateFormat).substring(0, 14)

        if (time <= 24) {
            tv_rain_info.text = "过去" + time + "小时实况降雨图"
        } else {
            var day = time / 24
            tv_rain_info.text = "过去" + day + "天实况降雨图"
        }
        tv_rain_times.text = "( ${oldTime.substring(5)} - ${nowTime.substring(5)} )\n数据来源：市气象局降水监测站数据"
        fenxiStartTime = "${oldTime.replace("年", "-").replace("月", "-")
                .replace("日", " ").replace("时", ":")}00:00"
        fenxiEndTime = "${nowTime.replace("年", "-").replace("月", "-")
                .replace("日", " ").replace("时", ":")}00:00"
        haveRainList = true
        if (rainLayerUtil!!.isShowRain()) {
            rainLayerUtil!!.showRainLayer()
        }
        sb_map_rainalpha.progress = 100
        rainLayerUtil!!.drawToMap(mapListener.getMapView(), rainDataBean)
//        rainLayerUtil!!.rainType = "3"
        showRainLayer()


        if (time == 3) {
            mPresenter.getDisasterFromGis(ApiParamUtil.getFenXi(fenxiStartTime, fenxiEndTime))
            mPresenter.getRainStationFromGis(ApiParamUtil.getFenXi(fenxiStartTime, fenxiEndTime))
        }
    }

    var fenxiStartTime = ""
    var fenxiEndTime = ""


    override fun onTaskNumResult(numBean: TaskNumBean) {
        btnList.forEach {
            if (it.title == "审核") {
                var num = 0
                if (numBean.auditTask != null) num += numBean.auditTask!!
                if (numBean.safeReports != null) num += numBean.safeReports!!
                it.taskNum = num
            } else if (it.title == "审阅") {
                if (UserManager.getUser().personRole.leader) {
                    it.taskNum = if (numBean.leaderView != null) numBean.leaderView!! else 0
                } else if (UserManager.getUser().personRole.dispatch) {
                    it.taskNum = if (numBean.dispatchView != null) numBean.dispatchView!! else 0
                }
            } else if (it.title == "上报") {
                it.taskNum = if (numBean.reReports != null) numBean.reReports!! else 0
            }
        }
        btnAdapter.notifyDataSetChanged()
    }

    private val maxRainList = arrayListOf<RainPointBean>()

    //区县雨量站最大值
    override fun onMaxRainstationResult(list: List<RainPointBean>?) {
        maxRainList.clear()
        mGraphicsLayer.removeAll()
        mapListener.getMapView().callout.hide()
        if (list != null) {
            maxRainList.addAll(list)
            val normalIcon1 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, R.drawable.icon_red_point))//图标
            for (i in 0 until list.size) {
                var point = Point(list[i].longitude, list[i].latitude)
                var pictureMarkerSymbol = PictureMarkerSymbol(normalIcon1)
                mGraphicsLayer.addGraphic(Graphic(point, pictureMarkerSymbol))

                val textSymbol = TextSymbol(if (i == 0) {
                    20
                } else {
                    14
                }, list[i].vol.toString(), Color.RED)

                textSymbol.horizontalAlignment = TextSymbol.HorizontalAlignment.CENTER
                val attr = HashMap<String, Any>()
                attr["index"] = i
                var graphic = Graphic(point, textSymbol, attr)

                mGraphicsLayer.addGraphic(graphic)

            }
            mGraphicsLayer.isVisible = rainIsShow

        }
    }


    var fenxiYHDList = ArrayList<DisasterFromGisBean>()

    //分析：隐患点
    override fun onDisasterFromGisResult(data: List<DisasterFromGisBean>?) {
        mapListener.addZoomListener(object : MapFragment.MyZoomListener {
            override fun postAction(p0: Float, p1: Float, p2: Double) {
                fenxiLayer.isVisible = mapListener.getMapView().scale <= suofang
            }
        })
        val normalDis1 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_1_hdpi else R.drawable.normal_icon_dis_1))//图标-隐患点
        val normalDis2 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_2_hdpi else R.drawable.normal_icon_dis_2))//图标-隐患点
        val normalDis3 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_3_hdpi else R.drawable.normal_icon_dis_3))//图标-隐患点
        val normalDis4 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_4_hdpi else R.drawable.normal_icon_dis_4))//图标-隐患点
        val normalDis5 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_5_hdpi else R.drawable.normal_icon_dis_5))//图标-隐患点
        val normalDis6 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_6_hdpi else R.drawable.normal_icon_dis_6))//图标-隐患点
        val normalDis7 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_7_hdpi else R.drawable.normal_icon_dis_7))//图标-隐患点
        val normalDis8 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_8_hdpi else R.drawable.normal_icon_dis_8))//图标-隐患点
        val normalIcon1 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_1_hdpi else R.drawable.normal_icon_1))//图标-隐患点

        if (data != null && data.isNotEmpty()) {
            fenxiYHDList.clear()
            fenxiYHDList.addAll(data)
            for (i in 0 until data.size) {
                var picIcon = when (data[i].type) {
                    "崩塌" -> normalDis1
                    "滑坡" -> normalDis2
                    "地面沉降" -> normalDis3
                    "地裂缝" -> normalDis4
                    "泥石流" -> normalDis5
                    "斜坡" -> normalDis6
                    "地面塌陷" -> normalDis7
                    "库岸调查" -> normalDis8
                    else -> normalIcon1
                }

                var point = Point(data[i].longitude, data[i].latitude)
                var pictureMarkerSymbol = PictureMarkerSymbol(picIcon)
                val attr = HashMap<String, Any>()
                attr["index"] = i
                attr["type"] = 2
                var graphic = Graphic(point, pictureMarkerSymbol, attr)
                fenxiLayer.addGraphic(graphic)
            }
            fenxiLayer.isVisible = mapListener.getMapView().scale <= suofang
        }
    }

    var fenxiYLZList = ArrayList<RainStationFromGisBean>()

    //分析：雨量站
    override fun onRainStationFromGisResult(data: List<RainStationFromGisBean>?) {
        val normalIcon3 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, R.drawable.icon_fenxi_yuliangzhan))//图标-隐患点

        if (data != null && data.isNotEmpty()) {
            fenxiYLZList.clear()
            fenxiYLZList.addAll(data)
            for (i in 0 until data.size) {
                var point = Point(data[i].longitude, data[i].latitude)
                var pictureMarkerSymbol = PictureMarkerSymbol(normalIcon3)
                val attr = HashMap<String, Any>()
                attr["index"] = i
                attr["type"] = 3
                var graphic = Graphic(point, pictureMarkerSymbol, attr)
                fenxiLayer.addGraphic(graphic)
                fenxiLayer.isVisible = mapListener.getMapView().scale <= suofang
            }
        }
    }

    //雨量分析
    override fun onLiveInfomationResult(rainDataBean: LiveInfoMationBean?) {
        if (rainDataBean != null) {
            if (rainDataBean.maxRainstation != null && rainDataBean.maxRainstation.rainfall > 0) {
                var qx = rainDataBean.maxRainstation.areaname
                var xz = rainDataBean.maxRainstation.townsname
                var zdz = rainDataBean.maxRainstation.rainfall
                ZXDialogUtil.showYesNoDialog(activity!!, "分析结果",
                        "暴雨以上有${rainDataBean.townsNum}个镇，${rainDataBean.rainstationNum}个雨量站，" +
                                "影响${rainDataBean.disasterNum}个隐患点，降雨最大值位于${if (qx == null) "" else qx}" +
                                "-${if (xz == null) "" else xz}，雨量值为${if (zdz == null) "0" else zdz}mm。",
                        "详情", "关闭", { _, _ ->
                    //查看详情
                    RainAndPointActivity.startAction(activity!!, false, fenxiStartTime, fenxiEndTime)
                }, null)
            } else {
                ZXDialogUtil.showInfoDialog(activity!!, "分析结果",
                        "未发现暴雨及以上降雨。")
            }
        }
    }

    fun showCallout(position: Int, lx: Int) {
        var mCallout = mapListener.getMapView().callout
        mCallout.hide()
        val contentView = LayoutInflater.from(activity!!).inflate(R.layout.map_callout, null)
        var callout = contentView.findViewById<TextView>(R.id.tv_callout)
        if (lx == 1) {
            var point = Point(maxRainList[position].longitude, maxRainList[position].latitude)

            callout.text = maxRainList[position].areaname + "，" + maxRainList[position].townsname + "，" + maxRainList[position].sensorname
            mCallout.show(point, contentView)

        } else if (lx == 2) {//隐患点
            HiddenPointActivity.startAction(activity!!, false, fenxiYHDList[position].pkiaa)
        } else if (lx == 3) {//雨量站
            var point = Point(fenxiYLZList[position].longitude, fenxiYLZList[position].latitude)
            callout.text = fenxiYLZList[position].areaname + "，" + fenxiYLZList[position].townsname + "，" + fenxiYLZList[position].sensorname

            mCallout.show(point, contentView)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig != null && ll_mapBtn_resetscale != null && ll_mapBtn_location != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                if (!ZXDeviceUtil.isTablet()) ll_mapBtn_resetscale.layoutParams = RelativeLayout.LayoutParams(resources.getDimension(R.dimen.map_btn_width).toInt(), resources.getDimension(R.dimen.map_btn_width).toInt()).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    addRule(RelativeLayout.LEFT_OF, R.id.ll_mapBtn_location)
                    setMargins(0, ZXSystemUtil.dp2px(25.0f), ZXSystemUtil.dp2px(70.0f), 0)
                }
                if (!ZXDeviceUtil.isTablet()) ll_mapBtn_location.layoutParams = RelativeLayout.LayoutParams(resources.getDimension(R.dimen.map_btn_width).toInt(), resources.getDimension(R.dimen.map_btn_width).toInt()).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    setMargins(0, ZXSystemUtil.dp2px(25.0f), ZXSystemUtil.dp2px(10.0f), 0)
                }
                rv_mapBtn_list.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                    if (!ZXDeviceUtil.isTablet()) addRule(RelativeLayout.BELOW, R.id.ll_mapBtn_resetscale)
                    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    if (ZXDeviceUtil.isTablet()) setMargins(0, ZXSystemUtil.dp2px(70.0f), 0, 0)
                }
                tv_rain_info.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    setMargins(0, 50, 450, 0)
                }
                tv_rain_times.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                    //                    if (!ZXDeviceUtil.isTablet()) addRule(RelativeLayout.LEFT_OF, R.id.ll_mapBtn_resetscale)
//                    setMargins(0, ZXSystemUtil.dp2px(50.0f), ZXSystemUtil.dp2px(15.0f), 0)

                    addRule(RelativeLayout.BELOW, R.id.tv_rain_info)
                    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    setMargins(0, 0, 400, 0)
                }
                tv_tuceng_info.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                    addRule(RelativeLayout.BELOW, R.id.tv_rain_times)
                    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    setMargins(0, 50, 450, 0)
//                    setMargins(0, if (tv_rain_times.visibility == View.GONE) 50 else 0, 450, 0)
                }
                tv_tuceng_times.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                    addRule(RelativeLayout.BELOW, R.id.tv_tuceng_info)
                    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    setMargins(0, 0, 400, 0)
                }
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
                ll_mapBtn_resetscale.layoutParams = defaultResetScaleParams
                ll_mapBtn_location.layoutParams = defaultLocationParams
                rv_mapBtn_list.layoutParams = defaultListParams
                tv_rain_times.layoutParams = defaultTimesParams
                tv_rain_info.layoutParams = defaultInfoParams
                tv_tuceng_info.layoutParams = defaultTucengInfoParams
                tv_tuceng_times.layoutParams = defaultTucengTimeInfoParams
            }
        }
    }

    override fun onDestroy() {
        if (hiddenClusterUtil != null) {
            hiddenClusterUtil!!.onDestory()
        }
        if (disasterClusterUtil != null) {
            disasterClusterUtil!!.onDestory()
        }
        super.onDestroy()
    }

    data class LegendBean(var text: String, @DrawableRes var bg: Int, var remark: String = "")
}

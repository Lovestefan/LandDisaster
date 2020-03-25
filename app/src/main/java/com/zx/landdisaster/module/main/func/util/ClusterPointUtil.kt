package com.zx.landdisaster.module.main.func.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.MapView
import com.esri.core.geometry.Point
import com.esri.core.map.Graphic
import com.esri.core.symbol.PictureMarkerSymbol
import com.esri.core.symbol.TextSymbol
import com.google.gson.Gson
import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.bean.AreaBean
import com.zx.landdisaster.module.main.bean.DisasterPointBean
import com.zx.landdisaster.module.main.bean.HiddenPointBean
import com.zx.landdisaster.module.main.func.listener.MapListener
import com.zx.landdisaster.module.main.ui.MapFragment
import com.zx.zxutils.util.ZXDeviceUtil
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Xiangb on 2019/3/18.
 * 功能：点聚合工具
 */
class ClusterPointUtil(var context: Context, var type: ClusterType, var mapListener: MapListener) {

    enum class ClusterType { DISASTER, HIDDEN }

    private val clusterLayer = GraphicsLayer()
    private val normalLayer = GraphicsLayer()
    val datas = arrayListOf<ClusterBean>()//点集
    private val clusterIcon = if (type == ClusterType.DISASTER) {
        PictureMarkerSymbol(ContextCompat.getDrawable(context,if (ZXDeviceUtil.isTablet()) R.drawable.cluster_icon_disaster_hdpi else  R.drawable.cluster_icon_disaster))//聚合点图标
    } else if (type == ClusterType.HIDDEN) {
        PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.cluster_icon_hidden_hdpi else R.drawable.cluster_icon_hidden))//聚合点图标
    } else {
        PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.cluster_icon_disaster_hdpi else R.drawable.cluster_icon_disaster))//聚合点图标
    }
    private val normalIcon1 = PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_2_hdpi else R.drawable.normal_icon_2))//图标-灾情
    private val normalIcon2 = PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_3_hdpi else R.drawable.normal_icon_3))//图标-险情
    private val normalIcon3 = PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_1_hdpi else R.drawable.normal_icon_1))//图标-隐患点
    private val normalDis1 = PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_1_hdpi else R.drawable.normal_icon_dis_1))//图标-隐患点
    private val normalDis2 = PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_2_hdpi else R.drawable.normal_icon_dis_2))//图标-隐患点
    private val normalDis3 = PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_3_hdpi else R.drawable.normal_icon_dis_3))//图标-隐患点
    private val normalDis4 = PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_4_hdpi else R.drawable.normal_icon_dis_4))//图标-隐患点
    private val normalDis5 = PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_5_hdpi else R.drawable.normal_icon_dis_5))//图标-隐患点
    private val normalDis6 = PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_6_hdpi else R.drawable.normal_icon_dis_6))//图标-隐患点
    private val normalDis7 = PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_7_hdpi else R.drawable.normal_icon_dis_7))//图标-隐患点
    private val normalDis8 = PictureMarkerSymbol(ContextCompat.getDrawable(context, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_dis_8_hdpi else R.drawable.normal_icon_dis_8))//图标-隐患点

    private val graphicAll = arrayListOf<Graphic>()//全部点的graphic集合
//    private val graphicAll = arrayOf<Graphic>()//全部点的graphic集合

    private val areaGraphicBeansMap: HashMap<String, GraphicBean> = hashMapOf()

    private var symbolClickCall: (String) -> Unit = {}
    private var mapView: MapView

    private val threadList = arrayListOf<Thread>()

    private var showAreaGraphice = false

    private val clusterScale = 1000000.0

    private var isDestory = false

    private lateinit var areaList: List<AreaBean>

    init {
        clusterLayer.isVisible = false
        normalLayer.isVisible = false
        mapView = mapListener.getMapView()
        mapView.addLayer(clusterLayer)
        mapView.addLayer(normalLayer)
        mapListener.addZoomListener(object : MapFragment.MyZoomListener {
            override fun postAction(p0: Float, p1: Float, p2: Double) {
                if (!clusterLayer.isVisible && !normalLayer.isVisible || datas.isEmpty()) {
                    return
                }
                if (type == ClusterType.HIDDEN) {//隐患点
                    if (mapView.scale <= clusterScale && clusterLayer.isVisible && !normalLayer.isVisible) {//不进行聚合
                        normalLayer.isVisible = true
                        clusterLayer.isVisible = false
                        if (showAreaGraphice) {
                            showAreaGraphice = false
                            handler.sendEmptyMessage(1)
                        }
                    } else if (mapView.scale > clusterScale && !clusterLayer.isVisible && normalLayer.isVisible) {//聚合
                        normalLayer.isVisible = false
                        clusterLayer.isVisible = true
                    }
                }
            }
        })
        mapListener.addSingleTagListener(object : MapFragment.MySingleTapListener {
            override fun onSingleTap(x: Float, y: Float) {
                if (clusterLayer.isVisible && !normalLayer.isVisible) {//当前为聚合图层可见
                    val ids = clusterLayer.getGraphicIDs(x, y, 10)
                    if (ids != null && ids.isNotEmpty()) {
                        val graphic = clusterLayer.getGraphic(ids[0])
                        if (graphic?.getAttributeValue("areacode") != null) {
                            if (type == ClusterType.HIDDEN) {//隐患点才有聚合
                                mapView.zoomToScale(mapView.toMapPoint(x, y), clusterScale)
                                normalLayer.isVisible = true
                                clusterLayer.isVisible = false
                                val areacode = graphic.getAttributeValue("areacode")
                                showAreaGraphice = true
                                handler.sendMessage(Message().apply {
                                    obj = areacode
                                    what = 3
                                })
                            }
                        }
                    }
                } else if (!clusterLayer.isVisible && normalLayer.isVisible) {//当前为普通图层可见
                    val ids = normalLayer.getGraphicIDs(x, y, 10)
                    if (ids != null && ids.isNotEmpty()) {
                        val graphic = normalLayer.getGraphic(ids[0])
                        if (graphic != null && graphic.attributes.isNotEmpty()) {
                            if (type == ClusterType.DISASTER) {//灾险情
                                symbolClickCall(graphic.getAttributeValue("pkidd") as String)
                            } else if (type == ClusterType.HIDDEN) {//隐患点
                                symbolClickCall(graphic.getAttributeValue("pkiaa") as String)
                            }
                        }
                    }

                }
            }

        })
    }

    /**
     * 设置点击回调
     */
    fun setSymbolClickCall(symbolClickCall: (String) -> Unit) {
        this.symbolClickCall = symbolClickCall
    }

    //切换显示
    fun showCluster() {
        if (clusterLayer.isVisible || normalLayer.isVisible) {//将所有图层隐藏
            clusterLayer.isVisible = false
            normalLayer.isVisible = false
        } else {//显示图层
            if (type == ClusterType.DISASTER) {//灾险情仅显示普通图层
                normalLayer.isVisible = true
                clusterLayer.isVisible = false
            } else if (type == ClusterType.HIDDEN) {//隐患点需要根据比例尺决定
                if (mapView.scale > clusterScale) {
                    clusterLayer.isVisible = true
                    normalLayer.isVisible = false
                } else {
                    clusterLayer.isVisible = false
                    normalLayer.isVisible = true
                }
            }
        }
    }

    /**
     * 定位改变
     */
    fun locationChange() {
        if (clusterLayer.isVisible || normalLayer.isVisible) {
            normalLayer.isVisible = true
            clusterLayer.isVisible = false
            if (showAreaGraphice) {
                showAreaGraphice = false
                handler.sendEmptyMessage(1)
            }
        }
    }

    /**
     * 重置比例尺改变
     */
    fun resetScaleChange() {
        if (clusterLayer.isVisible || normalLayer.isVisible) {
            normalLayer.isVisible = false
            clusterLayer.isVisible = true
        }
    }

    /**
     * 判断是否开启工具
     */
    fun isShowLayer(): Boolean {
        return clusterLayer.isVisible || normalLayer.isVisible
    }

    /**
     * 设置点集
     */
    fun setDatas(disasterList: List<DisasterPointBean>? = null, hiddenPointBean: List<HiddenPointBean>? = null) {
        graphicAll.clear()
        this.datas.clear()
        this.datas.addAll(convertToClusterBean(disasterList, hiddenPointBean))
        if (type == ClusterType.DISASTER) {//灾险
            handler.sendEmptyMessage(1)
        } else {
            val json = context.assets.fileAsString("json", "area.json")
            areaList = Gson().fromJson<Array<AreaBean>>(json, Array<AreaBean>::class.java).toMutableList()
            handler.sendEmptyMessage(1)
            handler.sendEmptyMessage(0)
        }
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg != null) {
                try {
                    if (msg.what == 0) {//做聚合graphic添加
                        threadList.add(Thread { clusterPoint() }.apply { start() })
                    } else if (msg.what == 1) {//做普通graphic添加
                        threadList.add(Thread { drawNormalPoint() }.apply { start() })
                    } else if (msg.what == 3) {//做区域graphice添加
                        val areacode = msg.obj.toString()
                        threadList.add(Thread { drawNormalAreaPoint(areacode) }.apply { start() })
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun convertToClusterBean(disasterList: List<DisasterPointBean>? = null, hiddenList: List<HiddenPointBean>? = null): List<ClusterBean> {
        var clusterList = arrayListOf<ClusterBean>()
        if (disasterList != null) {
            if (disasterList.isNotEmpty()) {
                disasterList.forEach {
                    val attr = hashMapOf<String, String>()
                    attr["hazardname"] = it.hazardname
                    attr["pkidd"] = it.pkidd
                    attr["hazardtype"] = it.hazardtype.toString()
                    attr["scalelevel"] = it.scalelevel.toString()
                    attr["happentime"] = it.happentime.toString()
                    attr["longitude"] = it.longitude.toString()
                    attr["latitude"] = it.latitude.toString()
                    clusterList.add(ClusterBean(it.longitude, it.latitude, attr))
                }
            }


        } else if (hiddenList != null) {
            if (hiddenList.isNotEmpty()) {
                hiddenList.forEach {
                    val attr = hashMapOf<String, String>()
                    attr["pkiaa"] = it.pkiaa
                    attr["name"] = it.name
                    attr["type"] = it.type
                    attr["address"] = it.address
                    attr["x"] = it.x
                    attr["y"] = it.y
                    attr["elevation"] = it.elevation
                    attr["indoorcode"] = it.indoorcode
                    attr["outdoorcode"] = it.outdoorcode
                    attr["disastegrad"] = it.disastegrad
                    attr["destroyshouse"] = it.destroyshouse.toString()
                    attr["dangerousrank"] = it.dangerousrank
                    attr["areacode"] = it.areacode
                    attr["disasterstatus"] = it.disasterstatus
                    attr["scalelevel"] = it.scalelevel.toString()
                    attr["disasterstatusDesc"] = it.disasterstatusDesc
                    attr["longitude"] = it.longitude.toString()
                    attr["latitude"] = it.latitude.toString()
                    clusterList.add(ClusterBean(it.longitude, it.latitude, attr))
                }
            }
        }
        return clusterList
    }

    //进行点聚合
    private fun clusterPoint() {
        clusterLayer.removeAll()
        if (areaGraphicBeansMap.isEmpty()) {
            for (clusterBean in datas) {
                var areacode = clusterBean.attr["areacode"].toString()
                if (areacode.contains("null")) {
                    continue
                }
                if (areacode.length > 6) {
                    areacode = areacode.substring(0, 6)
                }
                val graphicBean = areaGraphicBeansMap[areacode]
                if (graphicBean != null) {
                    graphicBean.num = graphicBean.num + 1
                    graphicBean.points.add(clusterBean)
                } else {
                    for ((x, y, areaCode) in areaList) {
                        if (isDestory) {
                            return
                        }
                        if (areaCode == areacode) {
                            val point = Point(x, y - 0.1)
                            val clusterBeans = ArrayList<ClusterBean>()
                            clusterBeans.add(clusterBean)
                            areaGraphicBeansMap.put(areacode, GraphicBean(point, clusterBeans, clusterBeans.size, clusterIcon, clusterBean.attr))
                            break
                        }
                    }
                }
            }
        }
        areaGraphicBeansMap.forEach {
            if (isDestory) {
                return
            }
            val clusterNum = TextSymbol(15, it.value.num.toString(), Color.WHITE)
            clusterNum.offsetX = when (it.value.num.toString().length) {
                1 -> -6.0f
                2 -> -10f
                3 -> -14.0f
                4 -> -18.0f
                else -> -18.0f
            }
            clusterNum.offsetY = -9f
            val attributes = hashMapOf<String, Any>()
            attributes.put("areacode", it.key)
            val graphicIcon = Graphic(it.value.centerPoint, clusterIcon, attributes)
            val graphicNum = Graphic(it.value.centerPoint, clusterNum, attributes)
            clusterLayer.addGraphic(graphicIcon)
            clusterLayer.addGraphic(graphicNum)
        }
    }

    /**
     * 初始化普通的点数据
     */
    private fun drawNormalPoint() {
        normalLayer.removeAll()
        if (graphicAll.isEmpty()) {
            val graphicBeansAll = arrayListOf<GraphicBean>()
            datas.forEach {
                if (isDestory) {
                    return
                }
                var picIcon: PictureMarkerSymbol? = null
                if (type == ClusterType.DISASTER) {
                    if (it.attr.get("hazardtype") == "1") {
                        picIcon = normalIcon1
                    } else {
                        picIcon = normalIcon2
                    }
                } else if (type == ClusterType.HIDDEN) {
                    picIcon = when (it.attr.get("type")) {
                        "崩塌" -> normalDis1
                        "滑坡" -> normalDis2
                        "地面沉降" -> normalDis3
                        "地裂缝" -> normalDis4
                        "泥石流" -> normalDis5
                        "斜坡" -> normalDis6
                        "地面塌陷" -> normalDis7
                        "库岸调查" -> normalDis8
                        else -> normalIcon3
                    }
                }
                graphicBeansAll.add(GraphicBean(Point(it.longitude, it.latitude), arrayListOf(), 1, picIcon, it.attr))
            }
            addNormalGraphicToLayer(graphicBeansAll)
        } else {
            normalLayer.addGraphics(graphicAll.toTypedArray())
        }
    }

    /**
     * 绘制选择区县的普通点数据
     */
    private fun drawNormalAreaPoint(areacode: String) {
        normalLayer.removeAll()
        val graphicBeansAreacode = arrayListOf<GraphicBean>()
        datas.forEach {
            if (isDestory) {
                return
            }
            var picIcon: PictureMarkerSymbol? = null
            if (type == ClusterType.DISASTER) {
                if (it.attr.get("hazardtype") == "1") {
                    picIcon = normalIcon1
                } else {
                    picIcon = normalIcon2
                }
                graphicBeansAreacode.add(GraphicBean(Point(it.longitude, it.latitude), arrayListOf(), 1, picIcon, it.attr))
            } else if (type == ClusterType.HIDDEN) {
                picIcon = when (it.attr.get("type")) {
                    "崩塌" -> normalDis1
                    "滑坡" -> normalDis2
                    "地面沉降" -> normalDis3
                    "地裂缝" -> normalDis4
                    "泥石流" -> normalDis5
                    "斜坡" -> normalDis6
                    "地面塌陷" -> normalDis7
                    "库岸调查" -> normalDis8
                    else -> normalIcon3
                }
                var areaCode = it.attr["areacode"].toString()
                if (areaCode.length > 6) {
                    areaCode = areaCode.substring(0, 6)
                }
                if (areaCode == areacode) {
                    graphicBeansAreacode.add(GraphicBean(Point(it.longitude, it.latitude), arrayListOf(), 1, picIcon, it.attr))
                }
            }
        }
        addNormalGraphicToLayer(graphicBeansAreacode)
    }

    //向layer添加普通的灾险情graphic
    private fun addNormalGraphicToLayer(graphicBeans: ArrayList<GraphicBean>) {
        graphicBeans.forEach {
            if (isDestory) {
                return
            }
            val graphicIcon = Graphic(it.centerPoint, it.symbol, it.attr)
            normalLayer.addGraphic(graphicIcon)
            graphicAll.add(graphicIcon)
        }
    }

    fun onDestory() {
        isDestory = true
        if (threadList.size > 0) {
            threadList.forEach {
                it.interrupt()
            }
        }
    }


    data class ClusterBean(var longitude: Double, var latitude: Double, var attr: Map<String, String>)

    data class GraphicBean(var centerPoint: Point, var points: ArrayList<ClusterBean>, var num: Int, var symbol: PictureMarkerSymbol?, var attr: Map<String, String> = hashMapOf())


    private fun AssetManager.fileAsString(subdirectory: String, filename: String): String {
        return open("$subdirectory/$filename").use {
            it.readBytes().toString(Charset.defaultCharset())
        }
    }
}
package com.zx.landdisaster.module.main.ui

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.esri.android.map.LocationDisplayManager
import com.esri.android.map.MapOnTouchListener
import com.esri.android.map.MapView
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer
import com.esri.android.map.event.OnStatusChangedListener
import com.esri.android.map.event.OnZoomListener
import com.esri.android.runtime.ArcGISRuntime
import com.esri.core.symbol.MarkerSymbol
import com.esri.core.symbol.PictureMarkerSymbol
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.LocationTool
import com.zx.landdisaster.module.main.func.listener.MapListener
import com.zx.landdisaster.module.main.func.util.MapExtentUtil
import com.zx.landdisaster.module.main.func.util.OnlineTileLayer
import com.zx.landdisaster.module.main.mvp.contract.MapContract
import com.zx.landdisaster.module.main.mvp.model.MapModel
import com.zx.landdisaster.module.main.mvp.presenter.MapPresenter
import com.zx.zxutils.util.ZXDeviceUtil
import kotlinx.android.synthetic.main.fragment_map.*

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MapFragment : BaseFragment<MapPresenter, MapModel>(), MapContract.View {
    private var locationDisplayManager: LocationDisplayManager? = null

    private var vectorLayer: OnlineTileLayer? = null
    private var imageLayer: OnlineTileLayer? = null
    private var imageLable: OnlineTileLayer? = null

    //    private var fangfanLayer: ArcGISDynamicMapServiceLayer? = null
//    private var yifaLayer: ArcGISDynamicMapServiceLayer? = null
    private var fxyjLayer: ArcGISDynamicMapServiceLayer? = null
    private var qxybLayer: ArcGISDynamicMapServiceLayer? = null
//    private var vectorGlobalLayer: TianDiTuLayer? = null
//    private var vectorGlobalLable: TianDiTuLayer? = null
//    private var imageGlobalLayer: TianDiTuLayer? = null
//    private var imageGlobalLable: TianDiTuLayer? = null

    val mapListener = MyListener()

    private var defaultTouchListener: MapOnTouchListener? = null

    private var singleTapListenerList = arrayListOf<MySingleTapListener>()
    private var zoomListenerList = arrayListOf<MyZoomListener>()

    private var initMap: () -> Unit = {}

    companion object {
        /**
         * 启动器
         */
        fun newInstance(autoLocation: Boolean = true): MapFragment {
            val fragment = MapFragment()
            val bundle = Bundle()
            bundle.putBoolean("autoLocation", autoLocation)
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_map
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ArcGISRuntime.setClientId("5SKIXc21JlankElJ")
        ArcGISRuntime.License.setLicense("runtimestandard,101,rux00000,none,XXXXXXX")
        map_view.setMapBackground(Color.WHITE, -1, 0.0f, 0.0f)
//        showLoading("地图正在加载中")

        var user = UserManager.getUser()

        if (user.mapSl != "" && user.mapYx != "" && user.mapYxzj != "") {
            vectorLayer = OnlineTileLayer(user.mapSl, "vector")
            imageLayer = OnlineTileLayer(user.mapYx, "image")
            imageLable = OnlineTileLayer(user.mapYxzj, "image_lable")
        } else {
            vectorLayer = OnlineTileLayer("http://www.tianditucq.com/RemoteRest/services/CQMap_VEC/MapServer", "vector")
            imageLayer = OnlineTileLayer("http://www.tianditucq.com/RemoteRest/services/CQMap_IMG/MapServer", "image")
            imageLable = OnlineTileLayer("http://www.tianditucq.com/RemoteRest/services/CQMap_IMG_LABEL/MapServer", "image_lable")
        }


        if (ApiConfigModule.ISRELEASE) {
            fxyjLayer = ArcGISDynamicMapServiceLayer("http://183.230.8.9:4001/fxyjt/MapServer")
            qxybLayer = ArcGISDynamicMapServiceLayer("http://183.230.8.9:4001/qxybt/MapServer")
        } else {
            fxyjLayer = ArcGISDynamicMapServiceLayer("http://192.168.11.221:6080/arcgis/rest/services/fxyjt/MapServer")
            qxybLayer = ArcGISDynamicMapServiceLayer("http://192.168.11.221:6080/arcgis/rest/services/qxybt/MapServer")
        }


//        vectorGlobalLayer = TianDiTuLayer(TianDiTuLayerTypes.TIANDITU_VECTOR_2000)
//        vectorGlobalLable = TianDiTuLayer(TianDiTuLayerTypes.TIANDITU_VECTOR_ANNOTATION_CHINESE_2000)
//        imageGlobalLayer = TianDiTuLayer(TianDiTuLayerTypes.TIANDITU_IMAGE_2000)
//        imageGlobalLable = TianDiTuLayer(TianDiTuLayerTypes.TIANDITU_IMAGE_ANNOTATION_CHINESE_2000)

//        map_view.addLayer(vectorGlobalLayer)
//        map_view.addLayer(vectorGlobalLable)
//        map_view.addLayer(imageGlobalLayer)
//        map_view.addLayer(imageGlobalLable)

        map_view.addLayer(vectorLayer)
        map_view.addLayer(imageLayer)
        map_view.addLayer(imageLable)


//        fangfanLayer!!.opacity = 0.5f
//        yifaLayer!!.opacity = 0.5f
        fxyjLayer!!.opacity = 0.5f
        qxybLayer!!.opacity = 0.5f

//        map_view.addLayer(fangfanLayer)
//        map_view.addLayer(yifaLayer)
        map_view.addLayer(fxyjLayer)
        map_view.addLayer(qxybLayer)

        imageLayer!!.isVisible = false
        imageLable!!.isVisible = false

//        fangfanLayer!!.isVisible = false
//        yifaLayer!!.isVisible = false
        fxyjLayer!!.isVisible = false
        qxybLayer!!.isVisible = false
//        imageGlobalLayer!!.isVisible = false
//        imageGlobalLable!!.isVisible = false

        defaultTouchListener = MapOnTouchListener(activity!!, map_view)

    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        map_view.setOnStatusChangedListener { any, status ->
            if (status == OnStatusChangedListener.STATUS.INITIALIZED) {//初始化成功

                MapExtentUtil.drawCQExtent(map_view, activity!!)
                if (!isDetail) {
                    mapListener.resetScale()
                }
                if (UserManager.getUser().currentUser != null && UserManager.getUser().currentUser!!.areaCode != null) {
                    val extentString = MapExtentUtil.getExtentString(UserManager.getUser().currentUser!!.areaCode!!)
                    if (extentString.isNotEmpty()) {
                        onMapExtentResult(extentString, UserManager.getUser().currentUser!!.areaCode!!)
                    } else {
                        mPresenter.getMapExtent(ApiParamUtil.esriQueryArea(UserManager.getUser().currentUser!!.areaCode!!, UserManager.getUser().currentUser!!.areaName!!), UserManager.getUser().currentUser!!.areaCode!!)
                    }
                }
                initMap()
                if (arguments!!.getBoolean("autoLocation")) {
//                    mapListener.doLocation()
                }
                map_bg.visibility = View.GONE
            }
        }
        map_view.setOnSingleTapListener { x, y ->
            if (singleTapListenerList.isNotEmpty()) {
                singleTapListenerList.forEach {
                    it.onSingleTap(x, y)
                }
            }
        }
        map_view.onZoomListener = object : OnZoomListener {
            override fun preAction(p0: Float, p1: Float, p2: Double) {
            }

            override fun postAction(p0: Float, p1: Float, p2: Double) {
                if (zoomListenerList.isNotEmpty()) {
                    zoomListenerList.forEach {
                        it.postAction(p0, p1, p2)
                    }
                }
            }

        }
    }

    fun setMapInitCall(initMap: () -> Unit) {
        this.initMap = initMap
    }

    var isDetail = false

    /**
     * 定位监听
     */
    inner class MyListener : MapListener {
        override fun getOnTouchListener(): MapOnTouchListener? {
            return defaultTouchListener
        }

        override fun isDetail(d: Boolean) {
            isDetail = d
        }

        override fun getMapView(): MapView {
            return map_view
        }

        override fun doLocation(): Boolean {
            getPermission(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (locationDisplayManager != null) {
                    locationDisplayManager!!.isShowLocation = !locationDisplayManager!!.isShowLocation
                    locationDisplayManager!!.isShowPings = !locationDisplayManager!!.isShowPings
                    if (locationDisplayManager!!.isShowLocation) {
                        if (locationDisplayManager!!.location != null) {
                            map_view.centerAt(locationDisplayManager!!.location.latitude, locationDisplayManager!!.location.longitude, true)
                            map_view.scale = 9000.00
                        } else {
                            LocationTool.getLocation(activity!!) {
                                if (it != null) {
                                    map_view.centerAt(it.latitude, it.longitude, true)
                                    map_view.scale = 9000.00
                                } else {
                                    showToast("当前GPS信号弱，未获取到位置信息，请稍后再试")
                                }
                            }
                        }
                    }
                } else {
                    locationDisplayManager = map_view.locationDisplayManager.apply {
                        isAllowNetworkLocation = true
                        isShowLocation = true
                        isUseCourseSymbolOnMovement = false
                        isAccuracyCircleOn = false
                        locationAcquiringSymbol = PictureMarkerSymbol(activity!!, ContextCompat.getDrawable(activity!!, R.drawable.location_symbol)) as MarkerSymbol?
                        defaultSymbol = PictureMarkerSymbol(activity!!, ContextCompat.getDrawable(activity!!, R.drawable.location_symbol))
                        autoPanMode = LocationDisplayManager.AutoPanMode.LOCATION
                        isShowPings = true
                        start()
                        if (location != null) {
                            map_view.centerAt(location.latitude, location.longitude, true)
                            map_view.scale = 9000.00
                        } else {
                            LocationTool.getLocation(activity!!) {
                                if (it != null) {
                                    map_view.centerAt(it.latitude, it.longitude, true)
                                    map_view.scale = 9000.00
                                } else {
                                    showToast("当前GPS信号弱，未获取到位置信息，请稍后再试")
                                }
                            }
                        }
                    }
                }
            }
            if (locationDisplayManager == null) {
                return false
            } else {
                return locationDisplayManager!!.isShowLocation
            }
        }

        override fun resetScale() {
            map_view.centerAt(30.23485291008127, 107.65253363384483, true)
            map_view.scale = if (ZXDeviceUtil.isTablet()) 4000000.0 else 6500000.0
//            handler.postDelayed({
//            map_view.scale = 9000000.0
////                map_view.setScale(9000000.0, false)
//            }, 500)
//            map_view.setScale(6500000.0, true)
//            map_view.scale = 9000000.0
        }

        override fun changeMap(mapType: String) {
            when (mapType) {
                "vector" -> {
                    imageLayer!!.isVisible = false
                    imageLable!!.isVisible = false
                    vectorLayer!!.isVisible = true

//                    vectorGlobalLayer!!.isVisible = true
//                    vectorGlobalLable!!.isVisible = true
//                    imageGlobalLayer!!.isVisible = false
//                    imageGlobalLable!!.isVisible = false
//
//                    if (MapExtentUtil.cqImageExtentLayer != null) {
//                        MapExtentUtil.cqImageExtentLayer!!.isVisible = false
//                    }
//                    if (MapExtentUtil.cqVectorExtentLayer != null) {
//                        MapExtentUtil.cqVectorExtentLayer!!.isVisible = true
//                    }
                }
                "image" -> {
                    imageLayer!!.isVisible = true
                    imageLable!!.isVisible = true
                    vectorLayer!!.isVisible = false

//                    vectorGlobalLayer!!.isVisible = false
//                    vectorGlobalLable!!.isVisible = false
//                    imageGlobalLayer!!.isVisible = true
//                    imageGlobalLable!!.isVisible = true
//
//                    if (MapExtentUtil.cqImageExtentLayer != null) {
//                        MapExtentUtil.cqImageExtentLayer!!.isVisible = true
//                    }
//                    if (MapExtentUtil.cqVectorExtentLayer != null) {
//                        MapExtentUtil.cqVectorExtentLayer!!.isVisible = false
//                    }
                }
//                "fangfan" -> {//防范分区
//                    fangfanLayer!!.isVisible = !fangfanLayer!!.isVisible
//                    yifaLayer!!.isVisible = false
//                    fxyjLayer!!.isVisible = false
//                    qxybLayer!!.isVisible = false
//                }
//                "yifa" -> {//易发分区
//                    fangfanLayer!!.isVisible = false
//                    yifaLayer!!.isVisible = !yifaLayer!!.isVisible
//                    fxyjLayer!!.isVisible = false
//                    qxybLayer!!.isVisible = false
//                }
                "fengxianyujin" -> {//风险预警
//                    fangfanLayer!!.isVisible = false
//                    yifaLayer!!.isVisible = false
                    fxyjLayer!!.isVisible = !fxyjLayer!!.isVisible
                    qxybLayer!!.isVisible = false
                }
                "qixiangyubao" -> {//气象预报
//                    fangfanLayer!!.isVisible = false
//                    yifaLayer!!.isVisible = false
                    fxyjLayer!!.isVisible = false
                    qxybLayer!!.isVisible = !qxybLayer!!.isVisible
                }
            }
        }

        override fun addSingleTagListener(mySingleTapListener: MySingleTapListener) {
            singleTapListenerList.add(mySingleTapListener)
        }

        override fun addZoomListener(myZoomListener: MyZoomListener) {
            zoomListenerList.add(myZoomListener)
        }

        override fun setAlpha(type: Int, alpha: Float) {
            if (type == 1) {//风险预警
                fxyjLayer!!.opacity = alpha
            } else if (type == 2) {//气象预报
                qxybLayer!!.opacity = alpha
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (map_view != null) map_view.recycle()
    }

    override fun onMapExtentResult(extentString: String, areaCode: String) {
        MapExtentUtil.drawToMap(map_view, extentString, areaCode)
    }

    interface MySingleTapListener {
        fun onSingleTap(x: Float, y: Float)
    }

    interface MyZoomListener {
        fun postAction(p0: Float, p1: Float, p2: Double)
    }
}

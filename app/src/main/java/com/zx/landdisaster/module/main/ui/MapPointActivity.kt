package com.zx.landdisaster.module.main.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.esri.android.map.GraphicsLayer
import com.esri.core.geometry.Point
import com.esri.core.map.Graphic
import com.esri.core.symbol.PictureMarkerSymbol
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.disaster.bean.HiddenDetailBean
import com.zx.landdisaster.module.disaster.bean.ReportDetailBean
import com.zx.landdisaster.module.disaster.ui.ReportDetailActivity
import com.zx.landdisaster.module.main.mvp.contract.MapPointContract
import com.zx.landdisaster.module.main.mvp.model.MapPointModel
import com.zx.landdisaster.module.main.mvp.presenter.MapPointPresenter
import com.zx.zxutils.util.ZXFragmentUtil


/**
 * Create By admin On 2017/7/11
 * 功能：地图查看点
 */
class MapPointActivity : BaseActivity<MapPointPresenter, MapPointModel>(), MapPointContract.View {

    lateinit var mapFragment: MapFragment
    lateinit var mapBtnFragment: MapBtnFragment

    var graphicsLayer = GraphicsLayer()

    var hiddenBean: HiddenDetailBean? = null
    var reportBean: ReportDetailBean? = null

    private lateinit var normalIcon1: PictureMarkerSymbol
    private lateinit var normalIcon2: PictureMarkerSymbol
    private lateinit var normalIcon3: PictureMarkerSymbol
    private lateinit var normalDis1: PictureMarkerSymbol
    private lateinit var normalDis2: PictureMarkerSymbol
    private lateinit var normalDis3: PictureMarkerSymbol
    private lateinit var normalDis4: PictureMarkerSymbol
    private lateinit var normalDis5: PictureMarkerSymbol
    private lateinit var normalDis6: PictureMarkerSymbol
    private lateinit var normalDis7: PictureMarkerSymbol
    private lateinit var normalDis8: PictureMarkerSymbol

    companion object {
        /**
         * 启动器
         */

        fun startAction(activity: Activity, isFinish: Boolean, hiddenDetailBean: HiddenDetailBean) {
            val intent = Intent(activity, MapPointActivity::class.java)
            intent.putExtra("hiddenDetailBean", hiddenDetailBean)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }

        fun startAction(activity: ReportDetailActivity, isFinish: Boolean, reportDetailBean: ReportDetailBean) {
            val intent = Intent(activity, MapPointActivity::class.java)
            intent.putExtra("reportDetailBean", reportDetailBean)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_map_point
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        normalIcon1 = PictureMarkerSymbol(ContextCompat.getDrawable(this, R.drawable.normal_icon_2))//图标-灾情
        normalIcon2 = PictureMarkerSymbol(ContextCompat.getDrawable(this, R.drawable.normal_icon_3))//图标-险情
        normalIcon3 = PictureMarkerSymbol(ContextCompat.getDrawable(this, R.drawable.normal_icon_1))//图标-隐患点
        normalDis1 = PictureMarkerSymbol(ContextCompat.getDrawable(this, R.drawable.normal_icon_dis_1))//图标-隐患点
        normalDis2 = PictureMarkerSymbol(ContextCompat.getDrawable(this, R.drawable.normal_icon_dis_2))//图标-隐患点
        normalDis3 = PictureMarkerSymbol(ContextCompat.getDrawable(this, R.drawable.normal_icon_dis_3))//图标-隐患点
        normalDis4 = PictureMarkerSymbol(ContextCompat.getDrawable(this, R.drawable.normal_icon_dis_4))//图标-隐患点
        normalDis5 = PictureMarkerSymbol(ContextCompat.getDrawable(this, R.drawable.normal_icon_dis_5))//图标-隐患点
        normalDis6 = PictureMarkerSymbol(ContextCompat.getDrawable(this, R.drawable.normal_icon_dis_6))//图标-隐患点
        normalDis7 = PictureMarkerSymbol(ContextCompat.getDrawable(this, R.drawable.normal_icon_dis_7))//图标-隐患点
        normalDis8 = PictureMarkerSymbol(ContextCompat.getDrawable(this, R.drawable.normal_icon_dis_8))//图标-隐患点

        if (intent.hasExtra("hiddenDetailBean")) {
            hiddenBean = intent.getSerializableExtra("hiddenDetailBean") as HiddenDetailBean
        }
        if (intent.hasExtra("reportDetailBean")) {
            reportBean = intent.getSerializableExtra("reportDetailBean") as ReportDetailBean
        }

        ZXFragmentUtil.addFragment(supportFragmentManager, MapFragment.newInstance().apply { mapFragment = this }, R.id.fm_map)
        var address = ""
        var longitude = 0.0
        var latitude = 0.0
        if (hiddenBean != null) {
            address = hiddenBean!!.address ?: ""
            longitude = hiddenBean!!.longitude ?: 0.0
            latitude = hiddenBean!!.latitude ?: 0.0
        } else if (reportBean != null) {
            address = reportBean!!.reportdata!!.address ?: ""
            longitude = reportBean!!.reportdata!!.longitude!!.toDouble()
            latitude = reportBean!!.reportdata!!.latitude!!.toDouble()
        }
        ZXFragmentUtil.addFragment(supportFragmentManager, MapBtnFragment.newInstance(2, address, longitude, latitude).apply {
            mapBtnFragment = this
            setMapListener(mapFragment.mapListener)
            mapFragment.mapListener.isDetail(true)
        }, R.id.fm_btn)
        mapFragment.setMapInitCall {
            var picIcon: PictureMarkerSymbol? = null
            var point: Point? = null
            if (hiddenBean != null) {
                picIcon = when (hiddenBean!!.type) {
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
                point = Point(hiddenBean!!.longitude!!, hiddenBean!!.latitude!!)
            } else if (reportBean != null) {
                if (reportBean!!.reportdata!!.hazardtype == "1") {
                    picIcon = normalIcon1
                } else {
                    picIcon = normalIcon2
                }
                point = Point(reportBean!!.reportdata!!.longitude!!.toDouble(), reportBean!!.reportdata!!.latitude!!.toDouble())
            }
            graphicsLayer.addGraphic(Graphic(point, picIcon))
            mapFragment.mapListener.getMapView().centerAt(point, false)
            mapFragment.mapListener.getMapView().scale = 400000.0
            mapFragment.mapListener.getMapView().addLayer(graphicsLayer)
        }
        mapFragment.mapListener.addSingleTagListener(object : MapFragment.MySingleTapListener {
            override fun onSingleTap(x: Float, y: Float) {
                val ids = graphicsLayer.getGraphicIDs(x, y, 1)
                if (ids != null && ids.isNotEmpty()) {
                    val graphic = graphicsLayer.getGraphic(ids[0])
                    if (graphic != null) {
                        finish()
                    }
                }
            }
        })
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

}

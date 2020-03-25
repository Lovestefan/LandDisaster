package com.zx.landdisaster.module.main.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.MapView
import com.esri.core.geometry.Point
import com.esri.core.map.Graphic
import com.esri.core.symbol.PictureMarkerSymbol
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.main.mvp.contract.LocationSelectContract
import com.zx.landdisaster.module.main.mvp.model.LocationSelectModel
import com.zx.landdisaster.module.main.mvp.presenter.LocationSelectPresenter
import com.zx.zxutils.util.ZXFragmentUtil
import kotlinx.android.synthetic.main.activity_location_select.*
import kotlinx.android.synthetic.main.fragment_map.*


/**
 * Create By admin On 2017/7/11
 * 功能：坐标选点
 */
class LocationSelectActivity : BaseActivity<LocationSelectPresenter, LocationSelectModel>(), LocationSelectContract.View {

    lateinit var mapFragment: MapFragment
    lateinit var mapBtnFragment: MapBtnFragment
    var markerLayer = GraphicsLayer()
    var selectPoint: Point? = null

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, selectPoint: Point? = null) {
            val intent = Intent(activity, LocationSelectActivity::class.java)
            intent.putExtra("point", selectPoint)
            activity.startActivityForResult(intent, 0x01)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_location_select
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ZXFragmentUtil.addFragment(supportFragmentManager, MapFragment.newInstance().apply { mapFragment = this }, R.id.fm_location_map)
        ZXFragmentUtil.addFragment(supportFragmentManager, MapBtnFragment.newInstance(1).apply {
            mapBtnFragment = this
            setMapListener(mapFragment.mapListener)
        }, R.id.fm_btn)
        mapBtnFragment.setMapListener(mapFragment.mapListener)
        mapFragment.setMapInitCall {
            mapFragment.map_view.apply {
                addLayer(markerLayer)
                if (intent.hasExtra("point") && intent.getSerializableExtra("point") != null) {
                    selectPoint = intent.getSerializableExtra("point") as Point
                    addSymbol()
                }
                setOnSingleTapListener { x, y ->
                    selectPoint = toMapPoint(x, y)
                    addSymbol()
                }
            }
        }
    }

    private fun MapView.addSymbol() {
        markerLayer.removeAll()
        val picSymbol = PictureMarkerSymbol(ContextCompat.getDrawable(this@LocationSelectActivity, R.drawable.location_select_icon))
        picSymbol.offsetY = 20.0f
        picSymbol.offsetY = 20.0f
        markerLayer.addGraphic(Graphic(selectPoint, picSymbol))
        centerAt(selectPoint, true)
        toolbar_view.showRightText("确认")
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        toolbar_view.setRightClickListener {
            if (selectPoint!!.x < 105 || selectPoint!!.x > 110.5) {
                showToast("经度不符合规范，请重新选择")
            } else if (selectPoint!!.y < 28 || selectPoint!!.y > 32.5) {
                showToast("纬度不符合规范，请重新选择")
            } else {
                val intent = Intent()
                intent.putExtra("point", selectPoint)
                setResult(0x01, intent)
                finish()
            }
        }
    }

}

package com.zx.landdisaster.module.main.func.listener

import com.esri.android.map.MapOnTouchListener
import com.esri.android.map.MapView
import com.zx.landdisaster.module.main.ui.MapFragment

/**
 * Created by Xiangb on 2019/3/16.
 * 功能：
 */
interface MapListener {
    fun isDetail(isDetail: Boolean)

    fun getMapView(): MapView

    fun doLocation(): Boolean

    fun resetScale()

    fun changeMap(mapType: String)

    fun getOnTouchListener(): MapOnTouchListener?

    fun addZoomListener(myZoomListener: MapFragment.MyZoomListener)

    fun setAlpha(type: Int, alpha: Float)

    fun addSingleTagListener(mySingleTapListener: MapFragment.MySingleTapListener)

}
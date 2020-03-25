package com.zx.landdisaster.base.tool

import android.annotation.SuppressLint
import android.app.Activity
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.esri.core.geometry.Point
import com.tencent.bugly.crashreport.CrashReport
import com.zx.zxutils.ZXApp
import com.zx.zxutils.util.ZXLocationUtil
import java.util.*
import kotlin.concurrent.schedule

/**
 * Created by Xiangb on 2019/6/12.
 * 功能：
 */
@SuppressLint("StaticFieldLeak")
object LocationTool {

    var locationClient: LocationClient? = null
    var isReceiveStart = false
    var timerTask: TimerTask? = null
    var defaultPoint: PointBean? = null

    data class PointBean(var longitude: Double, var latitude: Double, var address: String?)

    fun getLocation(activity: Activity, baiduFirst: Boolean = false, locationCall: (PointBean?) -> Unit) {
        try {
            var point: PointBean? = null
            val location = ZXLocationUtil.getLocation(activity)
            if (location != null && !baiduFirst) {//普通定位
                if (location.latitude > 0 && location.longitude > 0) {
                    val address = if (ZXLocationUtil.getAddress(location.latitude, location.longitude) == null) {
                        "重庆市"
                    } else {
                        ZXLocationUtil.getAddress(location.latitude, location.longitude).subLocality
                    }
                    point = PointBean(location.longitude, location.latitude, address)
                    defaultPoint = point
                }
                locationCall(point)
            } else {//百度定位
                if (locationClient == null) {
                    locationClient = LocationClient(ZXApp.getContext())
                    val locationOption = LocationClientOption().apply {
                        locationMode = LocationClientOption.LocationMode.Hight_Accuracy//默认高精度
                        coorType = "bd09ll"//默认gcj02
                        scanSpan = 0//查询周期，默认为0
                        openGps = true
                    }
                    locationClient!!.locOption = locationOption
                    locationClient!!.registerLocationListener(object : BDAbstractLocationListener() {
                        override fun onReceiveLocation(p0: BDLocation?) {
                            try {
                                if (isReceiveStart && p0 != null) {
                                    val wgs84 = baiduToWgs84(p0.latitude, p0.longitude)
                                    if (wgs84.x < 0 || wgs84.y < 0) {
                                        return
                                    }
                                    if (timerTask != null) {
                                        timerTask!!.cancel()
                                    }
                                    val address = if (ZXLocationUtil.getAddress(wgs84.y, wgs84.x) == null) {
                                        p0.district
                                    } else {
                                        ZXLocationUtil.getAddress(wgs84.y, wgs84.x).subLocality
                                    }
                                    point = PointBean(wgs84.x, wgs84.y, address)
                                    defaultPoint = point
//                                    isLoad = false
                                    isReceiveStart = false
                                    locationClient!!.stop()
                                }
                            } catch (e: java.lang.Exception) {
                            }
                            locationCall(point)
                        }
                    })
                }
                isReceiveStart = true
                locationClient!!.start()
                timerTask = Timer().schedule(2000) {
                    if (isReceiveStart) {
                        isReceiveStart = false
//                        locationClient!!.stop()
                        val location1 = ZXLocationUtil.getLocation(activity)
                        if (location1 != null && location1.latitude > 0 && location1.longitude > 0) {
                            val address = if (ZXLocationUtil.getAddress(location1.latitude, location1.longitude) == null) {
                                "重庆市"
                            } else {
                                ZXLocationUtil.getAddress(location1.latitude, location1.longitude).subLocality
                            }
                            point = PointBean(location1.longitude, location1.latitude, address)
                            defaultPoint = point
                        }
                        locationCall(point)
                    }
                }
            }
        } catch (e: Exception) {
            locationCall(null)
            CrashReport.postCatchedException(e)
        }

    }

    private fun baiduToWgs84(bdLat: Double, bdLon: Double): Point {
        val map = HashMap<String, Double>()
        val PI = 3.14159265358979324
        val x_pi = 3.14159265358979324 * 3000.0 / 180.0
        val x = bdLon - 0.0065
        val y = bdLat - 0.006
        val z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi)
        val theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi)
        val gcjLon = z * Math.cos(theta)
        val gcjLat = z * Math.sin(theta)
        val a = 6378245.0
        val ee = 0.00669342162296594323
        var dLat = transformLat(gcjLon - 105.0, gcjLat - 35.0)
        var dLon = transformLon(gcjLon - 105.0, gcjLat - 35.0)
        val radLat = gcjLat / 180.0 * PI
        var magic: Double? = Math.sin(radLat)
        magic = 1 - ee * magic!! * magic
        val sqrtMagic = Math.sqrt(magic)
        dLat = dLat!! * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * PI)
        dLon = dLon!! * 180.0 / (a / sqrtMagic * Math.cos(radLat) * PI)
        dLat = gcjLat - dLat
        dLon = gcjLon - dLon
        map["wgs84lat"] = dLat
        map["wgs84lon"] = dLon
        return Point(dLon, dLat)
    }

    private fun transformLon(x: Double?, y: Double?): Double? {
        val PI = 3.14159265358979324
        var ret: Double = 300.0 + x!! + 2.0 * y!! + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x))
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0
        return ret
    }

    private fun transformLat(x: Double?, y: Double?): Double? {
        val PI = 3.14159265358979324
        var ret: Double = -100.0 + 2.0 * x!! + 3.0 * y!! + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x))
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0
        return ret
    }
}
package com.zx.landdisaster.module.main.func.util

import android.graphics.Color
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.MapView
import com.esri.core.geometry.GeometryEngine
import com.esri.core.map.Graphic
import com.esri.core.symbol.SimpleFillSymbol
import com.zx.landdisaster.module.main.bean.RainDataBean
import com.zx.zxutils.util.ZXStringUtil
import org.codehaus.jackson.JsonFactory
import org.json.JSONObject
import java.util.*


/**
 * Created by Xiangb on 2019/4/10.
 * 功能：根据json绘制图层
 */
class RainLayerUtil {

    var rainLayer: GraphicsLayer? = null
    private var drawTime: Long = 0
    var rainType = "1"

    private val colorList = arrayOf(Color.argb(0, 0, 0, 0),
            Color.argb(255, 166, 242, 143),
            Color.argb(255, 61, 186, 61),
            Color.argb(255, 97, 184, 255),
            Color.argb(255, 0, 0, 255),
            Color.argb(255, 255, 0, 255),
            Color.argb(255, 128, 0, 64))

    fun isShowRain(): Boolean = (rainLayer != null && rainLayer!!.isVisible)

    fun showRainLayer() {
        if (rainLayer != null) {
            rainLayer!!.isVisible = !rainLayer!!.isVisible
        }
    }

    fun isOutOfDate(): Boolean {
        if (drawTime > 0) {
            val calendar = Calendar.getInstance()
            val hourNow = calendar.get(Calendar.HOUR_OF_DAY)
            calendar.timeInMillis = drawTime
            val hourBefore = calendar.get(Calendar.HOUR_OF_DAY)
            if (hourNow == hourBefore) {
                return false
            }
        }
        return true
    }

    fun drawToMap(mapView: MapView, rainDataBean: RainDataBean) {
        try {
            drawTime = rainDataBean.date
            if (rainLayer == null) {
                rainLayer = GraphicsLayer()
            } else {
                rainLayer!!.removeAll()
            }
            rainLayer!!.opacity = 1.0f
            val jsonObj = JSONObject(ZXStringUtil.replaceBlank(rainDataBean.content.toString()))
            val jsonArr = jsonObj.getJSONArray("features")
            if (jsonArr.length() > 0) {
                for (i in 0 until jsonArr.length()) {
                    try {
                        val featureJson = jsonArr.getJSONObject(i)
                        val gridcode = featureJson.getJSONObject("attributes").getInt("gridcode")
                        val geometryJson = featureJson.getJSONObject("geometry")
                        val jsonFactory = JsonFactory()
                        val jsonParser = jsonFactory.createJsonParser(geometryJson.toString())
                        val geometry = GeometryEngine.jsonToGeometry(jsonParser)
                        val graphic = Graphic(geometry.geometry, SimpleFillSymbol(colorList[gridcode], SimpleFillSymbol.STYLE.SOLID).apply {
                            outline.width = 0.0f
                        })
                        rainLayer!!.addGraphic(graphic)
                    } catch (e: Exception) {
                        continue
                    }
                }
            }
            mapView.addLayer(rainLayer, 2)
            rainLayer!!.isVisible = false
            drawTime = System.currentTimeMillis()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
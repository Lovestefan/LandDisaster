package com.zx.landdisaster.module.main.func.util

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Color
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.MapView
import com.esri.core.geometry.Envelope
import com.esri.core.geometry.GeometryEngine
import com.esri.core.map.Graphic
import com.esri.core.symbol.SimpleFillSymbol
import com.zx.zxutils.util.ZXStringUtil
import org.codehaus.jackson.JsonFactory
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * Created by Xiangb on 2019/4/23.
 * 功能：
 */
object MapExtentUtil {

    private var extertLayer: GraphicsLayer? = null
    public var cqImageExtentLayer: GraphicsLayer? = null
    public var cqVectorExtentLayer: GraphicsLayer? = null
    private var extentMap = hashMapOf<String, String>()

    fun getExtentString(areaCode: String): String {
        if (extentMap.containsKey(areaCode)) {
            return extentMap[areaCode]!!
        } else {
            return ""
        }
    }

    fun drawCQExtent(mapView: MapView, context: Context) {
        try {
            val cqJson = context.assets!!.fileAsString("json", "cqextent.json").replace("\r", "").replace("\n", "")
            MapExtentUtil.cqImageExtentLayer = GraphicsLayer()
            MapExtentUtil.cqVectorExtentLayer = GraphicsLayer()
            val jsonObj = JSONObject(ZXStringUtil.replaceBlank(cqJson))
            val jsonArr = jsonObj.getJSONArray("features")
            if (jsonArr.length() > 0) {
                for (i in 0 until jsonArr.length()) {
                    try {
                        val featureJson = jsonArr.getJSONObject(i)
                        val geometryJson = featureJson.getJSONObject("geometry")
                        val jsonFactory = JsonFactory()
                        val jsonParser = jsonFactory.createJsonParser(geometryJson.toString())
                        val geometry = GeometryEngine.jsonToGeometry(jsonParser)
                        val graphicImage = Graphic(geometry.geometry, SimpleFillSymbol(Color.TRANSPARENT, SimpleFillSymbol.STYLE.SOLID).apply {
                            outline.width = 2.0f
                            outline.color = Color.WHITE
                        })
                        val graphicVector = Graphic(geometry.geometry, SimpleFillSymbol(Color.TRANSPARENT, SimpleFillSymbol.STYLE.SOLID).apply {
                            outline.width = 2.0f
                            outline.color = Color.GRAY
                        })
                        MapExtentUtil.cqImageExtentLayer!!.addGraphic(graphicImage)
                        MapExtentUtil.cqVectorExtentLayer!!.addGraphic(graphicVector)
                    } catch (e: Exception) {
                        continue
                    }
                }
            }
            mapView.addLayer(MapExtentUtil.cqImageExtentLayer)
            mapView.addLayer(MapExtentUtil.cqVectorExtentLayer)
            MapExtentUtil.cqImageExtentLayer!!.isVisible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun drawToMap(mapView: MapView, rainJson: String, areaCode: String) {
        try {
            MapExtentUtil.extertLayer = GraphicsLayer()
            val jsonObj = JSONObject(ZXStringUtil.replaceBlank(rainJson))
            val jsonArr = jsonObj.getJSONArray("features")
            if (jsonArr.length() > 0) {
                for (i in 0 until jsonArr.length()) {
                    try {
                        val featureJson = jsonArr.getJSONObject(i)
                        val geometryJson = featureJson.getJSONObject("geometry")
                        val jsonFactory = JsonFactory()
                        val jsonParser = jsonFactory.createJsonParser(geometryJson.toString())
                        val geometry = GeometryEngine.jsonToGeometry(jsonParser)
                        val graphic = Graphic(geometry.geometry, SimpleFillSymbol(Color.TRANSPARENT, SimpleFillSymbol.STYLE.SOLID).apply {
                            outline.width = 2.0f
                            outline.color = Color.WHITE
                        })
                        MapExtentUtil.extertLayer!!.addGraphic(graphic)
                        val envelope = Envelope()
                        graphic.geometry.queryEnvelope(envelope)
                        mapView.setExtent(envelope, 200, true)
                    } catch (e: Exception) {
                        continue
                    }
                }
            }
            mapView.addLayer(MapExtentUtil.extertLayer)
            if (!extentMap.containsKey(areaCode)) {
                extentMap.put(areaCode, rainJson)
            }
        } catch (e: Exception) {
        }
    }

    private fun AssetManager.fileAsString(subdirectory: String, filename: String): String {
        return open("$subdirectory/$filename").use {
            it.readBytes().toString(Charset.defaultCharset())
        }
    }
}
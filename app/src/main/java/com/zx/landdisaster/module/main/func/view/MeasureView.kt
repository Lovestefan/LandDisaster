package com.zx.landdisaster.module.main.func.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.MapOnTouchListener
import com.esri.android.map.MapView
import com.esri.core.geometry.*
import com.esri.core.map.Graphic
import com.esri.core.symbol.SimpleFillSymbol
import com.esri.core.symbol.SimpleLineSymbol
import com.esri.core.symbol.SimpleMarkerSymbol
import com.zx.landdisaster.R
import com.zx.zxutils.util.ZXDialogUtil
import kotlinx.android.synthetic.main.layout_measure_view.view.*
import java.text.DecimalFormat
import java.util.*
import javax.annotation.Nullable

/**
 * Created by Xiangb on 2019/6/10.
 * 功能：
 */
class MeasureView @JvmOverloads constructor(context: Context, @Nullable attrs: AttributeSet? = null, def: Int = 0) : LinearLayout(context, attrs, def) {

    private var mapView: MapView? = null
    private var meaMapTouchListener: MeaMapTouchListener? = null
    private var lineSymbol: SimpleLineSymbol? = null
    private var markerSymbol: SimpleMarkerSymbol? = null
    private var fillSymbol: SimpleFillSymbol? = null
    var mIsShowMeasure = false
    private var size: Double = 0.toDouble()
    private var drawLayer: GraphicsLayer
    private var ptStart: Point? = null// 起点
    private var ptPrevious: Point? = null
    private var tempPolygon: Polygon? = null// 记录绘制过程中的多边形
    private val mmPoints = ArrayList<Point>()
    private val mmGraId = ArrayList<Int>()
    private val mmGraphic = ArrayList<Graphic>()
    private val mmLines = ArrayList<Line>()
    private var mmNum = -1
    private var mmCount = 0
    private var geoType: Geometry.Type? = null// 用于判定当前选择的几何图形类型
    private val pastGeometry: Geometry? = null
    var defaultListener: MapOnTouchListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_measure_view, this, true)
        drawLayer = GraphicsLayer()
        initSymbols()
        rgMap_measure.setOnTouchListener { v, event -> true }
        //测量切换
        rgMap_measure.setOnCheckedChangeListener { group, checkedId ->
            drawLayer.removeAll()
            if (meaMapTouchListener == null) {
                return@setOnCheckedChangeListener
            }
            when (checkedId) {
                R.id.rbMap_length -> {
                    meaMapTouchListener!!.setType("Polyline")
                    clearGraphic()
//					calValue.setText("0");
//					calValueUnit.setText("米");
                    rbMap_length.setTextColor(context.resources.getColor(R.color.colorPrimary))
                    rbMap_area.setTextColor(context.resources.getColor(R.color.white))
                }
                R.id.rbMap_area -> {
                    meaMapTouchListener!!.setType("Polygon")
                    clearGraphic()
//					calValue.setText("0");
//					calValueUnit.setText("平方米");
                    rbMap_length.setTextColor(context.resources.getColor(R.color.white))
                    rbMap_area.setTextColor(context.resources.getColor(R.color.colorPrimary))
                }
            }
        }
        //上一步
        ivMap_undo.setOnClickListener {
            if (geoType == Geometry.Type.POLYLINE) {//线的情况
                if (mmNum == -1) {
                    mmNum = mmGraId.size
                }
                if (mmNum == 1) {//移除最后一个点
                    drawLayer.removeGraphic(mmGraId[0])
                    ivMap_undo.setBackgroundResource(R.drawable.goback_off)
                    ptPrevious = mmPoints[0]
                    size = 0.0
                    mmNum = 0
                } else if (mmNum > 1) {//移除一个点和一根线
                    // 移除最后两个Graphic
                    drawLayer.removeGraphic(mmGraId[mmNum - 1])
                    drawLayer.removeGraphic(mmGraId[mmNum - 2])
                    // 根据末位点的两次变化计算减少的长度
                    val line = Line()
                    line.setStart(mmPoints[(mmNum - 3) / 2])
                    line.setEnd(mmPoints[(mmNum - 1) / 2])
                    val polyline = Polyline()
                    polyline.addSegment(line, true)
                    // 计算返回一步所减少的距离并更新
                    val length = GeometryEngine.geodesicLength(polyline, mapView!!.getSpatialReference(), null)
                    size -= length
                    ptPrevious = mmPoints[(mmNum - 3) / 2]
                    ivMap_redo.setBackgroundResource(R.drawable.recover_on)
                    mmNum -= 2
                }
                //					if (size<1000){
                //						calValue.setText(formatDouble(size));
                //						calValueUnit.setText("米");
                //					}else{
                //						calValue.setText(formatDouble(size/1000));
                //						calValueUnit.setText("公里");
                //					}
            } else if (geoType == Geometry.Type.POLYGON) {//面的情况
                if (mmCount == 1) {//移除一个点
                    mmCount = 0
                    ivMap_undo.setBackgroundResource(R.drawable.goback_off)
                    drawLayer.removeAll()
                } else if (mmCount != 1 && mmCount != 0 && mmCount != -1) {//移除最后的Polygon
                    drawLayer.removeAll()
                    tempPolygon!!.removePoint((mmCount - 1) / 2)
                    val graphic = Graphic(tempPolygon, fillSymbol)
                    drawLayer.addGraphic(graphic)
                    mmCount -= 2
                    if (mmCount == 1) {//当只剩最后一个Graphic时，在起始位置画一个点
                        tempPolygon!!.getPoint(0)
                        val graphic2 = Graphic(tempPolygon, markerSymbol)
                        drawLayer.addGraphic(graphic2)
                        size = 0.0
                    }
                    val area = GeometryEngine.geodesicArea(tempPolygon, mapView!!.getSpatialReference(), null)
                    size = Math.abs(area)
                    ivMap_redo.setBackgroundResource(R.drawable.recover_on)
                }
                //					if (size<1000000){
                //						calValue.setText(formatDouble(size));
                //						calValueUnit.setText("平方米");
                //					}else{
                //						calValue.setText(formatDouble(size/1000000));
                //						calValueUnit.setText("平方公里");
                //					}
            }
        }
        //下一步
        ivMap_redo.setOnClickListener {

            if (geoType == Geometry.Type.POLYLINE) {//线的情况
                if (mmNum == mmGraId.size || mmNum == -1) {
                    ivMap_redo.setBackgroundResource(R.drawable.recover_off)
                    return@setOnClickListener
                } else if (mmNum == 0) {
                    mmGraId.removeAt(mmNum)
                    mmGraId.add(mmNum, drawLayer.addGraphic(mmGraphic[mmNum]))
                    ptPrevious = mmPoints[0]
                    ivMap_undo.setBackgroundResource(R.drawable.goback_on)
                    mmNum = 1
                } else {
                    mmGraId.removeAt(mmNum)
                    mmGraId.add(mmNum, drawLayer.addGraphic(mmGraphic[mmNum]))
                    mmGraId.removeAt(mmNum + 1)
                    mmGraId.add(mmNum + 1, drawLayer.addGraphic(mmGraphic[mmNum + 1]))
                    // 根据末位点的两次变化计算增加的长度
                    val line = Line()
                    line.setStart(mmPoints[(mmNum + 1) / 2 - 1])
                    line.setEnd(mmPoints[(mmNum + 1) / 2])
                    val polyline = Polyline()
                    polyline.addSegment(line, true)
                    // 计算返回一步所增加的距离并更新
                    val length = GeometryEngine.geodesicLength(polyline, mapView!!.getSpatialReference(), null)
                    size += length
                    ptPrevious = mmPoints[(mmNum + 1) / 2]
                    mmNum += 2
                    if (mmNum == mmGraId.size) {
                        ivMap_redo.setBackgroundResource(R.drawable.recover_off)
                    }
                }
                //					if (size<1000){
                //						calValue.setText(formatDouble(size));
                //						calValueUnit.setText("米");
                //					}else{
                //						calValue.setText(formatDouble(size/1000));
                //						calValueUnit.setText("公里");
                //					}
            } else if (geoType == Geometry.Type.POLYGON) {//面的情况
                if (tempPolygon == null) {

                } else if (mmCount == 0) {
                    tempPolygon!!.getPoint(0)
                    val graphic2 = Graphic(tempPolygon, markerSymbol)
                    drawLayer.addGraphic(graphic2)
                    size = 0.0
                    mmCount = 1
                } else if ((mmCount - 1) / 2 != mmLines.size) {
                    ivMap_undo.setBackgroundResource(R.drawable.goback_on)
                    drawLayer.removeAll()
                    tempPolygon!!.addSegment(mmLines[(mmCount - 1) / 2], false)
                    val graphic = Graphic(tempPolygon, fillSymbol)
                    drawLayer.addGraphic(graphic)
                    mmCount += 2
                    if ((mmCount - 1) / 2 == mmLines.size) {
                        ivMap_redo.setBackgroundResource(R.drawable.recover_off)
                    }
                    val area = GeometryEngine.geodesicArea(tempPolygon, mapView!!.getSpatialReference(), null)
                    size = Math.abs(area)
                }
                //					if (size<1000000){
                //						calValue.setText(formatDouble(size));
                //						calValueUnit.setText("平方米");
                //					}else{
                //						calValue.setText(formatDouble(size/1000000));
                //						calValueUnit.setText("平方公里");
                //					}
            }
        }
        //计算
        tvaMap_cal.setOnClickListener {
            val title = if (meaMapTouchListener!!.getType() == Geometry.Type.POLYLINE) {
                "长度"
            } else if (meaMapTouchListener!!.getType() == Geometry.Type.POLYGON) {
                "面积"
            } else {
                ""
            }
            val msg = if (meaMapTouchListener!!.getType() == Geometry.Type.POLYLINE) {
                "${formatDouble(size)} 米\n${formatDouble(size / 1000)} 公里"
            } else if (meaMapTouchListener!!.getType() == Geometry.Type.POLYGON) {
                "${formatDouble(size)} 平方米\n${formatDouble(size / 666.67)} 亩"
            } else {
                ""
            }
            ZXDialogUtil.showInfoDialog(context, title, msg)
        }
        //退出
        tvaMap_exit.setOnClickListener {
            closeMeasure()
//            clearGraphic()
//            meaMapTouchListener!!.setType(null)
//            rgMap_measure.setVisibility(View.GONE)
//            mIsShowMeasure = false
////				calValue.setText("0");
////				calValueUnit.setText("米");
//            rbMap_length.setTextColor(context.getResources().getColor(R.color.colorPrimary))
//            rbMap_area.setTextColor(context.getResources().getColor(R.color.white))
//            mapView!!.setOnTouchListener(defaultListener)
        }
        //删除
        ivMap_delete.setOnClickListener {
            clearGraphic()
//				if (mapTouchListener.getType()==Geometry.Type.POLYLINE){
//					calValue.setText("0");
//					calValueUnit.setText("米");
//				}else if (mapTouchListener.getType()== Geometry.Type.POLYGON){
//					calValue.setText("0");
//					calValueUnit.setText("平方米");
//				}
            meaMapTouchListener!!.setGeometryType(meaMapTouchListener!!.getType())
        }
    }

    private fun formatDouble(value: Double): String {
        val df = DecimalFormat("######0.00")
        return df.format(value)
    }

    // 初始化标志
    private fun initSymbols() {
        lineSymbol = SimpleLineSymbol(ContextCompat.getColor(context, R.color.red_color_normal), 1f, SimpleLineSymbol.STYLE.SOLID)
        lineSymbol!!.setAlpha(180)
        markerSymbol = SimpleMarkerSymbol(ContextCompat.getColor(context, R.color.colorPrimary), 8, SimpleMarkerSymbol.STYLE.CIRCLE)
        fillSymbol = SimpleFillSymbol(ContextCompat.getColor(context, R.color.red_color_normal), SimpleFillSymbol.STYLE.SOLID)
        fillSymbol!!.setAlpha(40)
    }

    // 初始化各个数据
    private fun clearGraphic() {
        ivMap_redo.setBackgroundResource(R.drawable.recover_off)
        ivMap_undo.setBackgroundResource(R.drawable.goback_off)
        drawLayer.removeAll()
        drawLayer = GraphicsLayer()
        ivMap_delete.setAlpha(160)
        mmNum = -1
        mmGraId.clear()
        mmGraphic.clear()
        mmPoints.clear()
        mmLines.clear()
        ptStart = null
        ptPrevious = null
        size = 0.0
        mmCount = 0
    }

    fun showMeasure(mapView: MapView) {
        this.mapView = mapView
        meaMapTouchListener = MeaMapTouchListener(context, mapView)
        mapView.setOnTouchListener(meaMapTouchListener)

        if (pastGeometry != null) {
            mapView.getCallout().hide()
        }
        clearGraphic()
        mIsShowMeasure = true
        rgMap_measure.visibility = View.VISIBLE
        meaMapTouchListener!!.setType("Polyline")
        rgMap_measure.check(R.id.rbMap_length)

        rbMap_length.setTextColor(context.getResources().getColor(R.color.colorPrimary))
        rbMap_area.setTextColor(context.getResources().getColor(R.color.white))
    }

    fun closeMeasure() {
        if (mapView == null) {
            return
        }
        if (pastGeometry != null) {
            mapView!!.getCallout().hide()
        }
        clearGraphic()
        //		calValue.setText("0");
        //		calValueUnit.setText("米");
        mIsShowMeasure = false
        rgMap_measure.visibility = View.GONE
        meaMapTouchListener!!.setType(null)
        rbMap_length.setTextColor(context.getResources().getColor(R.color.colorPrimary))
        rbMap_area.setTextColor(context.getResources().getColor(R.color.white))
//        if (defaultListener != null) {
            mapView!!.setOnTouchListener(defaultListener)
//        }
//        mapView!!.setOnTouchListener(null)
        //		mActivity.showDrawerLayout();
    }

    inner class MeaMapTouchListener(context: Context, val mapView: MapView) : MapOnTouchListener(context, mapView) {
        private var points = arrayListOf<Point>()

        // 根据用户选择设置当前绘制的几何图形类型
        fun setType(geometryType: String?) {
            size = 0.0
            ptStart = null
            tempPolygon = null
            if (geometryType == null) {
                geoType = null
                return
            }
            if (geometryType.equals("Point", ignoreCase = true))
                geoType = Geometry.Type.POINT
            else if (geometryType.equals("Polyline", ignoreCase = true))
                geoType = Geometry.Type.POLYLINE
            else if (geometryType.equals("Polygon", ignoreCase = true))
                geoType = Geometry.Type.POLYGON
        }

        // 根据用户选择设置当前绘制的几何图形类型
        fun setGeometryType(type: Geometry.Type) {
            geoType = type
            size = 0.0
            ptStart = null
            tempPolygon = null
        }

        fun getType(): Geometry.Type {
            return geoType!!
        }

        override fun onSingleTap(point: MotionEvent): Boolean {
            //首先判断点击时是不是初次点击（即不是由于返回上一步到初始）
            if (geoType == Geometry.Type.POLYLINE && mmNum == 0) {
                ivMap_undo.setBackgroundResource(R.drawable.goback_off)
                ivMap_redo.setBackgroundResource(R.drawable.recover_off)
                ivMap_delete.setAlpha(160)
                clearGraphic()
            }
            if (geoType == Geometry.Type.POLYGON && mmCount == 0) {
                ivMap_undo.setBackgroundResource(R.drawable.goback_off)
                ivMap_redo.setBackgroundResource(R.drawable.recover_off)
                drawLayer.removeAll()
                tempPolygon = null
                mmLines.clear()
                mmCount = 0
                ptStart = null
                ivMap_delete.setAlpha(160)
            }

            ivMap_undo.setBackgroundResource(R.drawable.goback_on)
            if (geoType == null) {
                // 如果目前是空，再判断目前是不是动态图层
                //				setIdentify(point);
                return false
            }
            val ptCurrent = mapView.toMapPoint(Point(point.x.toDouble(), point.y.toDouble()))// 获得单击点坐标
            mmPoints.add(ptCurrent)
            if (ptStart == null) {// 如果起点为空
                mapView.addLayer(drawLayer)
                size = 0.0
                drawLayer.removeAll()
            }

            if (geoType == Geometry.Type.POINT) {
                drawLayer.removeAll()
                ptStart = ptCurrent
                val graphic = Graphic(ptStart, markerSymbol)
                addGraphic(graphic)
                return true
            } else {
                points.add(ptCurrent)
                if (ptStart == null) {
                    ptStart = ptCurrent
                    val graphic = Graphic(ptStart, markerSymbol)
                    addGraphic(graphic)
                } else {
                    val graphic = Graphic(ptCurrent, markerSymbol)
                    addGraphic(graphic)
                    val line = Line()
                    line.setStart(ptPrevious)
                    line.setEnd(ptCurrent)
                    // 以下为根据线或面添加不同操作
                    if (geoType == Geometry.Type.POLYLINE) {// 计算长度
                        val polyline = Polyline()
                        polyline.addSegment(line, true)
                        val g = Graphic(polyline, lineSymbol)
                        addGraphic(g)
                        val length = GeometryEngine.geodesicLength(polyline, mapView.getSpatialReference(), null)
                        size += Math.abs(length)
                        //						if (size<1000){
                        //							calValue.setText(formatDouble(size));
                        //							calValueUnit.setText("米");
                        //						}else{
                        //							calValue.setText(formatDouble(size/1000));
                        //							calValueUnit.setText("公里");
                        //						}
                    } else {// 计算面积
                        if (tempPolygon == null)
                            tempPolygon = Polygon()
                        mmLines.add(line)
                        tempPolygon!!.addSegment(line, false)
                        drawLayer.removeAll()
                        val g = Graphic(tempPolygon, fillSymbol)
                        addGraphic(g)
                        val area = GeometryEngine.geodesicArea(tempPolygon, mapView.getSpatialReference(), null)
                        size = Math.abs(area)
                        //						if (size<1000000){
                        //							calValue.setText(formatDouble(size));
                        //							calValueUnit.setText("平方米");
                        //						}else{
                        //							calValue.setText(formatDouble(size/1000000));
                        //							calValueUnit.setText("平方公里");
                        //						}
                    }
                }
                ptPrevious = ptCurrent
                return true
            }
        }

        override fun onDoubleTap(point: MotionEvent): Boolean {
            return super.onDoubleTap(point)
        }

        // 添加Graphic图形
        fun addGraphic(graphic: Graphic) {
            if (geoType == Geometry.Type.POLYLINE) {
                mmGraId.add(drawLayer.addGraphic(graphic))
                mmGraphic.add(graphic)
                // 如果不是最后，即如果添加的时候没有回删的操作，就不进行操作
                // 并且mmnum不是-1，并且添加点的时候不进行，添加线才进行
                if (mmNum != mmGraId.size && mmNum != -1 && mmGraId.size % 2 == 1) {
                    val times = mmGraId.size - mmNum - 2
                    for (i in 0 until times) {
                        mmGraId.removeAt(mmGraId.size - 3)
                        mmGraphic.removeAt(mmGraphic.size - 3)
                    }
                    for (i in 0 until times / 2) {
                        mmPoints.removeAt(mmPoints.size - 2)
                    }
                    mmNum += 2
                    ivMap_redo.setBackgroundResource(R.drawable.recover_off)
                }
            } else if (geoType == Geometry.Type.POLYGON) {
                mmCount++
                if (mmCount % 2 == 1) {
                    drawLayer.addGraphic(graphic)
                }
                if ((mmCount - 1) / 2 != mmLines.size) {
                    for (i in 0 until mmLines.size - (mmCount - 1) / 2) {
                        mmLines.removeAt(mmLines.size - 1)
                    }
                    ivMap_redo.setBackgroundResource(R.drawable.recover_off)
                }
            }
            ivMap_delete.setAlpha(255)
        }
    }
}
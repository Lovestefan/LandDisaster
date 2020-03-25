package com.zx.landdisaster.base.tool

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.zx.landdisaster.R
import com.zx.zxutils.views.BottomSheet.ZXBottomSheet
import java.net.URISyntaxException
import java.util.*

/**
 * Created by Xiangb on 2019/6/10.
 * 功能：
 */
object OpenNaviUtil {

    fun startNavi(context: Activity, address: String, longitude: Double, latitude: Double) {
        ZXBottomSheet.initGrid(context)
                .addItem("高德导航", ContextCompat.getDrawable(context, R.drawable.navi_gaode))
                .addItem("百度导航", ContextCompat.getDrawable(context, R.drawable.navi_baidu))
                .addItem("腾讯导航", ContextCompat.getDrawable(context, R.drawable.navi_tengxun))
                .showCloseView(false)
                .showCheckMark(false)
                .setOnItemClickListener { _, i ->
                    var intent: Intent? = null
                    when (i) {
                        0//高德导航
                        -> if (isAvilible(context, "com.autonavi.minimap")) {
                            try {
                                var gcj02 = CoordinateConversion.wgs84togcj02(longitude, latitude).split(",")
                                intent = Intent.getIntent("amapuri://route/plan/?" +
                                        "sourceApplication=安全调度" +
                                        "&dname=" + address + "" +
                                        "&dlat=" + gcj02[0] + "&dlon=" + gcj02[1] + "" +
                                        "&dev=0")
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }

                        } else {
                            Toast.makeText(context, "您尚未安装高德地图", Toast.LENGTH_LONG).show()
                            val uri = Uri.parse("market://details?id=com.autonavi.minimap")
                            intent = Intent(Intent.ACTION_VIEW, uri)
                        }
                        1//百度导航
                        -> if (isAvilible(context, "com.baidu.BaiduMap")) {//传入指定应用包名
                            try {
                                //坐标转换
                                var bd09 = CoordinateConversion.wgs84tobd09(longitude, latitude)
                                //                          intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving®ion=西安&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                                intent = Intent.getIntent("intent://map/direction?" +
                                        //"origin=latlng:"+"34.264642646862,108.95108518068&" +   //起点  此处不传值默认选择当前位置
                                        "destination=latlng:" + bd09 + "|name:" + address +        //终点

                                        "&mode=driving&" +          //导航路线方式

                                        "&src=安全调度#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end")
                            } catch (e: URISyntaxException) {
                                Log.e("intent", e.message)
                            }

                        } else {//未安装
                            //market为路径，id为包名
                            //显示手机上所有的market商店
                            Toast.makeText(context, "您尚未安装百度地图", Toast.LENGTH_LONG).show()
                            val uri = Uri.parse("market://details?id=com.baidu.BaiduMap")
                            intent = Intent(Intent.ACTION_VIEW, uri)
                        }
                        2//腾讯导航
                        -> if (isAvilible(context, "com.tencent.map")) {//传入指定应用包名
                            //double[] gotoLang=AMAPUtils.getInstance().bdToGaoDe(Double.parseDouble(gotoLatitude),Double.parseDouble(gotoLongitude));
                            //gotoLatitude=String.valueOf(gotoLang[0]);gotoLongitude=String.valueOf(gotoLang[1]);

                            var gcj02 = CoordinateConversion.wgs84togcj02(longitude, latitude)
                            val url1 = "qqmap://map/routeplan?" +
                                    "type=drive" +
                                    "&to=" + address + "" +
                                    "&tocoord=" + gcj02 +
                                    "&policy=2&referer=安全调度"
                            intent = Intent("android.intent.action.VIEW", Uri.parse(url1))
                        } else {
                            Toast.makeText(context, "您尚未安装腾讯地图", Toast.LENGTH_LONG).show()
                            val uri = Uri.parse("market://details?id=com.tencent.map")
                            intent = Intent(Intent.ACTION_VIEW, uri)
                        }

                        else -> {
                        }
                    }
                    if (intent != null) {
                        context.startActivity(intent)
                    }
                }
                .build()
                .show()
    }

    /* 检查手机上是否安装了指定的软件
     * @param context
     * @param packageName：应用包名
     * @return
     */
    fun isAvilible(context: Context, packageName: String): Boolean {
        //获取packagemanager
        val packageManager = context.packageManager
        //获取所有已安装程序的包信息
        val packageInfos = packageManager.getInstalledPackages(0)
        //用于存储所有已安装程序的包名
        val packageNames = ArrayList<String>()
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (i in packageInfos.indices) {
                val packName = packageInfos[i].packageName
                packageNames.add(packName)
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName)
    }
}
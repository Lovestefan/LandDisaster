package com.zx.landdisaster.module.main.bean

/**
 * @E-mail：1160942652@qq.com
 * @作者：zhoucan
 * @创建时间：2019.09.10 15:03
 * @类名：分析：雨量站
 */
data class RainStationFromGisBean(
    val areacode: String,
    val areaname: String,
    val gridcode: Int,
    val latitude: Double,
    val longitude: Double,
    val rainfall: Double,
    val sensorname: String,
    val sensornumber: String,
    val townscode: String,
    val townsname: String
)
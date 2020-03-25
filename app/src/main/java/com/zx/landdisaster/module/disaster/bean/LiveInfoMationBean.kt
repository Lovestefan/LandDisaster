package com.zx.landdisaster.module.disaster.bean

/**
 * @E-mail：1160942652@qq.com
 * @作者：zhoucan
 * @创建时间：2019.09.10 14:59
 * @类名：雨量分析
 */
data class LiveInfoMationBean(val disasterNum: String,
                              val maxRainstation: MaxRainstation?,
                              val rainstationNum: String,
                              val townsNum: String
) {
    data class MaxRainstation(
            val areacode: String,
            val areaname: String,
            val gridcode: Int,
            val latitude: String,
            val longitude: String,
            val rainfall: Double,
            val sensorname: String,
            val sensornumber: String,
            val townscode: String,
            val townsname: String
    )

}
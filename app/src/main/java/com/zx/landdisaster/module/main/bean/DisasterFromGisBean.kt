package com.zx.landdisaster.module.main.bean

/**
 * @E-mail：1160942652@qq.com
 * @作者：zhoucan
 * @创建时间：2019.09.10 15:01
 * @类名：分析：隐患点
 */
data class DisasterFromGisBean(
    val address: String,
    val gridcode: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val pkiaa: String,
    val type: String
)
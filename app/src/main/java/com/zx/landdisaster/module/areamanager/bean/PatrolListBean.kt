package com.zx.landdisaster.module.areamanager.bean

import java.io.Serializable


/**
 * Updated by admin123 on 2019-05-05
 */
data class PatrolListBean(var content: String, var createtime: Long, var id: String, var lastmodytime: String,
                          var latitude: Double, var longitude: Double, var patroltime: Long, var phase: Int,
                          var pkiaa: String, var recorder: String): Serializable
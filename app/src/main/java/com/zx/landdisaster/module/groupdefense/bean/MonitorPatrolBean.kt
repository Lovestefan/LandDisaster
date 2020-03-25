package com.zx.landdisaster.module.groupdefense.bean

import java.io.Serializable

/**
 * Updated by FANGYI on 2019-04-26
 */
data class MonitorPatrolBean(var actualdata: Int,
                             var alarmlevel: String,
                             var createtime: Long,
                             var effectivity: String,
                             var lastmodytime: Any,
                             var latitude: Double,
                             var legal: Int,
                             var logid: String,
                             var longitude: Double,
                             var monitortime: Long,
                             var mpid: String,
                             var name: String,
                             var note: String,
                             var pkiaa: String,
                             var pkiaaname: String):Serializable
package com.zx.landdisaster.module.groupdefense.bean

import java.io.Serializable

/**
 * Updated by FANGYI on 2019-04-26
 */
data class MacroMonitroBean(var alarmlevel: String,
                            var createtime: Long,
                            var effectivity: String,
                            var lastmodytime: String,
                            var latitude: Double,
                            var legal: Int,
                            var logid: String,
                            var longitude: Double,
                            var macroappear: String,
                            var note: String,
                            var others: String,
                            var patroltime: Long,
                            var pkiaa: String,
                            var pkiaaname: String,
                            var setid: String) : Serializable
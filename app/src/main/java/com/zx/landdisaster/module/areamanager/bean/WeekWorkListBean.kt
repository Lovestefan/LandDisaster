package com.zx.landdisaster.module.areamanager.bean

import java.io.Serializable

/**
 * Updated by admin123 on 2019-04-19
 */
data class WeekWorkListBean(var areamanager: String, var content: String, var createtime: Long, var lastmodytime: String,
                            var reportid: String, var reporttime: Long, var townsname: String, var recordername: String) : Serializable
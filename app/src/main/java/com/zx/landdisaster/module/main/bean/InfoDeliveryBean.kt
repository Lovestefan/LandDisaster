package com.zx.landdisaster.module.main.bean

import java.io.Serializable

/**
 * Updated by admin123 on 2019-05-23
 */
data class InfoDeliveryBean(var createtime: Long, var lastmodytime: Long, var publishdept: String, var publishtime: Long,
                            var serviceid: String, var servicesketch: String, var servicetitle: String, var servicetype: Int
                            , var readed: Int) : Serializable
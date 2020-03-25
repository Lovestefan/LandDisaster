package com.zx.landdisaster.module.worklog.bean

import java.io.Serializable

/**
 * Updated by dell on 2019-03-25
 */
data class DailyListBean(var areacode: String = "",
                         var latitude: Double,
                         var logid: String = "",
                         var logims: String = "",
                         var logip: String = "",
                         var logperson: String = "",
                         var logtime: Long,
                         var longitude: Double,
                         var pkidd: String = "",
                         var prelogid: String = "",
                         var status: String = "",
                         var timeborder: String = "",
                         var timeborderstatus: String = "",
                         var workcontent: String = "",
                         var personName: String = "",
                         var auditorname: String = "",
                         var areaname: String = "") : Serializable
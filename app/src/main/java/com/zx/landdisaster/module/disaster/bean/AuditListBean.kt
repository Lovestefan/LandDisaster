package com.zx.landdisaster.module.disaster.bean

import java.io.Serializable

/**
 * Updated by dell on 2019-03-20
 */
data class AuditListBean(var auditKind: Int = 0,
                         var auditid: String = "",
                         var areacode: String = "",
                         var disastertype: String = "",
                         var hazardname: String = "",
                         var hazardtype: Int = 0,
                         var pkiaa: String = "",
                         var pkidd: String = "",
                         var scalelevel: String? = "",
                         var flowstatus: Int = 0,
                         var flownum: String = "",
                         var longitude: Double = 0.0,
                         var reportdataid: String = "",
                         var latitude: Double = 0.0,
                         var address: String = "",
                         var auditstatus: Int = 0,
                         var areaname: String = "",
                         var hasreview: String? = "",
                         var happentime: String? = "",
                         var reporttime: String? = "",
                         var audittime: String? = "") : Serializable
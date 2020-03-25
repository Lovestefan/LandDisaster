package com.zx.landdisaster.module.disaster.bean

import java.io.Serializable

/**
 * Updated by dell on 2019-03-22
 */
data class ReportListBean(var areacode: String,
                          var disastertype: String,
                          var hazardname: String,
                          var hazardtype: Int,
                          var pkiaa: String,
                          var pkidd: String,
                          var scalelevel: String,
                          var flowstatus: Int,
                          var flownum: String,
                          var longitude: Any,
                          var reportdataid: String,
                          var latitude: Any,
                          var address: String,
                          var auditstatus: Int,
                          var reportor: String,
                          var reportorname: String,
                          var currentstep: Int,
                          var happentime: String?,
                          var areaname: String,
                          var reporttime: Long,
                          var currentstepDes: String,
                          var editAble: Boolean,
                          var editAllAble: Boolean) : Serializable{
    fun getAuditStatusString(): String = when (auditstatus) {
        1 -> "通过"
        -1 -> "退回"
        0 -> "待审核"
        -2 -> "撤销"
        -3 -> "终止"
        else -> ""
    }
    fun getHazardTypeString(): String = when (hazardtype) {
        1 -> "灾情"
        2 -> "险情"
        else -> ""
    }
}
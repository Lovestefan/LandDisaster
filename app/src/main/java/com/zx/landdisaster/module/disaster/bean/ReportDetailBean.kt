package com.zx.landdisaster.module.disaster.bean

import java.io.Serializable

/**
 * Updated by dell on 2019-03-21
 */
data class ReportDetailBean(var auditKind: Int = 0, var areaname: String = "", var elPerson: Boolean = false, var reportflow: Reportflow?, var auditDetail: AuditDetail?, var reportdata: Reportdata?, var leaderreview: Leaderreview?) : Serializable {
    data class Reportflow(var auditstatus: String? = "",
                          var currentstep: Int? = 0,
                          var flownum: String? = "",
                          var flowstatus: Int? = 0,
                          var reportdataid: String? = "",
                          var reportor: String? = "",
                          var reportorname: String? = "",
                          var reporttime: Long? = 0,
                          var stepprocess: String? = "",
                          var totalstep: Int? = 0) : Serializable

    data class AuditDetail(var areacode: String? = "",
                           var auditid: String? = "",
                           var auditimei: String? = "",
                           var auditip: String? = "",
                           var auditopinion: String? = "",
                           var auditor: String? = "",
                           var auditorname: String? = "",
                           var auditorphone: String? = "",
                           var audittime: String? = "",
                           var flownum: String? = "",
                           var flowstep: Int? = 0,
                           var reportdataid: String? = "",
                           var status: Int? = 0) : Serializable

    data class Leaderreview(var auditid: String? = "",
                            var flownum: String? = "",
                            var hasreview: String? = "",
                            val reviewopinion: String? = "") : Serializable

    data class Reportdata(var address: String? = "",
                          var auditstatus: String? = "",
                          var currentstepDes: String? = "",
                          var reportor: String? = "",
                          var reportorname: String? = "",
                          var reporttime: String? = "",
                          var editAllAble: Boolean = true,
                          var editAble: Boolean = true,
                          var auditKind: String = "",
                          var auditid: String = "",
                          var elPerson: Boolean = false,

                          var areacode: String? = "",
                          var areaname: String? = "",
                          var cause: String? = "",
                          var causenote: String? = "",
                          var dealidea: String? = "",
                          var nextjobplan: String? = "",
                          var dienum: String? = "",
                          var disasternote: String? = "",
                          var disastertype: String? = "",
                          var disvolume: String? = "",
                          var economicloss: String? = "",
                          var flownum: String? = "",
                          var happentime: String? = "",
                          var hazardname: String? = "",
                          var hazardtype: String? = "",
                          var injuredperson: String? = "",
                          var isarea: String? = "",
                          var latitude: String? = "",
                          var leavenum: String? = "",
                          var longitude: String? = "",
                          var missingnum: String? = "",
                          var pkiaa: String? = "",
                          var pkiaaType: Int? = 1,
                          var pkidd: String? = "",
                          var reportdataid: String? = "",
                          var reportdept: String? = "",
                          var reportdeptDesc: String? = "",
                          var reportnote: String? = "",
                          var reportorimei: String? = "",
                          var reportorip: String? = "",
                          var reportorphone: String? = "",
                          var scalelevel: String? = "",
                          var threatenobj: String? = "",
                          var threatobject: String? = "",
                          var threatnum: String? = "",
                          var files: ArrayList<AddFileBean> = arrayListOf()) : Serializable {
        fun getScaleLevelString(): String = when (scalelevel) {
            "A" -> "特大型"
            "B" -> "大型"
            "C" -> "小型"
            "D" -> "中型"
            else -> ""
        }

        fun getThreatObject(): String? {
            return if (threatenobj.isNullOrEmpty()) {
                threatobject
            } else {
                threatenobj
            }
        }
    }
}
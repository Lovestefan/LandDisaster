package com.zx.landdisaster.module.worklog.bean

import java.io.Serializable

/**
 * Created by admin123 on 2019-05-15
 */
data class DailyAuditListBean(var auditid: String, var auditimei: String, var auditip: String, var auditopinion: String,
                              var auditor: String, var auditorname: String, var auditorphone: String, var audittime: String,
                              var logid: String, var status: Int, var logperson: String, var logpersonname: String,
                              var workcontent: String, var logtime: Long, var areacode: String, var areaname: String,
                              var startTime: String, var endTime: String): Serializable
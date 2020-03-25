package com.zx.landdisaster.module.disaster.bean

/**
 * Updated by dell on 2019-03-22
 */
data class AuditLogsBean(var logtype: Int,
                         var reporttime: String,
                         var aduittime: String,
                         var reportdept: String,
                         var reportdeptDesc: String,
                         var aduitdept: String,
                         var reportor: String,
                         var aduitor: String,
                         var reportip: String,
                         var aduitip: String,
                         var reportphone: String?,
                         var auditphone: String?,
                         var reportimei: String?,
                         var auditimei: String?,
                         var result: String,
                         var auditopinion: String?
                         )
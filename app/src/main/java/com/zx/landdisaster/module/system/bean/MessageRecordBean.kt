package com.zx.landdisaster.module.system.bean

data class MessageRecordBean(
        val areaCode: String,
        val areacode: String,
        val areapandects: List<PersonBean>,
        val code: String,
        val content: String,
        val id: String,
        val identity: String,
        val identityDesc: String,
        val noticeType: String,
        val ok: String,
        val personbases: List<PersonBean>,
        val personid: String,
        val time: String,
        val reportdataid: String,
        val auditid: String,
        val flownum: String,
        val auditKind: Int,
        val title: String
) {
    data class PersonBean(
            val name: String
    )
}
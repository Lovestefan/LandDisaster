package com.zx.landdisaster.module.main.bean

/**
 * Updated by admin123 on 2019-05-27
 */
data class InfoReleaseBean(var type: String, var content: String?, var reportdata: ReportData?)

data class ReportData(
        val title: String,
        val areas: List<Area>,
        val contents: List<String>
)

data class Area(
        val area: String,
        val areamanager: String,
        val garrison: String,
        val groupdefense: String,
        val ringstand: String
)
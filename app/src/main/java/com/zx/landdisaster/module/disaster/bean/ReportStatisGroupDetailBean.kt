package com.zx.landdisaster.module.disaster.bean

data class ReportStatisGroupDetailBean(
        val areacode: String,
        val areaname: String,
        val disasternum: String,
        val endtime: String,
        val inlegalnum: String,
        val legalRate: String,
        val legalnum: String,
        val macroReportRate: String,
        val macroneednum: String,
        val macrorealnum: String,
        val monitornum: String,
        val noMacrorealnum: String,
        val noRationnum: String,
        val orgname: String,
        val rationReportRate: String,
        val rationneednum: String,
        val rationrealnum: String,
        val snapid: String,
        val snapsource: String,
        val starttime: String,
        val town: String
)
package com.zx.landdisaster.module.disaster.bean

data class ReportstatisBean(
        val areacode: String="",
        val areaname: String="",
        val streetcode: String="",
        val streetname: String="",
        val needreport: String="",
        val reportcount: String="",
        val noreport: String="",
        val reportRate: String=""
)
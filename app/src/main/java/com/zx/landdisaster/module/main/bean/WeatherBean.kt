package com.zx.landdisaster.module.main.bean

data class WeatherBean(
        val days: List<Day>,
        val hours: List<Hour>,
        val msg: String
)

data class Day(
        val date: String,
        val icon: String,
        val temp1: Int,
        val temp2: Int,
        val weather: String
)

data class Hour(
        val date: String,
        val hour: String,
        val icon: String,
        val temp: Int,
        val weather: String
)




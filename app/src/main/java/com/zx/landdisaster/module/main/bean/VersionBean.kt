package com.zx.landdisaster.module.main.bean

/**
 * Updated by dell on 2019-04-15
 */
data class VersionBean(var path: String,
                       var updateTime: String,
                       var isForce: Boolean = false,
                       var versionName: String?,
                       var version: String?,
                       var content: String)
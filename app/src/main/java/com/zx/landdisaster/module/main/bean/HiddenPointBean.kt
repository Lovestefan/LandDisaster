package com.zx.landdisaster.module.main.bean

/**
 * Updated by dell on 2019-04-02
 */
data class HiddenPointBean(var pkiaa: String,
                           var name: String,
                           var type: String,
                           var address: String,
                           var x: String,
                           var y: String,
                           var elevation: String,
                           var indoorcode: String,
                           var outdoorcode: String,
                           var disastegrad: String,
                           var destroyshouse: Int,
                           var dangerousrank: String,
                           var areacode: String,
                           var longitude: Double,
                           var latitude: Double,
                           var disasterstatus: String,
                           var scalelevel: String?,
                           var disasterstatusDesc: String)
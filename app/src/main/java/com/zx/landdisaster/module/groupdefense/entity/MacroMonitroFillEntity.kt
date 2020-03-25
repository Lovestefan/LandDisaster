package com.zx.landdisaster.module.groupdefense.entity

import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroCurrentTypeBean
import java.io.Serializable

/**
 * Created by YI.FANG on 2019/4/19. 14:10
 * mail:87469669@qq.com
 */


data class MacroMonitroFillEntity(var macropatroldata: Macropatroldata,
                                  var macropatrolphenomenas: List<MacroMonitroCurrentTypeBean>) : Serializable{
    data class Macropatroldata(var setid: String,
                               var note: String,
                               var pkiaa: String,
                               var latitude: Double,
                               var pkiaaname: String,
                               var longitude: Double,
                               var legal: Int,
                               var effectivity: String,
                               var alarmlevel: String,
                               var macroappear: String) : Serializable
}
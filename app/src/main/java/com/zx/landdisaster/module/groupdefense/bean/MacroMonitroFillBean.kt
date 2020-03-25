package com.zx.landdisaster.module.groupdefense.bean

import java.io.Serializable

/**
 * Updated by FANGYI on 2019-04-26
 */
data class MacroMonitroFillBean(var macropatroldata: Macropatroldata,
                                var macropatrolphenomenas: List<Macropatrolphenomenas>) :Serializable{
    data class Macropatroldata(var setid: String?,
                               var note: String?,
                               var pkiaa: String?,
                               var latitude: Double?,
                               var pkiaaname: String?,
                               var longitude: Double?,
                               var legal: Int?,
                               var effectivity: String?,
                               var alarmlevel: String?,
                               var macroappear: String?):Serializable

    data class Macropatrolphenomenas(var exist: Int,
                                     var typeid: String):Serializable
}


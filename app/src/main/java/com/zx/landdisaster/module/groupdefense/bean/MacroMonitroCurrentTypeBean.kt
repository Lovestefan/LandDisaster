package com.zx.landdisaster.module.groupdefense.bean

import com.zx.landdisaster.module.disaster.bean.AddFileBean
import java.io.Serializable

/**
 * Updated by FANGYI on 2019-04-26
 */
data class MacroMonitroCurrentTypeBean(var id: String,
                                       var setid: String,
                                       var typeid: String,
                                       var typecode: String,
                                       var desc: String,
                                       var exist: Int,
                                       var fileBean: AddFileBean?) : Serializable


package com.zx.landdisaster.module.groupdefense.bean

import com.zx.landdisaster.module.disaster.bean.AddFileBean
import java.io.Serializable

/**
 * Updated by FANGYI on 2019-04-19
 */
data class MonitorPatrolFillBean(var mpid: String?,
                                 var note: String?,
                                 var pkiaaname: String?,
                                 var name: String?,
                                 var actualdata: String?,
                                 var monitortime: String?,
                                 var pkiaa: String?,
                                 var latitude: Double?,
                                 var longitude: Double?,
                                 var alarmlevel: String?,
                                 var effectivity: String?,
                                 var legal: Int?,
                                 var files: ArrayList<AddFileBean> = arrayListOf()) : Serializable

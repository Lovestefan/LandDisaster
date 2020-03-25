package com.zx.landdisaster.module.areamanager.bean

import com.zx.landdisaster.module.disaster.bean.AddFileBean
import java.io.Serializable

/**
 * Updated by admin123 on 2019-05-05
 */
data class PatrolDetailBean(var pkiaa: String, var phase: Int, var content: String, var latitude: Double, var longitude: Double,
                            var files: ArrayList<AddFileBean>?) : Serializable
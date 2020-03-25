package com.zx.landdisaster.module.worklog.bean

import com.zx.landdisaster.module.disaster.bean.AddFileBean
import java.io.Serializable

/**
 * Updated by dell on 2019-04-19
 */
data class WorkLogListBean(var content: String,
                           var dairyid: String,
                           var note: String,
                           var onduty: String,
                           var recorder: String,
                           var reporttime: Long,
                           var worktype: String,
                           var files: ArrayList<AddFileBean>?,
                           var recordername: String = "",
                           var type: Int = 0,
                           var position: Int = -1) : Serializable
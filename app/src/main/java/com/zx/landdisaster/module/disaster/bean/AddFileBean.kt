package com.zx.landdisaster.module.disaster.bean

import com.zx.landdisaster.api.ApiConfigModule
import com.zx.zxutils.util.ZXFileUtil
import java.io.Serializable

/**
 * Created by Xiangb on 2019/3/19.
 * 功能：
 */
data class AddFileBean(var name: String, var type: Int, var path: String, var vedioPath: String = "") : Serializable {
    companion object {
        fun reSetType(type: String?, name: String, path: String): AddFileBean {
            val mypath = if (!ZXFileUtil.isFileExists(path)) {
                ApiConfigModule.BASE_IP + ApiConfigModule.URL_FILE + path
            }else{
                path
            }
            if (type.isNullOrEmpty()) {
                val mytype = if (name.endsWith("jpg") || name.endsWith("png")) {
                    1
                } else if (name.endsWith("mp4") || name.endsWith("rmvb") || name.endsWith("3gp") || name.endsWith("avi")) {
                    2
                } else {
                    3
                }

                return AddFileBean(name, mytype, mypath, mypath)
            } else {
                val mytype = if (type == "jpg" || type == "png") {
                    1
                } else if (type == "mp4" || type == "rmvb" || type == "3gp" || type == "avi") {
                    2
                } else {
                    3
                }
                return AddFileBean(name, mytype, mypath, mypath)
            }
        }
    }
}
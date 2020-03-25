package com.zx.landdisaster.module.disaster.func.util

import android.content.Context
import android.widget.TextView
import com.zx.landdisaster.R
import com.zx.zxutils.util.ZXSystemUtil

/**
 * Created by Xiangb on 2019/5/7.
 * 功能：
 */
object TextSizeUtil {
    fun resetText(context: Context, view: TextView) {
        view.textSize = ZXSystemUtil.px2sp(context.resources.getDimension(R.dimen.text_size_normal))
    }
}
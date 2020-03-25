package com.zx.landdisaster.module.disaster.bean

import android.support.annotation.DrawableRes

/**
 * Created by Xiangb on 2019/3/16.
 * 功能：
 */
data class ReportFuncBean(var name: String, @DrawableRes var bg: Int, var num: Int = 0)
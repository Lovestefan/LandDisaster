package com.zx.landdisaster.module.main.func.util

import java.util.*

/**
 * Created by Xiangb on 2019/6/13.
 * 功能：日报提醒时间检测工具
 */
class RemindTimeUtil {

    private var isQueryRemind = false
    private var checkCallBack: () -> Unit = {}

    fun checkTime(t: String, checkCallBack: () -> Unit) {
        this.checkCallBack = checkCallBack
        var loginTime = t
        try {
            if (loginTime.equals("") || loginTime == null)
                loginTime = "16:00"
            if (loginTime.split("-").size == 1) {
                //单个时间 xx:xx
                if (loginTime.split(":").size == 2) {
                    timeBJ(loginTime, loginTime)
//                    timeBJ(loginTime, defTime)
                }
            } else if (loginTime.split(";").size == 1) {
                singleTimes(loginTime)
            } else {
                //多个时间段 xx:xx-xx:xx;xx:xx-xx:xx
                for (index in loginTime.split(";").indices) {
                    singleTimes(loginTime.split(";")[index])
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 16 && !isQueryRemind) {
                this.checkCallBack()
                isQueryRemind = true
            } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) != 16 && isQueryRemind) {
                isQueryRemind = false
            }
        }
    }

    private fun singleTimes(time: String) {
        //单个时间段 xx:xx-xx:xx
        if (time.split("-").size == 2) {
            //多个时间 xx:xx
            var sT = time.split("-")[0]
            var eT = time.split("-")[1]
            if (sT.split(":").size == 2 && eT.split(":").size == 2) {
                timeBJ(sT, eT)
            }
        } else if (time.split("-").size == 1) {
            //单个时间 xx:xx
            if (time.split(":").size == 2) {
                timeBJ(time, time)
//                timeBJ(time, defTime)
            }
        }
    }

    private fun timeBJ(sT: String, eT: String) {
        val startHour = sT.split(":")[0].toInt()
        val startMin = sT.split(":")[1].toInt()
        var entHour = eT.split(":")[0].toInt()
        var endMin = eT.split(":")[1].toInt()

        if (sT == eT) {
            //默认为一小时后
            if (endMin == 0) {
                endMin = 60
            } else {
                entHour++
            }
        }
        var nowHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (entHour == 24 && nowHour == 0) {
            nowHour += 24
        }
        val nowMin: Int = Calendar.getInstance().get(Calendar.MINUTE)

        if (entHour != nowHour && endMin == 0)
            endMin = 60
        else if (entHour > nowHour && endMin < nowMin)
            endMin += 60


        if (nowHour in startHour..entHour && nowMin in startMin..endMin && !isQueryRemind) {
            this.checkCallBack()
            isQueryRemind = true
        } else if (isQueryRemind) {
            isQueryRemind = false
        }
    }

}
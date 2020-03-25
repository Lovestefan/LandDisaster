package com.zx.landdisaster.base.tool

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.zx.zxutils.ZXApp
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

/**
 * Created by Xiangb on 2019/6/18.
 * 功能：
 */
object ImeiCodeTool {
    /**
     * 系统4.0的时候
     * 获取手机IMEI 或者MEID
     *
     * @return 手机IMEI
     */
    @SuppressLint("MissingPermission")
    private fun getImeiOrMeid(ctx: Context): String? {
        val manager = ctx.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
        return manager?.deviceId

    }

    /**
     * 5.0统一使用这个获取IMEI IMEI2 MEID
     *
     * @param ctx
     * @return
     */
    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.M)
    private fun getImeiAndMeid(ctx: Context): Map<*, *> {
        val map = HashMap<String, String>()
        val mTelephonyManager = ctx.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
        var clazz: Class<*>? = null
        var method: Method? = null//(int slotId)

        try {
            clazz = Class.forName("android.os.SystemProperties")
            method = clazz!!.getMethod("get", String::class.java, String::class.java)
            val gsm = method!!.invoke(null, "ril.gsm.imei", "") as String
            val meid = method.invoke(null, "ril.cdma.meid", "") as String
            map["meid"] = meid
            if (!TextUtils.isEmpty(gsm)) {
                //the value of gsm like:xxxxxx,xxxxxx
                val imeiArray = gsm.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (imeiArray != null && imeiArray.size > 0) {
                    map["imei1"] = imeiArray[0]

                    if (imeiArray.size > 1) {
                        map["imei2"] = imeiArray[1]
                    } else {
                        map["imei2"] = mTelephonyManager.getDeviceId(1)
                    }
                } else {
                    map["imei1"] = mTelephonyManager.getDeviceId(0)
                    map["imei2"] = mTelephonyManager.getDeviceId(1)
                }
            } else {
                map["imei1"] = mTelephonyManager.getDeviceId(0)
                map["imei2"] = mTelephonyManager.getDeviceId(1)

            }

        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        return map
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.O)
    fun getIMEIforO(context: Context): Map<*, *> {
        val map = HashMap<String, String>()
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val imei1 = tm.getImei(0)
        val imei2 = tm.getImei(1)
        if (TextUtils.isEmpty(imei1) && TextUtils.isEmpty(imei2)) {

            map["imei1"] = tm.meid //如果CDMA制式手机返回MEID
        } else {
            map["imei1"] = imei1

            map["imei2"] = imei2
        }
        return map
    }


    fun getIMEI(): String {
        var imei = ""
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {  //6.0以下 直接获取
                imei = getImeiOrMeid(ZXApp.getContext()) ?: ""
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { //6.0-8.0系统
                val imeiMaps = getImeiAndMeid(ZXApp.getContext())
                imei = getTransform(imeiMaps)
            } else {
                val imeiMaps = getIMEIforO(ZXApp.getContext())
                imei = getTransform(imeiMaps)
            }
        } catch (e: Exception) {
            imei = ""
        }
        return imei
    }


    private fun getTransform(imeiMaps: Map<*, *>?): String {
        var imei = ""
        if (imeiMaps != null) {
            val imei1 = imeiMaps["imei1"] as String
            if (TextUtils.isEmpty(imei1)) {
                return imei
            }
            val imei2 = imeiMaps["imei2"] as String
            if (imei2 != null) {
                if (imei1.trim { it <= ' ' }.length == 15 && imei2.trim { it <= ' ' }.length == 15) {
                    //如果两个位数都是15。说明都是有效IMEI。根据从大到小排列
                    val i1 = java.lang.Long.parseLong(imei1.trim { it <= ' ' })
                    val i2 = java.lang.Long.parseLong(imei2.trim { it <= ' ' })
                    if (i1 > i2) {
                        imei = imei1
                    } else {
                        imei = imei2
                    }

                } else {  //
                    if (imei1.trim { it <= ' ' }.length == 15) {
                        //如果只有imei1是有效的
                        imei = imei1
                    } else if (imei2.trim { it <= ' ' }.length == 15) {
                        //如果只有imei2是有效的
                        imei = imei2
                    } else {
                        //如果都无效那么都为meid。只取一个就可以
                        imei = imei1
                    }

                }
            } else {
                imei = imei1
            }
        }
        return imei
    }


//    @SuppressLint("MissingPermission")
//    fun getIMEI(): String {
//        try {
//            //实例化TelephonyManager对象
//            val telephonyManager = ZXApp.getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//            //获取IMEI号
//            var imei: String? = telephonyManager.deviceId
//            //在次做个验证，也不是什么时候都能获取到的啊
//            if (imei == null) {
//                imei = ""
//            }
//            return imei
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return ""
//        }
//    }
}
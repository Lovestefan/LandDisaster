package com.zx.landdisaster.base.other

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.frame.zxmvp.baserx.RxManager
import com.zx.zxutils.util.ZXAppUtil
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXLogUtil
import com.zx.zxutils.util.ZXSharedPrefUtil
import org.json.JSONException
import org.json.JSONObject


/**
 * Created by Xiangb on 2019/3/12.
 * 功能：
 */
class MyJPushReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val bundle = intent.extras
            ZXLogUtil.loge("[MyReceiver] onReceive - " + intent.action + ", extras: " + printBundle(bundle));
            ZXLogUtil.loge("[MyReceiver] Registration Id : " + JPushInterface.getRegistrationID(context))

            if (JPushInterface.ACTION_REGISTRATION_ID == intent.action) {//注册成功
                val regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID)
                ZXLogUtil.loge("[MyReceiver] 接收Registration Id : " + regId!!)
                //send the Registration Id to your server...
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED == intent.action) {//自定义消息，没有弹出通知
                ZXLogUtil.loge("[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE)!!)
                //打开弹窗
                val map = hashMapOf<String, String>()
                map[JPushInterface.EXTRA_NOTIFICATION_TITLE] = bundle.getString(JPushInterface.EXTRA_TITLE)
                map[JPushInterface.EXTRA_ALERT] = bundle.getString(JPushInterface.EXTRA_MESSAGE)
                map[JPushInterface.EXTRA_EXTRA] = bundle.getString(JPushInterface.EXTRA_EXTRA) ?: ""
                ZXSharedPrefUtil().putMap("jPushBundle", map)
                RxManager().post("jPush", bundle)

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED == intent.action) {//通知，有弹出通知
                val notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)
                ZXLogUtil.loge("[MyReceiver] 接收到推送下来的通知的ID: $notifactionId")
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED == intent.action) {//当用户点击了通知
                ZXLogUtil.loge("[MyReceiver] 用户点击打开了通知")

                //打开自定义的Activity
//                val newIntent = Intent(context, MainActivity::class.java)
//                newIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//                newIntent.action = Intent.ACTION_MAIN
//                newIntent.putExtra("jpushtype", 1)
//                newIntent.putExtra("msg", bundle.getString(JPushInterface.EXTRA_ALERT))
//                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
//                context.startActivity(newIntent)
                if (!ZXAppUtil.isAppForeground()) {
                    moveTaskToFront(context)
                }
                ZXSharedPrefUtil().putBool("isGetPush", true)
                val map = hashMapOf<String, String>()
                map[JPushInterface.EXTRA_NOTIFICATION_TITLE] = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE)
                map[JPushInterface.EXTRA_ALERT] = bundle.getString(JPushInterface.EXTRA_ALERT)
                map[JPushInterface.EXTRA_EXTRA] = bundle.getString(JPushInterface.EXTRA_EXTRA)
                ZXSharedPrefUtil().putMap("jPushBundle", map)
                RxManager().post("jPush", bundle)
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK == intent.action) {
                ZXLogUtil.loge("[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA)!!)
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE == intent.action) {
                val connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false)
                ZXLogUtil.loge("[MyReceiver]" + intent.action + " connected state change to " + connected)
            } else {
                ZXLogUtil.loge("[MyReceiver] Unhandled intent - " + intent.action!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun moveTaskToFront(context: Context) {
        val mAm = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        //获得当前运行的task
        val taskList = mAm.getRunningTasks(100)
        for (rti in taskList) {
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (rti.topActivity.packageName == context.packageName) {
                mAm.moveTaskToFront(rti.id, 0)
                return
            }
        }
        val shortcutIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        context.startActivity(shortcutIntent)
    }

    // 打印所有的 intent extra 数据
    private fun printBundle(bundle: Bundle): String {
        val sb = StringBuilder()
        for (key in bundle.keySet()) {
            if (key == JPushInterface.EXTRA_NOTIFICATION_ID) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key))
            } else if (key == JPushInterface.EXTRA_CONNECTION_CHANGE) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key))
            } else if (key == JPushInterface.EXTRA_EXTRA) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    ZXLogUtil.loge("This message has no Extra data")
                    continue
                }

                try {
                    val json = JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA))
                    val it = json.keys()

                    while (it.hasNext()) {
                        val myKey = it.next()
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]")
                    }
                } catch (e: JSONException) {
                    ZXLogUtil.loge("Get message extra JSON error!")
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key))
            }
        }
        return sb.toString()
    }

}
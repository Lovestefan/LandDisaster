package com.zx.landdisaster.base.other

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.zx.landdisaster.app.MyApplication
import com.zx.zxutils.util.ZXToastUtil

/**
 * Created by Xiangb on 2019/6/18.
 * 功能：
 */
class RestartService : Service() {

    //关闭应用后多久重新启动
    private var stopDelayed: Long = 1000

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        ZXToastUtil.showToast("服务器切换成功，即将重启应用")
        MyApplication.instance.finishAll()
        Handler().postDelayed({
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            startActivity(launchIntent)
            this@RestartService.stopSelf()
        }, stopDelayed)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
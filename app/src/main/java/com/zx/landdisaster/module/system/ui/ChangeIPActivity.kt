package com.zx.landdisaster.module.system.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.app.MyApplication
import com.zx.landdisaster.base.BaseActivity

import com.zx.landdisaster.module.system.mvp.contract.ChangeIPContract
import com.zx.landdisaster.module.system.mvp.model.ChangeIPModel
import com.zx.landdisaster.module.system.mvp.presenter.ChangeIPPresenter
import com.zx.zxutils.util.ZXSharedPrefUtil
import kotlinx.android.synthetic.main.activity_change_ip.*
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * Create By admin On 2017/7/11
 * 功能：服务器切换
 */
class ChangeIPActivity : BaseActivity<ChangeIPPresenter, ChangeIPModel>(), ChangeIPContract.View {

    private var select = 1//用户选择
    private var nowIP = 1//当前使用
    private var moren1 = ""
    private var moren2 = ""
    private val moren3 = "192.168.11.230"

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, userOpen: Boolean = false) {
            val intent = Intent(activity, ChangeIPActivity::class.java)
            intent.putExtra("userOpen", userOpen)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_change_ip
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val nowIp = ZXSharedPrefUtil().getString("base_ip", ApiConfigModule.ip_1)
        if (ApiConfigModule.ISRELEASE) {
            tvIp3.visibility = View.GONE
        }
        select = if (nowIp == ApiConfigModule.ip_test) 3 else if (nowIp == ApiConfigModule.ip_1) 1 else 2
        select()
        nowIP = select

        //判断是否是请求出错，弹出的
        if (!intent.getBooleanExtra("userOpen", false)) {
            tvTitle.text = "您的网络状况不佳，是否切换网络？"
        }
        tvIp.text = "当前服务器：" + if (select == 1) "主服务器" else if (select == 2) "备用服务器" else "测试环境"

        moren1 = "主服务器：" + getIp(ApiConfigModule.ip_1)
        moren2 = "备用服务器：" + getIp(ApiConfigModule.ip_2)

        tvIp1.text = moren1 + "    ping：0"
        tvIp2.text = moren2 + "    ping：0"
        tvIp3.text = "测试环境：$moren3    ping：0"

        isPingSuccess(ApiConfigModule.ip_1.replace("http://", "").replace(":4001/", ""), 1)
        isPingSuccess(ApiConfigModule.ip_2.replace("http://", "").replace(":4001/", ""), 2)
        isPingSuccess(moren3, 3)
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        tvIp1.setOnClickListener {
            select = 1
            select()
        }
        tvIp2.setOnClickListener {
            select = 2
            select()
        }
        tvIp3.setOnClickListener {
            select = 3
            select()
        }
        tv_cancel.setOnClickListener {
            finish()
        }
        tv_ok.setOnClickListener {
            var ip = ""
            if (select == 1) {
                ip = ApiConfigModule.ip_1
            } else if (select == 2) {
                ip = ApiConfigModule.ip_2
            } else if (select == 3) {
                ip = ApiConfigModule.ip_test
            }
            ZXSharedPrefUtil().putString("base_ip", ip)
            MyApplication.instance.init()
            if (nowIP != 3 && select == 3 || nowIP == 3 && select != 3) {
                //正式环境跟测试环境互相切换 需要重新登录
                handleError("10002", "切换成功")
            } else {
                if (nowIP != select)
                    showToast("切换成功")
                finish()
            }
        }
    }

    private fun select() {
        tvIp1.setTextColor(ContextCompat.getColor(this, R.color.black))
        tvIp2.setTextColor(ContextCompat.getColor(this, R.color.black))
        tvIp3.setTextColor(ContextCompat.getColor(this, R.color.black))
        if (select == 1) {//主服务器
            tvIp1.setTextColor(ContextCompat.getColor(this, R.color.blue))
        } else if (select == 2) {//备用服务器
            tvIp2.setTextColor(ContextCompat.getColor(this, R.color.blue))
        } else if (select == 3) {//测试
            tvIp3.setTextColor(ContextCompat.getColor(this, R.color.blue))
        }
    }

    private fun getIp(ip: String): String {
        return ip.replace(":4001/", "").replace("http://", "")
    }

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1)
                tvIp1.text = "$moren1    ping：${msg.obj}"
            else if (msg.what == 2)
                tvIp2.text = "$moren2    ping：${msg.obj}"
            else if (msg.what == 3)
                tvIp3.text = "测试环境：$moren3    ping：${msg.obj}"
        }
    }

    fun isPingSuccess(ip: String, type: Int) {
        object : Thread()// 创建子线程
        {
            override fun run() {
                val tv_PingInfo = StringBuffer()
                try {
                    val p = Runtime.getRuntime().exec("/system/bin/ping -c 3 $ip")
                    val buf = BufferedReader(InputStreamReader(p.inputStream))

                    var str = ""
                    // 读出全部信息并显示
                    while (buf.readLine() != null) {
                        str += buf.readLine() + "\r\n"
                        tv_PingInfo.append(str)
                    }

                    var obj = tv_PingInfo.toString()
                    var result = "0"
                    if (obj.contains("ttl=") && obj.split("ttl=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size > 1) {
                        val time = obj.split("ttl=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

                        if (time.split("time".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size > 1) {
                            val ttl = time.split("time".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                            result = ttl.replace(" ", "")
                        }
                    }
                    val msg = mHandler.obtainMessage()
                    msg.what = type
                    msg.obj = result
                    mHandler.sendMessage(msg)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        }.start()
    }
}

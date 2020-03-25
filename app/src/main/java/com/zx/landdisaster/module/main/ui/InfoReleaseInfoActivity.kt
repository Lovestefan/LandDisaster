package com.zx.landdisaster.module.main.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.frame.zxmvp.baseapp.BaseApplication
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.frame.zxmvp.baserx.RxSubscriber
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.main.bean.Area
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean
import com.zx.landdisaster.module.main.bean.InfoReleaseBean
import com.zx.landdisaster.module.main.func.adapter.InfoDeliveryReportAdapter
import com.zx.landdisaster.module.main.mvp.contract.InfoReleaseInfoContract
import com.zx.landdisaster.module.main.mvp.model.InfoReleaseInfoModel
import com.zx.landdisaster.module.main.mvp.presenter.InfoReleaseInfoPresenter
import com.zx.zxutils.other.ZXInScrollRecylerManager
import com.zx.zxutils.util.ZXTimeUtil
import kotlinx.android.synthetic.main.activity_info_release_info.*
import rx.Observable
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class InfoReleaseInfoActivity : BaseActivity<InfoReleaseInfoPresenter, InfoReleaseInfoModel>(), InfoReleaseInfoContract.View {

    private val dataBeans = arrayListOf<Area>()
    private val listAdapter = InfoDeliveryReportAdapter(dataBeans)
    private lateinit var infoDeliveryBean: InfoDeliveryBean

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, data: InfoDeliveryBean) {
            val intent = Intent(activity, InfoReleaseInfoActivity::class.java)
            intent.putExtra("data", data)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_info_release_info
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        infoDeliveryBean = intent.getSerializableExtra("data") as InfoDeliveryBean
        tbv_title.setMidText(infoDeliveryBean.servicetitle)

        rv_table.apply {
            layoutManager = ZXInScrollRecylerManager(this@InfoReleaseInfoActivity)
            adapter = listAdapter
        }

        val text = ZXTimeUtil.millis2String(infoDeliveryBean.publishtime) + "  " + infoDeliveryBean.publishdept
        tv_time.text = text

        tv_text.movementMethod = ScrollingMovementMethod()

        mPresenter.getFileData(infoDeliveryBean.serviceid, ApiParamUtil.getInfoReleaseFile())
        readed(infoDeliveryBean.serviceid)

        uploadLog(307, 8, "查看文件：${infoDeliveryBean.servicetitle}")
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    override fun onReadFileResult(data: InfoReleaseBean?) {
        if (data != null) {
            if (data.type == "text") {
                tv_text.text = data.content
                tv_text.visibility = View.VISIBLE
            } else if (data.type == "pdf") {
                getPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mPresenter.downloadFile(ApiConfigModule.BASE_IP + ApiConfigModule.URL_FILE + data.content)
                }
            } else if (data.type == "reportstatis.json") {
                if (data.reportdata != null) {
                    lineView.visibility = View.GONE
                    tv_time.text = data.reportdata!!.title
                    var contents = data.reportdata!!.contents
                    if (contents != null && contents.isNotEmpty())
                        tv_contents.text = contents[0]
                    dataBeans.clear()
                    dataBeans.addAll(data.reportdata!!.areas)
                    listAdapter.notifyDataSetChanged()
                } else {
                    tv_nodata.visibility = View.VISIBLE
                }
                ll_table.visibility = View.VISIBLE
            }
        } else {
            tv_nodata.visibility = View.VISIBLE
        }
    }

    override fun onFileDownloadResult(file: File) {
        getPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            pdf_view.fromFile(file)
                    .defaultPage(0)
                    .enableAnnotationRendering(true)
                    .scrollHandle(DefaultScrollHandle(this))
                    .spacing(10) // in dp
                    .pageFitPolicy(FitPolicy.BOTH)
                    .load()
            pdf_view.visibility = View.VISIBLE
        }
    }


    //标记消息已读
    fun readed(serviceid: String): Observable<String>? {
        BaseApplication.baseApplication.appComponent.repositoryManager()
                .obtainRetrofitService(ApiService::class.java)
                .readed(serviceid)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
                .subscribe(object : RxSubscriber<String?>() {
                    override fun _onNext(t: String?) {
//                        Log.e("信息发布", "成功标识已读")
                    }

                    override fun _onError(code: String?, message: String?) {
                    }
                })
        return null
    }
}

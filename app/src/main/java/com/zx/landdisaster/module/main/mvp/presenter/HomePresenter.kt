package com.zx.landdisaster.module.main.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.frame.zxmvp.http.download.DownInfo
import com.frame.zxmvp.http.download.listener.DownloadOnNextListener
import com.frame.zxmvp.http.download.manager.HttpDownManager
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.app.ConstStrings
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean
import com.zx.landdisaster.module.main.bean.VersionBean
import com.zx.landdisaster.module.main.bean.WeatherBean
import com.zx.landdisaster.module.main.mvp.contract.HomeContract
import com.zx.landdisaster.module.worklog.bean.DailyRemindBean
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFileUtil
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class HomePresenter : HomeContract.Presenter() {
    override fun getFindWeatherdecisionData(map: Map<String, String>) {
        mModel.findWeatherdecision(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<InfoDeliveryBean>>() {
                    override fun _onNext(t: NormalList<InfoDeliveryBean>?) {
                        if (t == null) {
                            mView.onFindWeatherdecisionResult(NormalList())
                            return
                        }
                        mView.onFindWeatherdecisionResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onFindWeatherdecisionResult(NormalList())
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getDailyRemind() {
        mModel.dailyRemindData()
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<DailyRemindBean>() {
                    override fun _onNext(t: DailyRemindBean?) {
                        if (t != null) {
                            mView.onRemindResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                    }

                })
    }

    override fun updateLocation(x: String, y: String) {
        mModel.updateLocation(x,y)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<Any>() {
                    override fun _onNext(t: Any?) {
                    }

                    override fun _onError(code: String?, message: String?) {
                    }
                })
    }

    override fun getWeatherdescription() {
        mModel.getWeatherdescription()
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>() {
                    override fun _onNext(t: String?) {
                        mView.onWeatherdescriptionResult(t!!)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onWeatherdescriptionResult(null)
                    }

                })
    }

    override fun getDegree(url: String) {
        mModel.getDegree(url)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<WeatherBean>() {
                    override fun _onNext(t: WeatherBean) {
                        mView.onDegreeResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onDegreeResult(null)
                    }

                })
    }

    override fun downloadApk(downUrl: String) {
        val fileName = downUrl.substring(downUrl.lastIndexOf("/") + 1)
        val downInfo = DownInfo( ApiConfigModule.URL_FILE+downUrl)
        downInfo.baseUrl = ApiConfigModule.BASE_IP
        downInfo.savePath = ConstStrings.getApkPath() + fileName
        downInfo.listener = object : DownloadOnNextListener<Any>() {
            override fun onNext(o: Any) {

            }

            override fun onStart() {
                ZXDialogUtil.showLoadingDialog(mContext, "正在下载中，请稍后...", 0)
            }

            override fun onComplete(file: File) {
                mView.onApkDownloadResult(file)
                ZXDialogUtil.dismissLoadingDialog()
            }

            override fun updateProgress(progress: Int) {
                ZXDialogUtil.showLoadingDialog(mContext, "正在下载中，请稍后...", progress)
            }
        }
        if (ZXFileUtil.isFileExists(ConstStrings.getApkPath() + fileName)) {
            mView.onApkDownloadResult(File(ConstStrings.getApkPath() + fileName))
        } else {
            HttpDownManager.getInstance().startDown(downInfo)
        }
    }

    override fun getVerson() {
        mModel.versionData()
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<VersionBean>() {
                    override fun _onNext(t: VersionBean?) {
                        if (t != null) {
                            mView.onVersionResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
//                        mView.handleError(code, message)
                    }

                })
    }
}
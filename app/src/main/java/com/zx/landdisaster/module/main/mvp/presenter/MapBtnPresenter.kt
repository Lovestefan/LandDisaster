package com.zx.landdisaster.module.main.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.LiveInfoMationBean
import com.zx.landdisaster.module.disaster.bean.RainPointBean
import com.zx.landdisaster.module.main.bean.*
import com.zx.landdisaster.module.main.mvp.contract.MapBtnContract
import okhttp3.RequestBody


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MapBtnPresenter : MapBtnContract.Presenter() {

    override fun getDisasterList(map: Map<String, String>) {
        mModel.disasterListData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<DisasterPointBean>>(mView) {
                    override fun _onNext(t: NormalList<DisasterPointBean>?) {
                        if (t?.result != null) {
                            mView.onDisasterResult(t.result!!)
                        } else {
                            mView.onDisasterResult(arrayListOf())
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getHiddenList(info: RequestBody) {
        mModel.hiddenListData(info)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<HiddenPointBean>>(mView) {
                    override fun _onNext(t: NormalList<HiddenPointBean>?) {
                        if (t?.result != null) {
                            mView.onHiddenResult(t.result!!)
                        } else {
                            mView.onHiddenResult(arrayListOf())
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }
                })
    }

    override fun getRainList(map: Map<String, String>) {
        mModel.rainListData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<RainDataBean>(mView) {
                    override fun _onNext(t: RainDataBean?) {
                        if (t == null) {
                            mView.onRainListResult(null)
                        } else {
                            mView.onRainListResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onRainListResult(null)
                    }

                })
    }

    override fun getTaskNum() {
        mModel.taskNumData()
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<TaskNumBean>() {
                    override fun _onNext(t: TaskNumBean?) {
                        if (t != null) {
                            mView.onTaskNumResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                    }

                })
    }

    override fun getUpdateTime(map: Map<String, String>) {
        mModel.getUpdateTime(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>() {
                    override fun _onNext(t: String) {
                        mView.onUpdateTimeResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onUpdateTimeResult("")
                    }

                })
    }

    override fun getMaxRainstation(map: Map<String, String>) {
        mModel.getMaxRainstation(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<RainPointBean>>(mView) {
                    override fun _onNext(t: List<RainPointBean>?) {
                        mView.onMaxRainstationResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onMaxRainstationResult(null)
                        mView.handleError(code, message)
                    }
                })
    }

    override fun getLiveInfomation(map: Map<String, String>) {
        mModel.getLiveInfomation(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<LiveInfoMationBean>(mView) {
                    override fun _onNext(t: LiveInfoMationBean) {
                        if (t == null) {
                            mView.onLiveInfomationResult(null)
                        } else {
                            mView.onLiveInfomationResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onLiveInfomationResult(null)
                    }

                })
    }

    override fun getDisasterFromGis(map: Map<String, String>) {
        mModel.getDisasterFromGis(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<DisasterFromGisBean>>(mView) {
                    override fun _onNext(t: List<DisasterFromGisBean>) {
                        if (t == null) {
                            mView.onDisasterFromGisResult(null)
                        } else {
                            mView.onDisasterFromGisResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onDisasterFromGisResult(null)
                    }

                })
    }

    override fun getRainStationFromGis(map: Map<String, String>) {
        mModel.getRainStationFromGis(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<RainStationFromGisBean>>(mView) {
                    override fun _onNext(t: List<RainStationFromGisBean>) {
                        if (t == null) {
                            mView.onRainStationFromGisResult(null)
                        } else {
                            mView.onRainStationFromGisResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onRainStationFromGisResult(null)
                    }

                })
    }
}
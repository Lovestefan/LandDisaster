package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.disaster.mvp.contract.RainAndPointContract
import com.zx.landdisaster.module.main.bean.AreaFromGisBean
import com.zx.landdisaster.module.main.bean.DisasterFromGisBean
import com.zx.landdisaster.module.main.bean.RainStationFromGisBean


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class RainAndPointPresenter : RainAndPointContract.Presenter() {
    override fun getAreaFromGis(map: Map<String, String>) {
        mModel.getAreaFromGis(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<AreaFromGisBean>>(mView) {
                    override fun _onNext(t: List<AreaFromGisBean>) {
                        if (t == null) {
                            mView.onAreaFromGisResult(null)
                        } else {
                            mView.onAreaFromGisResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onAreaFromGisResult(null)
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
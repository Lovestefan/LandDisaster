package com.zx.landdisaster.module.main.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.google.gson.JsonObject
import com.zx.landdisaster.module.main.mvp.contract.MapContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MapPresenter : MapContract.Presenter() {

    override fun getMapExtent(esriString: String, areaCode: String) {
        mModel.mapExtentData(esriString)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<JsonObject>() {
                    override fun _onNext(t: JsonObject?) {
                        if (t != null) {
                            mView.onMapExtentResult(t.toString(), areaCode)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                    }

                })
    }


}
package com.zx.landdisaster.module.main.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.main.bean.SearchBean
import com.zx.landdisaster.module.main.mvp.contract.MapSearchContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MapSearchPresenter : MapSearchContract.Presenter() {
    override fun doSearch(map: Map<String, String>) {
        mModel.searchData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<SearchBean>>() {
                    override fun _onNext(t: NormalList<SearchBean>?) {
                        if (t == null) {
                            mView.onSearchResult(NormalList())
                            return
                        }
                        mView.onSearchResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }


}
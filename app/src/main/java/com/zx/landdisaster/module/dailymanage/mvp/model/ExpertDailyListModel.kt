package com.zx.landdisaster.module.dailymanage.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.basebean.BaseRespose
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.dailymanage.bean.ExpertDailyBean

import com.zx.landdisaster.module.dailymanage.mvp.contract.ExpertDailyListContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ExpertDailyListModel : BaseModel(), ExpertDailyListContract.Model {
    override fun findMyReport(map: Map<String, String>): Observable<NormalList<ExpertDailyBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findMyReport(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}
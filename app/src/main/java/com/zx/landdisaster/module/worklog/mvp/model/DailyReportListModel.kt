package com.zx.landdisaster.module.worklog.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.worklog.bean.DailyListBean

import com.zx.landdisaster.module.worklog.mvp.contract.DailyReportListContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class DailyReportListModel : BaseModel(), DailyReportListContract.Model {
    override fun dailyListData(map: Map<String, String>): Observable<NormalList<DailyListBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getDailyList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}
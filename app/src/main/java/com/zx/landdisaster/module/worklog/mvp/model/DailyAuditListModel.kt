package com.zx.landdisaster.module.worklog.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.worklog.bean.DailyAuditListBean

import com.zx.landdisaster.module.worklog.mvp.contract.DailyAuditListContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class DailyAuditListModel : BaseModel(), DailyAuditListContract.Model {

    override fun pageAuditReport(map: Map<String, String>): Observable<NormalList<DailyAuditListBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .pageAuditReport(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}
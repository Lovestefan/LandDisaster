package com.zx.landdisaster.module.worklog.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean

import com.zx.landdisaster.module.worklog.mvp.contract.DailyReportDetailContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class DailyReportDetailModel : BaseModel(), DailyReportDetailContract.Model {
    override fun dailyFileData(map: Map<String, String>): Observable<List<AuditReportFileBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getDailyFile(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}
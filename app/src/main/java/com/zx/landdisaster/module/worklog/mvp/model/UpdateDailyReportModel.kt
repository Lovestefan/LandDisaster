package com.zx.landdisaster.module.worklog.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.worklog.bean.DailyRemindBean

import com.zx.landdisaster.module.worklog.mvp.contract.UpdateDailyReportContract
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class UpdateDailyReportModel : BaseModel(), UpdateDailyReportContract.Model {

    override fun dailySubmitData(info : RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .submitDailyInfo(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


    override fun dailyFileAddData(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .dailyUploadFile(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}
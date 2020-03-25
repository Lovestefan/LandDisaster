package com.zx.landdisaster.module.areamanager.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.areamanager.mvp.contract.AddWeekWorkContract
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AddWeekWorkModel : BaseModel(), AddWeekWorkContract.Model {
    override fun getWeekWorkFileData(map: Map<String, String>): Observable<List<AuditReportFileBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getWeekWorkFile(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun uploadData(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .uploadWeekWork(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun uploadFileData(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .uploadWeekWorkFile(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
}
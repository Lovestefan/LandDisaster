package com.zx.landdisaster.module.worklog.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.worklog.bean.WorkLogListBean

import com.zx.landdisaster.module.worklog.mvp.contract.WorkLogListContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class WorkLogListModel : BaseModel(), WorkLogListContract.Model {
    override fun workLogData(map: Map<String, String>): Observable<NormalList<WorkLogListBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getWorkLogList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun workLogFileData(map: Map<String, String>): Observable<List<AuditReportFileBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getWeekWorkFile(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}
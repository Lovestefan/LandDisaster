package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportListBean
import com.zx.landdisaster.module.disaster.mvp.contract.ReportHazardListContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportHazradListModel : BaseModel(), ReportHazardListContract.Model {

    override fun reportListData(map: Map<String, String>): Observable<NormalList<ReportListBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getReportList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}
package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService

import com.zx.landdisaster.module.disaster.mvp.contract.AuditHomeContract
import com.zx.landdisaster.module.main.bean.TaskNumBean
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AuditHomeModel : BaseModel(), AuditHomeContract.Model {
    override fun taskNumData(): Observable<TaskNumBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getTaskNum()
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
}
package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.main.bean.DisasterPointBean

import com.zx.landdisaster.module.disaster.mvp.contract.DisasterPointListContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class DisasterPointListModel : BaseModel(), DisasterPointListContract.Model {
    override fun disasterListData(map: Map<String, String>): Observable<NormalList<DisasterPointBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getDisasterList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}
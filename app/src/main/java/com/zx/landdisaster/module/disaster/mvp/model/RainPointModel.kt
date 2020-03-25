package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.RainPointBean

import com.zx.landdisaster.module.disaster.mvp.contract.RainPointContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class RainPointModel : BaseModel(), RainPointContract.Model {
    override fun rainListData(map: Map<String, String>): Observable<NormalList<RainPointBean>?> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getRainPointList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}
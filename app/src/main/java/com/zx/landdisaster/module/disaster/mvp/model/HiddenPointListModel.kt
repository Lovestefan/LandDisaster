package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.main.bean.HiddenPointBean

import com.zx.landdisaster.module.disaster.mvp.contract.HiddenPointListContract
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class HiddenPointListModel : BaseModel(), HiddenPointListContract.Model {

    override fun hiddenListData(info: RequestBody, pageNo : Int, pageSize : Int): Observable<NormalList<HiddenPointBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getHiddenList(info, pageNo, pageSize)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
}
package com.zx.landdisaster.module.areamanager.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList

import com.zx.landdisaster.module.areamanager.mvp.contract.AddPatrolContract
import com.zx.landdisaster.module.disaster.bean.DisasterPkiaaBean
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AddPatrolModel : BaseModel(), AddPatrolContract.Model {
    override fun uploadFileData(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .uploadPatvolFile(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun submitData(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .submit(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun pkiaaListData(map: Map<String, String>): Observable<NormalList<DisasterPkiaaBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getDisasterPkiaaList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}
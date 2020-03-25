package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.DisasterPkiaaBean
import com.zx.landdisaster.module.disaster.bean.ReportResultbean
import com.zx.landdisaster.module.disaster.mvp.contract.ReportHazardAddContract
import com.zx.zxutils.entity.KeyValueEntity
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportHazardAddModel : BaseModel(), ReportHazardAddContract.Model {
    override fun isExistInRegion(map:Map<String, String>): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getExistInRegion(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun reportSubmitData(info: RequestBody): Observable<ReportResultbean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .submitReport(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun fileUploadData(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .uploadFile(body = info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun reportEditData(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .editReport(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun deleteFileData(map: Map<String, String>): Observable<Any> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .deleteFile(map = map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun pkiaaListData(map: Map<String, String>): Observable<NormalList<DisasterPkiaaBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getDisasterPkiaaList(map = map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    fun areaListData(): List<KeyValueEntity> {
        val areaList = arrayListOf<KeyValueEntity>()
        areaList.add(KeyValueEntity("万州区", "500101"))
        areaList.add(KeyValueEntity("涪陵区", "500102"))
        areaList.add(KeyValueEntity("渝中区", "500103"))
        areaList.add(KeyValueEntity("大渡口区", "500104"))
        areaList.add(KeyValueEntity("江北区", "500105"))
        areaList.add(KeyValueEntity("沙坪坝区", "500106"))
        areaList.add(KeyValueEntity("九龙坡区", "500107"))
        areaList.add(KeyValueEntity("南岸区", "500108"))
        areaList.add(KeyValueEntity("北碚区", "500109"))
        areaList.add(KeyValueEntity("綦江区", "500110"))
        areaList.add(KeyValueEntity("大足区", "500111"))
        areaList.add(KeyValueEntity("渝北区", "500112"))
        areaList.add(KeyValueEntity("巴南区", "500113"))
        areaList.add(KeyValueEntity("黔江区", "500114"))
        areaList.add(KeyValueEntity("长寿区", "500115"))
        areaList.add(KeyValueEntity("江津区", "500116"))
        areaList.add(KeyValueEntity("合川区", "500117"))
        areaList.add(KeyValueEntity("永川区", "500118"))
        areaList.add(KeyValueEntity("南川区", "500119"))
        areaList.add(KeyValueEntity("璧山区", "500120"))
        areaList.add(KeyValueEntity("铜梁区", "500151"))
        areaList.add(KeyValueEntity("潼南区", "500152"))
        areaList.add(KeyValueEntity("荣昌县", "500153"))
        areaList.add(KeyValueEntity("梁平县", "500228"))
        areaList.add(KeyValueEntity("城口县", "500229"))
        areaList.add(KeyValueEntity("丰都县", "500230"))
        areaList.add(KeyValueEntity("垫江县", "500231"))
        areaList.add(KeyValueEntity("武隆县", "500232"))
        areaList.add(KeyValueEntity("忠县", "500233"))
        areaList.add(KeyValueEntity("开县", "500234"))
        areaList.add(KeyValueEntity("云阳县", "500235"))
        areaList.add(KeyValueEntity("奉节县", "500236"))
        areaList.add(KeyValueEntity("巫山县", "500237"))
        areaList.add(KeyValueEntity("巫溪县", "500238"))
        areaList.add(KeyValueEntity("石柱县", "500240"))
        areaList.add(KeyValueEntity("秀山县", "500241"))
        areaList.add(KeyValueEntity("酉阳县", "500242"))
        areaList.add(KeyValueEntity("彭水县", "500243"))
        return areaList
    }

}
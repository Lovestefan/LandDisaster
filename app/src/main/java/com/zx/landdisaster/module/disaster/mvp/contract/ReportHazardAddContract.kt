package com.zx.landdisaster.module.disaster.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IModel
import com.frame.zxmvp.base.IView
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.DisasterPkiaaBean
import com.zx.landdisaster.module.disaster.bean.ReportDetailBean
import com.zx.landdisaster.module.disaster.bean.ReportResultbean
import okhttp3.RequestBody
import rx.Observable
import java.io.File

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface ReportHazardAddContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun onReportSubmitResult(reportResultbean: ReportResultbean)

        fun onFileUploadPregress(progress: Int)

        fun onFileUploadResult(flowNum: String)

        fun onFileDeleteResult()

        fun onPkiaaListResult(pkiaaList: NormalList<DisasterPkiaaBean>?)

        fun onSubmitErrorResult()

        fun onExistInRegionResult(data: String)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun reportSubmitData(info: RequestBody): Observable<ReportResultbean>

        fun reportEditData(info: RequestBody): Observable<String>

        fun fileUploadData(info: RequestBody): Observable<String>

        fun deleteFileData(map: Map<String, String>): Observable<Any>

        fun pkiaaListData(map: Map<String, String>): Observable<NormalList<DisasterPkiaaBean>>

        fun isExistInRegion(map: Map<String, String>): Observable<String>
    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun submitReport(reportBean: ReportDetailBean)

        abstract fun uploadFile(files: List<File>, flowNum: String)

        abstract fun deleteFile(map: Map<String, String>)

        abstract fun getPkiaaList(map: Map<String, String>)

        abstract fun getIsExistInRegion(map: Map<String, String>)
    }
}

package com.zx.landdisaster.module.areamanager.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IModel
import com.frame.zxmvp.base.IView
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import okhttp3.RequestBody
import rx.Observable
import java.io.File

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface AddWeekWorkContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun onUploadResult(logid: String)

        fun onUploadFilePregress(progress: Int)
        fun onUploadFileResult()
        fun getWeekWorkFileResult(list: List<AuditReportFileBean>)
        fun getWeekWorkFileResult()
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun uploadData(info: RequestBody): Observable<String>
        fun uploadFileData(info: RequestBody): Observable<String>
        fun getWeekWorkFileData(map: Map<String, String>): Observable<List<AuditReportFileBean>>
    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun uploadWeekWork(info: RequestBody)
        abstract fun uploadWeekWorkFile(files: List<File>, logid: String, reportType: String)
        abstract fun getWeekWorkFile(map: Map<String, String>)
    }
}
package com.zx.landdisaster.module.areamanager.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IView
import com.frame.zxmvp.base.IModel
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.DisasterPkiaaBean
import okhttp3.RequestBody
import rx.Observable
import java.io.File

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface AddPatrolContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun onSubmitResult(logid: String)

        fun onUploadFilePregress(progress: Int)
        fun onUploadFileResult()
        fun onPkiaaListResult(pkiaaList : NormalList<DisasterPkiaaBean>?)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun submitData(info: RequestBody): Observable<String>

        fun uploadFileData(info: RequestBody): Observable<String>
        fun pkiaaListData(map: Map<String, String>) : Observable<NormalList<DisasterPkiaaBean>>
    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun submit(info: RequestBody)

        abstract fun uploadFile(files: List<File>, recordid: String)
        abstract fun getPkiaaList(map: Map<String, String>)
    }
}

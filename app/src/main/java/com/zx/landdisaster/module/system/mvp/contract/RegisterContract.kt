package com.zx.landdisaster.module.system.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IView
import com.frame.zxmvp.base.IModel
import com.zx.landdisaster.module.system.bean.AreaBean
import okhttp3.RequestBody
import rx.Observable
import java.io.File

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface RegisterContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun onHearUploadResult()
        fun onRegisterResult(hearUrl: String)
        fun onUploadFilePregress(progress: Int)
        fun onFindByParentResult(areaBean: List<AreaBean>?)
        fun onCheckAccountResult(data: String)
        fun onCheckResult(data: String)
        fun onSendRegisterResult(data: String)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun getHearUpload(map: RequestBody): Observable<String>
        fun getRegister( map: Map<String, String>): Observable<String>
        fun findByParent(map: Map<String, String>): Observable<List<AreaBean>>
        fun getCheckAccount(map: Map<String, String>): Observable<String>
        fun checkPassword(map: Map<String, String>): Observable<String>
        fun sendRegister(map: Map<String, String>): Observable<String?>
    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun hearUpload(file: File, personid: String)
        abstract fun register( map: Map<String, String>)
        abstract fun getFindByParent(map: Map<String, String>)
        abstract fun getCheckAccountData(map: Map<String, String>)
        abstract fun checkPassword(map: Map<String, String>)
        abstract fun sendRegister(map: Map<String, String>)
    }
}

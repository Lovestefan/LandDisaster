package com.zx.landdisaster.module.other.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IModel
import com.frame.zxmvp.base.IView
import com.zx.landdisaster.module.other.bean.CountDisasterBean
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface CountDisasterContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun onCountDisasterResult(countList: List<CountDisasterBean>)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun countDisasterData(map: Map<String, String>): Observable<List<CountDisasterBean>>
    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun countDisaster(map: Map<String, String>)
    }
}

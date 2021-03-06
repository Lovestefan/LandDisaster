package com.zx.landdisaster.module.system.mvp.contract;

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IModel
import com.frame.zxmvp.base.IView
import com.zx.landdisaster.base.bean.UserBean
import com.zx.landdisaster.module.disaster.bean.PersonRoleBean
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface SplashContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun onLoginResult(userBean: UserBean)

        fun onLoginError(code: String?)

        fun onPersonRoleResult(roleBean: PersonRoleBean)

        fun onCheckResult(data: String)
        fun onCurrentAuthMenuResult(list: List<String>)

    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun doLogin(info: RequestBody): Observable<UserBean>

        fun personRoleData(): Observable<PersonRoleBean>


        fun checkPassword(map: Map<String, String>): Observable<String>
        fun currentAuthMenu(map: Map<String, String>): Observable<List<String>>

    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun doLogin(info: RequestBody)
        abstract fun checkPassword(map: Map<String, String>)
        abstract fun currentAuthMenu(map: Map<String, String>)
    }
}

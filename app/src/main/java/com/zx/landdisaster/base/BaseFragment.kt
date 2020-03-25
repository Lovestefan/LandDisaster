package com.zx.landdisaster.base

import android.os.Bundle
import android.os.Handler
import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.RxBaseFragment
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.bean.Jurisdiction
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.ActionLog
import com.zx.landdisaster.base.tool.UserActionTool
import com.zx.zxutils.util.ZXSharedPrefUtil
import com.zx.zxutils.util.ZXToastUtil

/**
 * Created by Xiangb on 2019/2/26.
 * 功能：
 */
abstract class BaseFragment<T : BasePresenter<*, *>, E : BaseModel> : RxBaseFragment<T, E>() {
    var pageSize = 10
    private lateinit var permissionArray: Array<String>
    private lateinit var permessionBack: () -> Unit
    var mSharedPrefUtil = ZXSharedPrefUtil()
    var handler = Handler()

    override fun showLoading(message: String) {
        (activity as BaseActivity<*, *>).showLoading(message)
        //        ZXDialogUtil.showLoadingDialog(getActivity(), message);
    }

    override fun dismissLoading() {
        (activity as BaseActivity<*, *>).dismissLoading()
        //        ZXDialogUtil.dismissLoadingDialog();
    }

    override fun showLoading(message: String, progress: Int) {
        (activity as BaseActivity<*, *>).showLoading(message, progress)
    }

    override fun showToast(message: String) {
        ZXToastUtil.showToast(message)
    }

    override fun handleError(code: String?, message: String) {
        (activity as BaseActivity<*, *>).handleError(code, message)
    }

    override fun handlerSuccess(message: String?) {
        (activity as BaseActivity<*, *>).handlerSuccess(message)
    }

    override fun initView(savedInstanceState: Bundle?) {
        onViewListener()
    }

    abstract fun onViewListener()

    fun getPermission(permissionArray: Array<String>, permessionBack: () -> Unit) {
        (activity as BaseActivity<*, *>).getPermission(permissionArray, permessionBack)
    }

    fun uploadLog(id: Int, type: Int, action: String) {
        UserActionTool.updateUserAction(ActionLog.getActionLog(id, type, action))
    }

    fun haveQuanXian(qx: String): Boolean {
        if (UserManager.quanxian.isNotEmpty()) {
            return UserManager.quanxian.toString().contains(qx)
        } else {
            return false
        }
    }

    fun getUserType(): Int {
        return if (haveQuanXian(Jurisdiction.new_home)) {
            2
        } else if (haveQuanXian(Jurisdiction.old_home)) {
            0//群测群防，片区负责人
        } else {
            1//地环站，专家，驻守地质队员
        }
    }
}
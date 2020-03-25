package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.disaster.bean.AuditListBean
import com.zx.landdisaster.module.disaster.mvp.contract.AuditListContract
import rx.Observable


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AuditListPresenter : AuditListContract.Presenter() {
    override fun getAuditList(map: HashMap<String, String>) {
        val observable: Observable<NormalList<AuditListBean>>
        if (UserManager.getUser().personRole.leader) {
            observable = mModel.auditListLeaderData(map)
        } else if (UserManager.getUser().personRole.dispatch) {
            observable = mModel.auditListDispatchData(map)
        } else {
            observable = mModel.auditListData(map)
        }
        observable.compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<AuditListBean>>() {
                    override fun _onNext(t: NormalList<AuditListBean>?) {
                        if (t == null) {
                            mView.onAuditListResult(NormalList())
                            return
                        }
                        mView.onAuditListResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onAuditListResult(NormalList())
                        mView.handleError(code, message)
                    }

                })
    }

}
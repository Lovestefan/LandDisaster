package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportStatisGroupdeBean
import com.zx.landdisaster.module.disaster.bean.ReportstatisBean
import com.zx.landdisaster.module.disaster.mvp.contract.ReportStatisContract
import com.zx.landdisaster.module.system.bean.AreaBean


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportStatisPresenter : ReportStatisContract.Presenter() {

    override fun countReports(map: Map<String, String>) {
        mModel.countReports(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<ReportstatisBean>>() {
                    override fun _onNext(t: NormalList<ReportstatisBean>?) {
                        if (t == null) {
                            mView.onReportStatisResult(NormalList())
                            return
                        }
                        mView.onReportStatisResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onReportStatisResult(NormalList())
                        mView.handleError(code, message)
                    }
                })
    }

    override fun pageGarrisonreportrate(map: Map<String, String>) {
        mModel.pageGarrisonreportrate(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<ReportstatisBean>>() {
                    override fun _onNext(t: NormalList<ReportstatisBean>?) {
                        if (t == null) {
                            mView.onReportStatisResult(NormalList())
                            return
                        }
                        mView.onReportStatisResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onReportStatisResult(NormalList())
                        mView.handleError(code, message)
                    }
                })
    }

    override fun pageAreaManagerreportrate(map: Map<String, String>) {
        mModel.pageAreaManagerreportrate(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<ReportstatisBean>>() {
                    override fun _onNext(t: NormalList<ReportstatisBean>?) {
                        if (t == null) {
                            mView.onReportStatisResult(NormalList())
                            return
                        }
                        mView.onReportStatisResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onReportStatisResult(NormalList())
                        mView.handleError(code, message)
                    }
                })
    }

    override fun pageGroupdefensesreportrate(map: Map<String, String>) {
        mModel.pageGroupdefensesreportrate(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<ReportStatisGroupdeBean>>() {
                    override fun _onNext(t: NormalList<ReportStatisGroupdeBean>?) {
                        if (t == null) {
                            mView.onPageGroupdefensesreportrate(NormalList())
                            return
                        }
                        mView.onPageGroupdefensesreportrate(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onPageGroupdefensesreportrate(NormalList())
                        mView.handleError(code, message)
                    }
                })
    }

    override fun getFindByParent(map: Map<String, String>) {
        mModel.findByParent(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<AreaBean>>() {
                    override fun _onNext(t: List<AreaBean>?) {
                        mView.onFindByParentResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun findNoReportTime(map: Map<String, String>) {
        mModel.findNoReportTime(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<String>>() {
                    override fun _onNext(t: List<String>?) {
                        mView.onFindNoReportTime(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onFindNoReportTime(null)
                        mView.handleError(code, message)
                    }

                })
    }

}
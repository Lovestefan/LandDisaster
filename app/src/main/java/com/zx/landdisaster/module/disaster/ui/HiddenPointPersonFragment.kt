package com.zx.landdisaster.module.disaster.ui

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.module.disaster.bean.HiddenDetailBean
import com.zx.landdisaster.module.disaster.bean.QueryPersonBean
import com.zx.landdisaster.module.disaster.func.adapter.HiddenPersonAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.HiddenPointPersonContract
import com.zx.landdisaster.module.disaster.mvp.model.HiddenPointPersonModel
import com.zx.landdisaster.module.disaster.mvp.presenter.HiddenPointPersonPresenter
import kotlinx.android.synthetic.main.fragment_hidden_point_person.*

/**
 * Create By admin On 2017/7/11
 * 功能：四重网格人员
 */
class HiddenPointPersonFragment : BaseFragment<HiddenPointPersonPresenter, HiddenPointPersonModel>(), HiddenPointPersonContract.View {

    var personList = arrayListOf<QueryPersonBean.PersonBean>()
    var personAdapter = HiddenPersonAdapter(personList)

    companion object {
        /**
         * 启动器
         */
        fun newInstance(): HiddenPointPersonFragment {
            val fragment = HiddenPointPersonFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_hidden_point_person
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        rv_hidden_person.apply {
            layoutManager = GridLayoutManager(activity!!, 2)
            adapter = personAdapter
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    fun setData(hiddenDetailBean: HiddenDetailBean) {
        mPresenter.getPersonInfo(hiddenDetailBean.pkiaa!!, hiddenDetailBean.areacode!!)
    }

    override fun onPersonResult(queryPersonBean: QueryPersonBean) {
        personList.clear()
        if (queryPersonBean.personRingStandDetail.isNotEmpty()) personList.add(queryPersonBean.personRingStandDetail.first())
        if (queryPersonBean.personAreaManagerDetail.isNotEmpty()) personList.add(queryPersonBean.personAreaManagerDetail.first())
        if (queryPersonBean.personGarrisonDetail.isNotEmpty()) personList.add(queryPersonBean.personGarrisonDetail.first())
        if (queryPersonBean.personGroupDefenseDetail.isNotEmpty()) personList.add(queryPersonBean.personGroupDefenseDetail.first())
        personAdapter.notifyDataSetChanged()
    }
}

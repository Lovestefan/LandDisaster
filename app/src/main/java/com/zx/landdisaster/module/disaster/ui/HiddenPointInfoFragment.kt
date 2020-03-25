package com.zx.landdisaster.module.disaster.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.bean.KeyValueBean
import com.zx.landdisaster.module.disaster.bean.HiddenDetailBean
import com.zx.landdisaster.module.disaster.func.adapter.HiddenDetailAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.HiddenPointInfoContract
import com.zx.landdisaster.module.disaster.mvp.model.HiddenPointInfoModel
import com.zx.landdisaster.module.disaster.mvp.presenter.HiddenPointInfoPresenter
import kotlinx.android.synthetic.main.fragment_hidden_point_info.*

/**
 * Create By admin On 2017/7/11
 * 功能：隐患点详情
 */
class HiddenPointInfoFragment : BaseFragment<HiddenPointInfoPresenter, HiddenPointInfoModel>(), HiddenPointInfoContract.View {

    private val dataList = arrayListOf<Pair<String, List<KeyValueBean>>>()
    private val hiddenAdapter = HiddenDetailAdapter(dataList)

    private var hiddenDetailBean: HiddenDetailBean? = null

    companion object {
        /**
         * 启动器
         */
        fun newInstance(): HiddenPointInfoFragment {
            val fragment = HiddenPointInfoFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_hidden_point_info
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        rv_hidden_info.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = hiddenAdapter
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    fun setData(hiddenDetailBean: HiddenDetailBean) {
        dataList.clear()

        var list_0 = arrayListOf<KeyValueBean>()

        list_0.add(KeyValueBean("名称", if (hiddenDetailBean.name != null) hiddenDetailBean.name!! else ""))
        list_0.add(KeyValueBean("地灾类型", if (hiddenDetailBean.type != null) hiddenDetailBean.type!! else ""))
        list_0.add(KeyValueBean("等级", hiddenDetailBean.scalelevelDesc?:hiddenDetailBean.getScaleLevelString()))
        list_0.add(KeyValueBean("地灾编号", if (hiddenDetailBean.pkiaa != null) hiddenDetailBean.pkiaa!! else ""))
        list_0.add(KeyValueBean("地理位置", if (hiddenDetailBean.address != null) hiddenDetailBean.address!! else ""))
        list_0.add(KeyValueBean("村长", if (hiddenDetailBean.monitormass != null) hiddenDetailBean.monitormass!! else ""))
        list_0.add(KeyValueBean("电话", if (hiddenDetailBean.phone != null) hiddenDetailBean.phone!! else ""))

        dataList.add(Pair("基础信息", list_0))

        var list_1 = arrayListOf<KeyValueBean>()

        list_1.add(KeyValueBean("灾害等级", if (hiddenDetailBean.disastegradDesc != null) hiddenDetailBean.disastegradDesc!! else "未定级"))
        list_1.add(KeyValueBean("险情等级", if (hiddenDetailBean.dangerousrankDesc != null) hiddenDetailBean.dangerousrankDesc!! else "未定级"))
        list_1.add(KeyValueBean("死亡人口", if (hiddenDetailBean.deathcount != null) hiddenDetailBean.deathcount!! else ""))
        list_1.add(KeyValueBean("威胁人口", if (hiddenDetailBean.trappedunm != null) hiddenDetailBean.trappedunm!! else ""))
        list_1.add(KeyValueBean("毁坏房屋", if (hiddenDetailBean.destroyshouse != null) hiddenDetailBean.destroyshouse!! else ""))
        list_1.add(KeyValueBean("威胁房屋", if (hiddenDetailBean.housethreat != null) hiddenDetailBean.housethreat!! + "户" else ""))
        list_1.add(KeyValueBean("阻断交通", if (hiddenDetailBean.transStringerrupt != null) hiddenDetailBean.transStringerrupt!! else ""))
        list_1.add(KeyValueBean("毁坏田地", if (hiddenDetailBean.farmdestroy != null) hiddenDetailBean.farmdestroy!! else ""))

        list_1.add(KeyValueBean("交通隐患", if (hiddenDetailBean.transtroublehide != null) hiddenDetailBean.transtroublehide!! else ""))
        list_1.add(KeyValueBean("威胁财产", if (hiddenDetailBean.threatenpro != null) hiddenDetailBean.threatenpro!! + "万元" else ""))
        list_1.add(KeyValueBean("其他危害", if (hiddenDetailBean.otherharms != null) hiddenDetailBean.otherharms!! else ""))
        list_1.add(KeyValueBean("直接损失", if (hiddenDetailBean.directloss != null) hiddenDetailBean.directloss!! else ""))
        list_1.add(KeyValueBean("威胁对象", if (hiddenDetailBean.threatenobjDesc != null) hiddenDetailBean.threatenobjDesc!! else ""))


        dataList.add(Pair("灾险情", list_1))



        hiddenAdapter.notifyDataSetChanged()
    }
}

package com.zx.landdisaster.module.areamanager.ui

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.app.ConstStrings
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.ActionLog
import com.zx.landdisaster.base.tool.LocationTool
import com.zx.landdisaster.module.areamanager.bean.PatrolDetailBean
import com.zx.landdisaster.module.areamanager.mvp.contract.AddPatrolContract
import com.zx.landdisaster.module.areamanager.mvp.model.AddPatrolModel
import com.zx.landdisaster.module.areamanager.mvp.presenter.AddPatrolPresenter
import com.zx.landdisaster.module.disaster.bean.DisasterPkiaaBean
import com.zx.landdisaster.module.disaster.func.adapter.DisasterPkiaaListAdapter
import com.zx.landdisaster.module.disaster.func.util.TextSizeUtil
import com.zx.landdisaster.module.other.ui.AddFileFragment
import com.zx.zxutils.util.*
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import com.zx.zxutils.views.SwipeRecylerView.ZXSwipeRecyler
import kotlinx.android.synthetic.main.activity_add_patrol.*
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：巡查上报
 */
class AddPatrolFragment : BaseFragment<AddPatrolPresenter, AddPatrolModel>(), AddPatrolContract.View {

    private lateinit var addFileFragment: AddFileFragment
    private var srPkiaaSearchList: ZXSwipeRecyler? = null
    private var pkiaaList: ArrayList<DisasterPkiaaBean>? = null
    private var pkiaaAdapter: DisasterPkiaaListAdapter? = null
    private var pkiaaPageNo = 1
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private var patrolBean = PatrolDetailBean("", 1, "", 0.0, 0.0, null)

    companion object {
        /**
         * 启动器
         */
        fun newInstance(patrolBean: PatrolDetailBean? = null): AddPatrolFragment {
            val fragment = AddPatrolFragment()
            val bundle = Bundle()
            bundle.putSerializable("patrolInfo", patrolBean)
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_add_patrol
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val roleBean = UserManager.getUser().personRole
        if (!roleBean.groupDefense && !roleBean.areaManager) {
            TextSizeUtil.resetText(activity!!, et_pkiaa)
            TextSizeUtil.resetText(activity!!, tv_pkiaaSearch)
            TextSizeUtil.resetText(activity!!, rb_add1_hazradType1)
            TextSizeUtil.resetText(activity!!, rb_add1_hazradType2)
            TextSizeUtil.resetText(activity!!, rb_add1_hazradType3)
            TextSizeUtil.resetText(activity!!, et_patrol_content)
            TextSizeUtil.resetText(activity!!, tv_content_name)
            TextSizeUtil.resetText(activity!!, tv_type_name)
            TextSizeUtil.resetText(activity!!, tv_pkiaa_name)
        }

        downLayout.visibility = View.VISIBLE
        ZXFragmentUtil.addFragment(childFragmentManager, AddFileFragment.newInstance().apply {
            addFileFragment = this
        }, R.id.fm_add_file)


        getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationTool.getLocation(activity!!) {
                if (it != null) {
                    latitude = it.latitude
                    longitude = it.longitude
                }
            }
        }
        if (mSharedPrefUtil.contains(ConstStrings.getPatrolData()) && mSharedPrefUtil.getObject<PatrolDetailBean>(ConstStrings.getPatrolData()) != null) {//判断是否存在保存的数据
            ZXDialogUtil.showYesNoDialog(activity!!, "提示", "存在未提交的上报信息，是否恢复？", "确认", "取消", { dialog, which ->
                patrolBean = mSharedPrefUtil.getObject(ConstStrings.getPatrolData())
                et_pkiaa.setText(patrolBean.pkiaa)
                setType(patrolBean.phase)
                latitude = patrolBean.latitude
                longitude = patrolBean.longitude
                et_patrol_content.setText(patrolBean.content)
                if (patrolBean.files != null)
                    addFileFragment.setData(patrolBean.files!!)
            }, { _, _ ->
                mSharedPrefUtil.remove(ConstStrings.getPatrolData())
                ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "PatrolFile/")
            })
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        btn_save.setOnClickListener {
            //暂存方法
            ZXDialogUtil.showYesNoDialog(activity!!, "提示", "是否暂存上报信息并返回上一级？") { dialog, which ->
                saveData()
                mSharedPrefUtil.putObject(ConstStrings.getPatrolData(), patrolBean)
                activity!!.finish()
            }
        }
        btn_submit.setOnClickListener {
            if (et_patrol_content.text.isEmpty())
                showToast("请输入巡查内容")
            else {

                if (!ZXNetworkUtil.isConnected()) {
                    ZXDialogUtil.showYesNoDialog(activity!!, "提示", "网络连接失败，是否暂存上报信息并返回上一级？") { dialog, which ->
                        btn_save.performClick()
                    }
                    return@setOnClickListener
                }
                ZXDialogUtil.showYesNoDialog(activity!!, "提示", "是否提交？") { dialog, which ->
                    mPresenter.submit(ApiParamUtil.submitPatrol(et_pkiaa.text.toString(), getType().toString(), longitude, latitude,
                            et_patrol_content.text.toString()))
                }
            }
            ZXSystemUtil.closeKeybord(activity!!)
        }
        //page2 隐患点编号查询
        tv_pkiaaSearch.setOnClickListener {
            showPikkaDialog(et_pkiaa.text.toString())
        }
    }


    fun onBackPressed() {
        if (btn_submit.visibility == View.GONE) {
            activity!!.finish()
            return
        }
        ZXDialogUtil.showWithOtherBtnDialog(activity!!, "提示", "还未保存信息，是否直接退出？", "保存",
                { _: DialogInterface, i: Int ->
                    if (mSharedPrefUtil.contains(ConstStrings.getPatrolData()) && mSharedPrefUtil.getObject<PatrolDetailBean>(ConstStrings.getPatrolData()) != null) {
                        mSharedPrefUtil.remove(ConstStrings.getPatrolData())
                        ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "PatrolFile/")
                    }
                    activity!!.finish()
                }, { _: DialogInterface, i: Int ->
            saveData()
            mSharedPrefUtil.putObject(ConstStrings.getPatrolData(), patrolBean)
            activity!!.finish()
        })
    }

    override fun onUploadFileResult() {
        showToast("提交成功！")
        uploadLog(302, 8, "提交巡查报告附件")
        mSharedPrefUtil.remove(ConstStrings.getPatrolData())
        ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "PatrolFile/")
        activity!!.finish()
    }

    override fun onUploadFilePregress(progress: Int) {
        handler.post {
            showLoading("正在上传中...", progress)
        }
    }

    /**
     * 提交回调
     */
    override fun onSubmitResult(recordid: String) {
        uploadLog(302, 3, "提交巡查报告")
        val files = arrayListOf<File>()
        if (addFileFragment.fileBeans.isNotEmpty()) {
            addFileFragment.fileBeans.forEach {
                val path = if (it.type == 1) {
                    it.path
                } else {
                    it.vedioPath
                }
                if (!path.startsWith("http")) {//只上传本地选择的文件
                    val file = File(path)
                    files.add(file)
                }
            }
        }
        if (files.isNotEmpty()) {
            mPresenter.uploadFile(files, recordid)
        } else {
            onUploadFileResult()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        addFileFragment.onActivityResult(requestCode, resultCode, data)
    }

    //隐患点查询
    private fun showPikkaDialog(str: String) {
        pkiaaList = arrayListOf()
        pkiaaAdapter = DisasterPkiaaListAdapter(pkiaaList!!)
        pkiaaPageNo = 1
        val pikkaView = LayoutInflater.from(activity!!).inflate(R.layout.layout_hazard_search_pkiaa, null, false)
        val etText = pikkaView!!.findViewById<EditText>(R.id.et_pkiaa_searchTxet)
        val ivSearch = pikkaView.findViewById<ImageView>(R.id.iv_pkiaa_searchBtn)
        val tvClose = pikkaView.findViewById<TextView>(R.id.tv_pkiaa_close)

        etText.setText(str)

        srPkiaaSearchList = pikkaView.findViewById(R.id.sr_pkiaa_searchList)
        srPkiaaSearchList!!.setLayoutManager(LinearLayoutManager(activity))
                .setAdapter(pkiaaAdapter)
                .autoLoadMore()
                .setPageSize(10)
                .setSRListener(object : ZXSRListener<DisasterPkiaaBean> {
                    override fun onItemLongClick(item: DisasterPkiaaBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pkiaaPageNo++
                        loadPkiaaData(etText.text.toString())
                    }

                    override fun onRefresh() {
                        loadPkiaaData(etText.text.toString(), true)
                    }

                    override fun onItemClick(item: DisasterPkiaaBean?, position: Int) {
                        if (item != null) {
                            et_pkiaa.setText(item.pkiaa)
                            ZXDialogUtil.dismissDialog()
                        }
                    }

                })
        etText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loadPkiaaData(etText.text.toString(), true)
                return@setOnEditorActionListener true
            }
            false
        }
        ivSearch.setOnClickListener { loadPkiaaData(etText.text.toString()) }
        tvClose.setOnClickListener { ZXDialogUtil.dismissDialog() }
        loadPkiaaData(etText.text.toString(),true)
        ZXDialogUtil.showCustomViewDialog(activity!!, "", pikkaView, null)
    }

    fun loadPkiaaData(searchText: String, refresh: Boolean = false) {
        if (refresh && srPkiaaSearchList != null) {
            pkiaaPageNo = 1
            srPkiaaSearchList!!.clearStatus()
        }
        mPresenter.getPkiaaList(ApiParamUtil.disasterPkiaaParam(pkiaaPageNo, searchText))
    }

    override fun onPkiaaListResult(pkiaaList: NormalList<DisasterPkiaaBean>?) {
        if (this.pkiaaList == null || this.pkiaaAdapter == null || this.srPkiaaSearchList == null) {
            return
        }
        srPkiaaSearchList!!.refreshData(pkiaaList!!.result, pkiaaList!!.totalRecords)

//        srPkiaaSearchList!!.stopRefresh()
//        if (pkiaaList?.result == null) {
//            srPkiaaSearchList!!.setLoadInfo(0)
//            this.pkiaaList!!.clear()
//        } else {
//            srPkiaaSearchList!!.setLoadInfo(pkiaaList.totalRecords)
//            this.pkiaaList!!.clear()
//            this.pkiaaList!!.addAll(pkiaaList.result!!)
//            srPkiaaSearchList!!.recyclerView.scrollToPosition(0)
//        }
//        srPkiaaSearchList!!.notifyDataSetChanged()
    }

    fun getType(): Int {
        if (rb_add1_hazradType1.isChecked)
            return 1
        else if (rb_add1_hazradType2.isChecked)
            return 2
        else (rb_add1_hazradType3.isChecked)
        return 3
    }

    fun setType(id: Int) {
        if (id == 1)
            rg_add1_hazardType.check(rb_add1_hazradType1.id)
        else if (id == 2)
            rg_add1_hazardType.check(rb_add1_hazradType2.id)
        else
            rg_add1_hazardType.check(rb_add1_hazradType3.id)
    }

    fun saveData() {
        patrolBean = PatrolDetailBean(et_pkiaa.text.toString(), getType(), et_patrol_content.text.toString(),
                latitude, longitude, addFileFragment.fileBeans)
    }
}

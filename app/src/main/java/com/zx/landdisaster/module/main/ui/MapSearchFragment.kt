package com.zx.landdisaster.module.main.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.EditorInfo
import com.esri.android.map.GraphicsLayer
import com.esri.core.geometry.Point
import com.esri.core.geometry.Polygon
import com.esri.core.map.Graphic
import com.esri.core.symbol.PictureMarkerSymbol
import com.esri.core.symbol.TextSymbol
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.disaster.ui.HiddenPointActivity
import com.zx.landdisaster.module.disaster.ui.ReportDetailActivity
import com.zx.landdisaster.module.main.bean.SearchBean
import com.zx.landdisaster.module.main.func.adapter.SearchResultAdapter
import com.zx.landdisaster.module.main.func.listener.MapListener
import com.zx.landdisaster.module.main.mvp.contract.MapSearchContract
import com.zx.landdisaster.module.main.mvp.model.MapSearchModel
import com.zx.landdisaster.module.main.mvp.presenter.MapSearchPresenter
import com.zx.landdisaster.module.system.ui.UserActivity
import com.zx.zxutils.util.ZXDeviceUtil
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXSystemUtil
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.fragment_map_search.*
import java.util.*

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MapSearchFragment : BaseFragment<MapSearchPresenter, MapSearchModel>(), MapSearchContract.View {

    private var pageNo = 0

    private var isExpand = true

    private var mSearchText: String? = null

    private val searchList = ArrayList<SearchBean>()
    private var mAdapter = SearchResultAdapter(searchList)
    private lateinit var mapListener: MapListener

    private var searchMarkerLayer: GraphicsLayer? = null
    private var locationMarkerLayer: GraphicsLayer? = null


    private var searchIcon1: PictureMarkerSymbol? = null
    private var searchIcon2: PictureMarkerSymbol? = null
    private var searchIcon3: PictureMarkerSymbol? = null
    private var searchIcon5: PictureMarkerSymbol? = null


    companion object {
        /**
         * 启动器
         */
        fun newInstance(): MapSearchFragment {
            val fragment = MapSearchFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_map_search
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        if (getUserType() == 1) {
            iv_search_cancel.background = ContextCompat.getDrawable(mContext, R.drawable.icon_user)
        } else {
            iv_search_cancel.background = ContextCompat.getDrawable(mContext, R.drawable.icon_home)
        }
        searchIcon1 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_1_hdpi else R.drawable.normal_icon_1))
        searchIcon2 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_2_hdpi else R.drawable.normal_icon_2))
        searchIcon3 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_3_hdpi else R.drawable.normal_icon_3))
        searchIcon5 = PictureMarkerSymbol(ContextCompat.getDrawable(activity!!, if (ZXDeviceUtil.isTablet()) R.drawable.normal_icon_5_hdpi else R.drawable.normal_icon_5))

//        if (UserManager.getUser().personRole.expert) {
//            expertLayout.visibility = View.VISIBLE
//            notExpertLayout.visibility = View.GONE
//
//            iv_search_cancel1.setOnClickListener {
//                userClick()
//            }
//        }
        sr_search_list.setLayoutManager(LinearLayoutManager(activity))
                .setAdapter(mAdapter)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<SearchBean> {
                    override fun onItemLongClick(item: SearchBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()
                    }

                    override fun onRefresh() {
                        pageNo = 1
                        loadData()
                    }

                    override fun onItemClick(item: SearchBean?, position: Int) {
                        if (item != null) {
                            onItemClick(position)
                        }
                    }

                })
        loadData()
    }

    fun userClick() {
        if (getUserType() == 1) {
            UserActivity.startAction(mActivity, false)
        } else {
            mActivity.finish()
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        iv_search_cancel.setOnClickListener {
            if (iv_search_cancel.tag == "close") {
                et_search_text.setText("")
                iv_search_cancel.tag = "user"
                if (getUserType() == 1) {
                    iv_search_cancel.background = ContextCompat.getDrawable(mContext, R.drawable.icon_user)
                } else {
                    iv_search_cancel.background = ContextCompat.getDrawable(mContext, R.drawable.icon_home)
                }
                card_search_result.visibility = View.GONE
                ZXSystemUtil.closeKeybord(activity)
                searchList.clear()
                drawSearchPointToMap()
                sr_search_list.setLoadInfo(0)
                mAdapter.notifyDataSetChanged()
            } else {
                userClick()
            }
        }
        ll_search_catalog.setOnClickListener {
            expandResult(!isExpand)
        }

        et_search_text.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                ZXSystemUtil.closeKeybord(activity)
                mSearchText = et_search_text.text.toString()
                loadData()
                return@setOnEditorActionListener true
            }
            false
        }
        et_search_text.setOnTouchListener { _, _ ->
            if (card_search_result.visibility == View.GONE) {
                card_search_result.visibility = View.VISIBLE
                expandResult(true)
                iv_search_cancel.tag = "close"
                iv_search_cancel.background = ContextCompat.getDrawable(mContext, R.drawable.icon_close)
            }
            false
        }
    }

    private fun expandResult(expand: Boolean) {
        isExpand = expand
        sr_search_list.recyclerView.scrollToPosition(0)
        if (expand) {
            sr_search_list.visibility = View.VISIBLE
            iv_search_catalog.background = ContextCompat.getDrawable(mContext, R.drawable.catalog_close)
        } else {
            sr_search_list.visibility = View.GONE
            iv_search_catalog.background = ContextCompat.getDrawable(mContext, R.drawable.catalog_expand)
        }
    }

    private fun loadData(reload: Boolean = false) {
        if (reload) {
            pageNo = 1
            sr_search_list.clearStatus()
        }
        if (et_search_text.text.isNotEmpty()) {
            uploadLog(308, 1, "搜索：${et_search_text.text}")
            mPresenter.doSearch(ApiParamUtil.searchListParam(pageNo, pageSize, et_search_text.text.toString()))
        } else {
            sr_search_list.stopRefresh()
        }
    }

    override fun onSearchResult(list: NormalList<SearchBean>?) {
        card_search_result.visibility = View.VISIBLE
        if (list != null) {
            sr_search_list.refreshData(list.result!!, list.totalRecords)
        } else {
            mAdapter.emptyView = EmptyViewTool.getInstance(activity!!).getEmptyView { loadData() }
        }

//        sr_search_list.stopRefresh()
//        if (searchList?.result == null) {
//            sr_search_list.setLoadInfo(0)
//            this.searchList.clear()
//        } else {
//            sr_search_list.setLoadInfo(searchList.totalRecords)
//            this.searchList.clear()
//            this.searchList.addAll(searchList.result!!)
//            sr_search_list.recyclerView.scrollToPosition(0)
//        }
//        sr_search_list.notifyDataSetChanged()
        drawSearchPointToMap()

    }

    /**
     * 在地图上绘制点
     */
    private fun drawSearchPointToMap() {
        val mapView = mapListener.getMapView()
        if (searchMarkerLayer == null) {
            searchMarkerLayer = GraphicsLayer()
            mapView.addLayer(searchMarkerLayer)
        }
        if (locationMarkerLayer == null) {
            locationMarkerLayer = GraphicsLayer()
            mapView.addLayer(locationMarkerLayer)
        }
        locationMarkerLayer!!.removeAll()
        searchMarkerLayer!!.removeAll()
        val graphicList = arrayListOf<Graphic>()//graphic集合
        var polygon: Polygon? = null
        if (searchList.isNotEmpty()) {
            for (i in searchList.indices) {
                val bean = searchList[i]
                val picSymbol = when (bean.type) {
                    2 -> searchIcon2
                    3 -> searchIcon3
                    else -> null
                }
                if (picSymbol != null && bean.x != null && bean.y != null) {
                    val textSymbol = TextSymbol(12, (i + 1).toString(), Color.WHITE)
                    textSymbol.offsetX = -4f
                    textSymbol.offsetY = -7f
                    graphicList.add(Graphic(Point(bean.x!!, bean.y!!), picSymbol, i))
                    graphicList.add(Graphic(Point(bean.x!!, bean.y!!), textSymbol, i))
                    if (polygon == null) {
                        polygon = Polygon()
                        polygon.startPath(Point(bean.x!!, bean.y!!))
                    } else {
                        polygon.lineTo(Point(bean.x!!, bean.y!!))
                    }
                }
            }
            if (polygon != null && polygon.pointCount > 1) {
                polygon.closeAllPaths()
            }
        }
        searchMarkerLayer!!.addGraphics(graphicList.toTypedArray())
        if (graphicList.isNotEmpty() && polygon != null && polygon.pointCount > 1) {
            mapView.setExtent(polygon, 100, true)
        }
    }

    fun setMapListener(mapListener: MapListener) {
        this.mapListener = mapListener
        mapListener.addSingleTagListener(object : MapFragment.MySingleTapListener {
            override fun onSingleTap(x: Float, y: Float) {
                if (searchMarkerLayer != null && searchMarkerLayer!!.isVisible) {
                    val ids = searchMarkerLayer!!.getGraphicIDs(x, y, 1)
                    if (ids != null && ids.isNotEmpty()) {
                        val graphic = searchMarkerLayer!!.getGraphic(ids[0])
                        if (graphic != null) {
                            onItemClick(graphic.drawOrder)
                        }
                    }
                }
            }

        })
    }

    /**
     * 列表及地图点击事件
     */
    private fun onItemClick(position: Int) {
        if (locationMarkerLayer != null) {
            locationMarkerLayer!!.removeAll()
        }
        when (searchList[position].type) {
            1 -> {//基础
                if (searchList[position].id.isNotEmpty()) {
                    HiddenPointActivity.startAction(activity!!, false, searchList[position].id.substring(1))
                    uploadLog(308, 3, "查看隐患点详情：${searchList[position].id.substring(1)}")
                }
            }
            2 -> {//灾情
                if (searchList[position].id.isNotEmpty()) {
                    ReportDetailActivity.startAction(activity!!, searchList[position].id.substring(1), false)
                    uploadLog(308, 4, "查看灾险情详情：${searchList[position].id.substring(1)}")
                }
            }
            3 -> {//险情
                if (searchList[position].id.isNotEmpty()) {
                    ReportDetailActivity.startAction(activity!!, searchList[position].id.substring(1), false)
                    uploadLog(308, 4, "查看灾险情详情：${searchList[position].id.substring(1)}")
                }
            }
            4 -> {//人员
                ZXDialogUtil.showListDialog(activity!!, "", "关闭", arrayListOf(
                        "名称：${searchList[position].name}",
                        "地址：${searchList[position].addr}",
                        "区县：${searchList[position].key1}",
                        "身份：${UserManager.getUserRoleName(searchList[position].key2)}",
                        "单位 ：${searchList[position].key3}",
                        "电话 ：${searchList[position].key4}"
                )) { _, which ->
                    if (which == 5 && searchList[position].key4.isNotEmpty()) {
                        ZXSystemUtil.callTo(activity, searchList[position].key4)
                    }
                }
                uploadLog(308, 2, "查看人员详情：${searchList[position].name}")
            }
            5 -> {//位置
                if (searchList[position].x != null && searchList[position].y != null) {
                    val locationSymbol = searchIcon5
                    locationMarkerLayer!!.addGraphic(Graphic(Point(searchList[position].x!!, searchList[position].y!!), locationSymbol))
                    mapListener.getMapView().zoomTo(Point(searchList[position].x!!, searchList[position].y!!), 100000.0f)
                    expandResult(false)
                }
            }
        }
    }
}

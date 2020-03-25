package com.zx.landdisaster.module.other.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.disaster.func.adapter.AddFileAdapter
import com.zx.landdisaster.module.disaster.func.util.FileUriUtil
import com.zx.landdisaster.module.other.mvp.contract.AddFileContract
import com.zx.landdisaster.module.other.mvp.model.AddFileModel
import com.zx.landdisaster.module.other.mvp.presenter.AddFilePresenter
import com.zx.zxutils.other.ZXInScrollRecylerManager
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFileUtil
import kotlinx.android.synthetic.main.fragment_add_file.*
import java.io.File

/**
 * Create By admin On 2017/7/11
 * 功能：文件添加fragment
 */
class AddFileFragment : BaseFragment<AddFilePresenter, AddFileModel>(), AddFileContract.View {

    val deleteFiles = arrayListOf<AddFileBean>()//被删除的网络文件记录
    val fileBeans = arrayListOf<AddFileBean>()
    private val fileAdapter = AddFileAdapter(fileBeans) {
        val path = if (fileBeans[it].type == 1) {
            fileBeans[it].path
        } else {
            fileBeans[it].vedioPath
        }
        if (path.startsWith("http")) {
            deleteFiles.add(fileBeans[it])
        }
        fileBeans.removeAt(it)
        eidtCallBack(FileAction.FDELETE)
    }
    private var eidtCallBack: (FileAction) -> Unit = {}
    private var maxFileNum = 10
    private var fileType = FileType.NORMAL

    enum class FileAction {
        FADD,
        FDELETE
    }

    enum class FileType {
        VEDIO,
        CAMERA,
        CAMERA_VEDIO,
        NORMAL,
        JUST_SHOW
    }

    companion object {
        /**
         * 启动器
         */
        fun newInstance(fileType: FileType = FileType.NORMAL, maxFileNum: Int = 10): AddFileFragment {
            val fragment = AddFileFragment()
            val bundle = Bundle()
            bundle.putSerializable("fileType", fileType)
            bundle.putInt("maxFileNum", maxFileNum)
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_add_file
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        fileType = arguments!!.getSerializable("fileType") as FileType
        if (fileType == FileType.JUST_SHOW) {
            setCanEdit(false)
        }
        maxFileNum = arguments!!.getInt("maxFileNum")
        rv_fileList.apply {
            layoutManager = ZXInScrollRecylerManager(activity) as RecyclerView.LayoutManager?
            adapter = fileAdapter
        }
    }

    /**
     * 设置数据
     */
    fun setData(files: ArrayList<AddFileBean>) {
        fileBeans.clear()
        fileBeans.addAll(files)
        fileAdapter.notifyDataSetChanged()
    }

    /**
     * 设置是否可编辑
     */
    fun setCanEdit(canEdit: Boolean) {
        if (ll_selectFile == null) {
            handler.postDelayed({
                ll_selectFile.visibility = if (canEdit) View.VISIBLE else View.GONE
                fileAdapter.canDelete = canEdit
                fileAdapter.notifyDataSetChanged()
            }, 300)
        } else {
            ll_selectFile.visibility = if (canEdit) View.VISIBLE else View.GONE
            fileAdapter.canDelete = canEdit
            fileAdapter.notifyDataSetChanged()
        }
    }

    /**
     * 编辑的回调
     */
    fun setOnEditCallBack(eidtCallBack: (AddFileFragment.FileAction) -> Unit) {

        this.eidtCallBack = eidtCallBack
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //文件选择事件
        tv_selectFile.setOnClickListener {
            if (fileBeans.size >= maxFileNum) {
                showToast("文件数量已达上限！")
            } else {
                when (fileType) {
                    FileType.NORMAL -> {
                        ZXDialogUtil.showListDialog(activity!!, "选取方式", "关闭", arrayOf("拍摄/录像", "本地文件选择")) { dialog, which ->
                            if (which == 0) {
                                getPermission(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                                    CameraVedioActivity.startAction(activity!!, false)
                                }
                            } else {
                                getPermission(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                                    intent.type = "*/*"//设置类型，我这里是任意类型，任意后缀的可以这样写。
                                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                                    startActivityForResult(intent, 0x03)
                                }
                            }
                        }
                    }
                    FileType.CAMERA_VEDIO -> {
                        getPermission(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                            CameraVedioActivity.startAction(activity!!, false)
                        }
                    }
                    FileType.CAMERA -> {
                        getPermission(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                            CameraVedioActivity.startAction(activity!!, false, 1)
                        }
                    }
                    FileType.VEDIO -> {
                        getPermission(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                            CameraVedioActivity.startAction(activity!!, false, 2)
                        }
                    }
                }
            }
        }
        //文件预览事件
        fileAdapter.setOnItemClickListener { adapter, view, position ->
            if (fileBeans[position].type == 1) {
                FilePreviewActivity.startAction(activity!!, false, fileBeans[position].name, fileBeans[position].path)
            } else if (fileBeans[position].type == 2) {
                FilePreviewActivity.startAction(activity!!, false, fileBeans[position].name, fileBeans[position].vedioPath)
            } else {
                if (!fileBeans[position].path.startsWith("http")) {
                    ZXFileUtil.openFile(activity!!, File(fileBeans[position].path))
                } else if (!fileBeans[position].path.endsWith("null", true)) {
                    getPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mPresenter.downloadFile(fileBeans[position].path)
                    }
                } else {
                    showToast("该文件暂时无法下载")
                }
            }
        }
    }

    override fun onFileDownloadResult(file: File) {
        ZXFileUtil.openFile(activity!!, file)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x03) {//文件选取
            if (data != null) {
                val uri = data.getData()
                val filePath = FileUriUtil.getFileAbsolutePath(activity!!, uri)
                if (filePath == null) {
                    showToast("获取文件路径失败，请重试")
                } else {
                    val type = filePath.substring(filePath.lastIndexOf(".") + 1)
                    if (type !in arrayOf("jpg", "png", "gif", "bmp", "psd", "tiff", "tga", "eps",
                                    "mp4", "rmvb", "avi", "mp3", "wma", "wav", "pdf", "xls", "txt",
                                    "doc", "docx", "ppt", "xlsx", "pptx", "cad", "csv", "dwg", "odt")) {
                        showToast("当前文件不符合规范，请重新选择")
                        return
                    }
                    val fileName = filePath.substring(filePath.lastIndexOf("/") + 1)

                    val f = AddFileBean.reSetType(null, fileName, filePath)

                    for (i in 0 until fileBeans.size) {
                        if (f == fileBeans[i]) {
                            showToast("文件已存在")
                            return
                        }
                    }
                    fileBeans.add(f)
                    fileAdapter.notifyDataSetChanged()
                    eidtCallBack(FileAction.FADD)
                }
            }
        } else if (resultCode == 0x02) {//拍摄-录像
            if (data != null) {
                val fileBean = AddFileBean(data.getStringExtra("name"), data.getIntExtra("type", 1), data.getStringExtra("path"), data.getStringExtra("vedioPath"))
                fileBeans.add(fileBean)
                fileAdapter.notifyDataSetChanged()
                eidtCallBack(FileAction.FADD)
            }
        }
    }

}

package com.zx.landdisaster.base.other

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.zx.landdisaster.R
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXSystemUtil

/**
 * Created by Xiangb on 2019/3/5.
 * 功能：顶部状态栏
 */
class ToolBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var toolbarBack: LinearLayout//返回按钮
    private lateinit var toolbarBackText: TextView//返回文字
    private lateinit var toolbarBackPic: ImageView//返回图片
    private lateinit var toolbarMidPic: ImageView//中间图片
    private lateinit var toolbarTitle: TextView//中间标题
    private lateinit var toolbarLeftImage: ImageView//左侧图片
    private lateinit var toolbarRightImage: ImageView//右侧图片
    private lateinit var toolbarRightText: TextView//右侧文字
    private lateinit var toolbarItem: RelativeLayout
    private var titleText = ""
    private var backText = "返回"
    private var showLeftBack = true//是否展示左侧返回按钮
    private var showLeftPic = false//是否展示左侧图片按钮
    private var showRightPic = false//是否展示右侧按钮
    private var showRightText = false//是否展示右侧文字
    private var midPic: Drawable? = null//中间图片
    private var rightPic: Drawable? = null//右侧图片
    private var leftPic: Drawable? = null//左侧图片
    private var rightText: String = "保存"//右侧文字

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ToolBarView, defStyleAttr, 0)
        titleText = if (typedArray.hasValue(R.styleable.ToolBarView_title_text)) typedArray.getString(R.styleable.ToolBarView_title_text) else ""
        backText = if (typedArray.hasValue(R.styleable.ToolBarView_back_text)) typedArray.getString(R.styleable.ToolBarView_back_text) else ""
        midPic = typedArray.getDrawable(R.styleable.ToolBarView_midpic_bg)
        leftPic = typedArray.getDrawable(R.styleable.ToolBarView_leftpic_bg)
        rightPic = typedArray.getDrawable(R.styleable.ToolBarView_rightpic_bg)
        rightText = if (typedArray.hasValue(R.styleable.ToolBarView_right_text)) typedArray.getString(R.styleable.ToolBarView_right_text) else ""
        showLeftBack = typedArray.getBoolean(R.styleable.ToolBarView_show_leftback, true)
        showLeftPic = typedArray.getBoolean(R.styleable.ToolBarView_show_leftpic, false)
        showRightPic = typedArray.getBoolean(R.styleable.ToolBarView_show_rightpic, false)
        showRightText = typedArray.getBoolean(R.styleable.ToolBarView_show_righttext, false)
        showRightPic = if (showRightText) false else showRightPic
        showLeftPic = if (showLeftBack) false else showLeftPic
        init(context)
    }

    private fun init(context: Context) {
        inflate(context, R.layout.layout_tool_bar, this)
        toolbarItem = findViewById(R.id.tool_bar_item)
        toolbarBack = findViewById(R.id.tool_bar_back)
        toolbarBackText = findViewById(R.id.tool_bar_backText)
        toolbarBackPic = findViewById(R.id.tool_bar_back_pic)
        toolbarMidPic = findViewById(R.id.tool_bar_midpic)
        toolbarTitle = findViewById(R.id.tool_bar_title)
        toolbarLeftImage = findViewById(R.id.tool_bar_leftImage)
        toolbarRightImage = findViewById(R.id.tool_bar_rightImage)
        toolbarRightText = findViewById(R.id.tool_bar_rightText)

        toolbarTitle.text = titleText
        toolbarMidPic.visibility = if (midPic == null) View.GONE else View.VISIBLE
        toolbarMidPic.background = midPic
        toolbarBack.visibility = if (showLeftBack) View.VISIBLE else View.GONE
        toolbarRightImage.visibility = if (showRightPic) View.VISIBLE else View.GONE
        toolbarLeftImage.visibility = if (showLeftPic) View.VISIBLE else View.GONE
        toolbarRightText.visibility = if (showRightText) View.VISIBLE else View.GONE
        if (rightPic != null) toolbarRightImage.background = rightPic
        if (leftPic != null) toolbarLeftImage.background = leftPic
        toolbarRightText.text = rightText

        toolbarBack.setOnClickListener { v -> (context as Activity).onBackPressed() }
        toolbarRightImage.setOnClickListener { v ->
            if (showRightPic && (UserManager.getUser().personRole.areaManager || UserManager.getUser().personRole.groupDefense)) {
                ZXDialogUtil.showYesNoDialog(context, "提示", "是否要发起电话帮助?") { dialog, which ->
                    ZXSystemUtil.callTo(context, resources.getString(R.string.toolbar_tel_string))
                }
            }
        }
    }

    /**
     * 设置右侧按钮点击事件
     */
    fun setRightClickListener(onClick: () -> Unit) {
        toolbarRightImage.setOnClickListener { onClick() }
        toolbarRightText.setOnClickListener { onClick() }
    }

    /**
     * 设置左侧按钮点击事件
     */
    fun setLeftClickListener(onClick: () -> Unit) {
        toolbarBack.setOnClickListener { onClick() }
        toolbarLeftImage.setOnClickListener { onClick() }
    }

    /**
     * 设置中间文字
     */
    fun setMidText(titleText: String) {
        toolbarTitle.text = titleText
        toolbarTitle.setSingleLine(true)
        toolbarTitle.isSelected = true
        toolbarTitle.isFocusable = true
        toolbarTitle.isFocusableInTouchMode = true
        toolbarTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
    }

    fun showRightImg(@DrawableRes imgRes: Int) {
        toolbarRightImage.visibility = View.VISIBLE
        toolbarRightImage.setBackgroundResource(imgRes)
    }

    fun showRightImg() {
        toolbarRightImage.visibility = View.VISIBLE
    }

    fun showLeftImg(@DrawableRes imgRes: Int) {
        toolbarLeftImage.visibility = View.VISIBLE
        toolbarLeftImage.setBackgroundResource(imgRes)
    }

    fun showLeftImg() {
        toolbarLeftImage.visibility = View.VISIBLE
    }

    fun showRightText() {
        toolbarRightText.visibility = View.VISIBLE
    }

    fun showRightText(text: String) {
        toolbarRightText.visibility = View.VISIBLE
        toolbarRightText.setText(text)
    }

    /**
     * 设置中间图片
     */
    fun setMidPic(@DrawableRes imgRes: Int) {
        toolbarMidPic.visibility = View.VISIBLE
        toolbarMidPic.background = ContextCompat.getDrawable(context, imgRes)
    }
}
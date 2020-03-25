package com.zx.landdisaster.module.main.func.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * @E-mail：1160942652@qq.com
 * @作者：zhoucan
 * @创建时间：2019.06.12 17:35
 * @类名：
 */
public class CustomScrollview extends ScrollView {
    private float mLastXIntercept = 0f;
    private float mLastYIntercept = 0f;

    public CustomScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        float x = ev.getX();
        float y = ev.getY();
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                intercepted = false; //初始化mActivePointerId
                super.onInterceptTouchEvent(ev);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //横坐标位移增量
                float deltaX = x - mLastXIntercept;
                //纵坐标位移增量
                float deltaY = y - mLastYIntercept;
                if (Math.abs(deltaX) < Math.abs(deltaY)) {
                    intercepted = true;
                } else {
                    intercepted = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                intercepted = false;
                break;
            }
        }
        mLastXIntercept = x;
        mLastYIntercept = y;
        return intercepted;
    }

}

package com.ezreal.huanting.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;


public class ReNestedScrollView extends NestedScrollView {

    private ScrollInterface scrollInterface;

    /**
     * 定义滑动接口
     */
    public interface ScrollInterface {
        void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

    public ReNestedScrollView(Context context) {
        super(context);
    }

    public ReNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (scrollInterface != null) {
            scrollInterface.onScrollChange(l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnMyScrollChangeListener(ScrollInterface t) {
        this.scrollInterface = t;
    }
}

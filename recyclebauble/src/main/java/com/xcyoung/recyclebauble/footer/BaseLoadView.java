package com.xcyoung.recyclebauble.footer;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

public abstract class BaseLoadView extends LinearLayout implements BaseLoadFooter {
    private static final String TAG=BaseLoadView.class.getSimpleName();
    protected View container;
    public BaseLoadView(Context context) {
        this(context,null);
    }

    public BaseLoadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        container=getContainerView();
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);
        addView(container, new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.setVisibility(GONE);
    }

    /**
     * 设置当前状态
     * @param state
     */
    public void setState(int state){
        switch (state){
            case STATE_LOADING:
                onLoading();
                switchVisible(true);
                break;
            case STATE_COMPLETE:
                onCompelete();
                switchVisible(false);
                break;
            case STATE_NOMORE:
                onNoMore();
//                this.setVisibility(GONE);
                switchVisible(false);
                break;
        }
    }

    @Override
    public void loading() {

    }

    @Override
    public void loadCompelete() {

    }

    @Override
    public void noMore() {

    }

    /**
     * 可见属性设置 使用动画制造延迟
     * @param isVisible
     */
    private void switchVisible(boolean isVisible){
        ObjectAnimator objectAnimator=ObjectAnimator.ofInt(this,"visibility",isVisible?0x00000000:0x00000008);
        objectAnimator.setDuration(1000).start();
    }

    //规定获取的整个View布局
    protected abstract View getContainerView();
    //正在加载时的ui
    protected abstract void onLoading();
    //加载完成时的ui
    protected abstract void onCompelete();
    //无更多数据的ui
    protected abstract void onNoMore();

}

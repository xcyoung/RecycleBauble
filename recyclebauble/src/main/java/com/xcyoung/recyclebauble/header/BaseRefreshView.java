package com.xcyoung.recyclebauble.header;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public abstract class BaseRefreshView extends LinearLayout implements BaseRefreshHeader {
    private static final String TAG=BaseRefreshView.class.getSimpleName();
//    protected View container;

    protected int mMeasuredHeight;          //下拉刷新的阈值
    protected int currentState = STATE_NORMAL;
    public BaseRefreshView(Context context) {
        this(context,null);
    }

    public BaseRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        getContainerView();
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);
//        addView(container, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
//        setGravity(Gravity.BOTTOM);

        measure(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
        Log.i(TAG,"MeasuredHeight:"+mMeasuredHeight);
//        Log.i(TAG,"MeasuredWidth:"+getMeasuredWidth());
    }

    /**
     * 设置当前状态
     * @param state
     */
    public void setState(int state){
        if(state==currentState) return;         //若状态和当前标志状态相同则不作处理

        switch (state){
            case STATE_NORMAL:
                onNormal();
                break;
            case STATE_RELEASE_TO_REFRESH:
                onReleaseRefresh();
                break;
            case STATE_REFRESHING:
                onRefreshing();
                changeVisiableHeight(mMeasuredHeight);      //正在加载状态 自动移动到阈值
                break;
            case STATE_DONE:
                onRefreshDone();
                break;
            case STATE_ERROR:
                onRefreshError();
                break;
        }

        currentState=state;         //更新当前状态
    }

    /**
     * 获取当前状态
     * @return
     */
    public int getState() {
        return currentState;
    }

    @Override
    public void onMove(float delta) {
        if(getVisiableHeight()>0 || delta>0){
            setVisiableHeight((int) delta+getVisiableHeight());
            onMoveUI((delta+getVisiableHeight())/mMeasuredHeight,delta);
            if(currentState<=BaseRefreshHeader.STATE_RELEASE_TO_REFRESH){           //状态处于正常或释放刷新的话则未处于刷新状态考虑更新ui
                if(getVisiableHeight() > mMeasuredHeight){
                    setState(STATE_RELEASE_TO_REFRESH);
                }else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh=false;      //用于确认是否回调onRefresh监听的标志
        int height=getVisiableHeight();
        if(height==0) isOnRefresh=false;

        //可见高度比阈值大且状态未更新到正在刷新则视为开始刷新
        if(height>mMeasuredHeight&&currentState<STATE_REFRESHING){
            setState(STATE_REFRESHING);
            isOnRefresh=true;
        }
        //如果状态刚好是正在刷新而高度又比阈值小或相等则不需作处理
        if(currentState==STATE_REFRESHING&&height<=mMeasuredHeight){

        }

        //主要针对可见高度比阈值小而又不是正在刷新状态则被视为未到阈值不作刷新处理，自动回弹
        if(currentState!=STATE_REFRESHING) changeVisiableHeight(0);

        //如果刚好是正在刷新状态则将视图移动到阈值的高度，作为高度的修正
        if(currentState==STATE_REFRESHING){
            int destHeight=mMeasuredHeight;
            changeVisiableHeight(destHeight);
        }

        return isOnRefresh;
    }

    @Override
    public void refreshComplete(boolean isSuccess) {
        setState(isSuccess?STATE_DONE:STATE_ERROR);
        reset();
    }

    protected void reset(){
        new Handler().postDelayed(new Runnable() {      //刷新完成0.5s后重置状态为正常状态
            @Override
            public void run() {
                changeVisiableHeight(0);
                setState(BaseRefreshHeader.STATE_NORMAL);
            }
        },1000);
    }

    /**
     * 使用属性动画过渡可见高度的改变
     * @param destHeight
     */
    private void changeVisiableHeight(int destHeight){
        ValueAnimator valueAnimator=ValueAnimator.ofInt(getVisiableHeight(),destHeight);
        valueAnimator.setDuration(500).start();         //过渡时长为0.5s
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisiableHeight((int) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }

    /**
     * 设置当前可见高度
     * @param visiableHeight
     */
    public void setVisiableHeight(int visiableHeight){
        if(visiableHeight<0) visiableHeight=0;
        ViewGroup.LayoutParams layoutParams= this.getLayoutParams();
        layoutParams.height=visiableHeight;
        this.setLayoutParams(layoutParams);
    }

    /**
     * 获取当前可见高度
     */
    public int getVisiableHeight(){
        ViewGroup.LayoutParams layoutParams= this.getLayoutParams();
        return layoutParams.height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onMoveUI(float progress,float delta){
        //do nothing
    }

    protected void setMeasuredHeight(){
        //do nothing
    }

    //规定获取的整个View布局
    protected abstract void getContainerView();
    //正常未刷新时的ui
    protected abstract void onNormal();
    //释放刷新时的ui
    protected abstract void onReleaseRefresh();
    //正在刷新的ui
    protected abstract void onRefreshing();
    //刷新完成后的ui
    protected abstract void onRefreshDone();
    //刷新失败后的ui
    protected abstract void onRefreshError();
}

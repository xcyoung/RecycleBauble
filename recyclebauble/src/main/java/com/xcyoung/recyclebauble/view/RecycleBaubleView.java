package com.xcyoung.recyclebauble.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.xcyoung.recyclebauble.adapter.Warpper;
import com.xcyoung.recyclebauble.footer.BaseLoadView;
import com.xcyoung.recyclebauble.footer.DefalutLoadView;
import com.xcyoung.recyclebauble.header.BaseRefreshView;
import com.xcyoung.recyclebauble.header.DefalutRefreshView;

public class RecycleBaubleView extends RecyclerView {
    private static final String TAG=RecycleBaubleView.class.getSimpleName();
    private BaseRefreshView refreshView;                     //顶部刷新
    private BaseLoadView loadView;                           //底部加载
    private View emptyView;                                  //空布局

    private Warpper warpper;                                 //装饰器
    private float lastY=-1;                                  //用于记录最新的y坐标
    private boolean isRefreshEnabled=true;                   //是否支持下拉刷新
    private boolean isLoadEnabled=true;                      //是否支持上拉加载
    private boolean isLoading=false;                         //是否正在上拉加载
//    private boolean isEmpty=false;                           //是否为空数据状态

    private Observer observer=new Observer();                //观察者
    private onLoadingListener onLoadingListener;

    public void setOnLoadingListener(RecycleBaubleView.onLoadingListener onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
    }

    public RecycleBaubleView(Context context) {
        this(context,null);
    }

    public RecycleBaubleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecycleBaubleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        if(isRefreshEnabled){
            refreshView=new DefalutRefreshView(context);
        }
        if(isLoadEnabled){
            loadView=new DefalutLoadView(context);
        }
    }

    /**
     * 开启或关闭刷新效果
     * @param isRefresh
     */
    public void setRefresh(boolean isRefresh){
        if(isRefreshEnabled&&refreshView!=null) {
            if (isRefresh) {
                refreshView.setState(refreshView.STATE_REFRESHING);
                if(onLoadingListener!=null) onLoadingListener.onRefresh();
            } else {
                refreshView.refreshComplete(true);
                setLoadCompelete(false);
            }
        }
    }

    /**
     * 当刷新失败的时候调用
     */
    public void setRefreshError(){
        if(isRefreshEnabled&&refreshView!=null) {
            refreshView.refreshComplete(false);
            setLoadCompelete(false);
        }
    }

    /**
     * 设置上拉加载完成
     * @param isNoMore   设置是否有更多参数
     */
    public void setLoadCompelete(boolean isNoMore){
        if(isLoadEnabled&&loadView!=null){
            isLoading=false;
            loadView.setState(isNoMore?loadView.STATE_NOMORE:loadView.STATE_COMPLETE);
        }
    }

    /**
     * 设置是否可下拉刷新
     * @param refreshEnabled
     */
    public void setRefreshEnabled(boolean refreshEnabled) {
        isRefreshEnabled = refreshEnabled;
        if(!isRefreshEnabled) refreshView = null;
    }

    public void setRefreshView(BaseRefreshView refreshView){
        this.refreshView=refreshView;
    }

    public void setLoadView(BaseLoadView loadView) {
        this.loadView = loadView;
    }

    /**
     * 设置是否可上拉加载
     * @param loadEnabled
     */
    public void setLoadEnabled(boolean loadEnabled) {
        isLoadEnabled = loadEnabled;
        if(!isLoadEnabled) loadView = null;
    }

    /**
     * 设置空布局
     * @param emptyView
     */
    public void setEmptyView(View emptyView){
        this.emptyView=emptyView;
        ((ViewGroup)getRootView()).addView(emptyView
                ,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 全部横向布局禁用头部和尾部
     * @param layout
     */
    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if(layout instanceof GridLayoutManager){
            if(((GridLayoutManager) layout).getOrientation() == GridLayoutManager.HORIZONTAL) {
                setRefreshEnabled(false);
                setLoadEnabled(false);
            }
        }else if(layout instanceof LinearLayoutManager){
            if(((LinearLayoutManager) layout).getOrientation() == LinearLayoutManager.HORIZONTAL){
                setRefreshEnabled(false);
                setLoadEnabled(false);
            }
        }else if(layout instanceof StaggeredGridLayoutManager){
            if(((StaggeredGridLayoutManager) layout).getOrientation() == StaggeredGridLayoutManager.HORIZONTAL){
                setRefreshEnabled(false);
                setLoadEnabled(false);
            }
        }
    }

    /**
     * 滑动状态的改变
     * @param state
     */
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        //RecyclerView.SCROLL_STATE_IDLE:静止没有滚动
        if(state == RecyclerView.SCROLL_STATE_IDLE && onLoadingListener!=null && isLoadEnabled && !isLoading){
            LayoutManager layoutManager=getLayoutManager();             //获取当前的layoutmanager
            //屏幕中最后一个可见子项的position
            int lastVisibleItemPosition = 0;
            if(layoutManager instanceof LinearLayoutManager){
                lastVisibleItemPosition=((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }else if(layoutManager instanceof GridLayoutManager){
                lastVisibleItemPosition=((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            }else if(layoutManager instanceof StaggeredGridLayoutManager){
                int[] into=new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                //这里返回的是最后一行的位置数组
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition=findPositionMax(into);
            }
//            Log.i(TAG,"lastVisibleItemPosition:"+lastVisibleItemPosition);
            //当前RecyclerView的所有子项个数
            // getItemCount()调用了getAdapter,由于重写了getAdapter导致获取的是原始的adapter,所以item数量要自行补全
//            int totalItemCount = layoutManager.getItemCount()+1;
            int totalItemCount = warpper.getItemCount();
//            Log.i(TAG,"totalItemCount:"+totalItemCount);
            //当前屏幕所有可看见的子项个数
            int visibleItemCount = layoutManager.getChildCount();
//            Log.i(TAG,"visibleItemCount:"+visibleItemCount);

            int refreshState=refreshView!=null?refreshView.getState():0;                //获取下拉刷新的状态，不是正在刷新才允许响应加载
//            Log.i(TAG,"refreshState："+refreshState);
            if(     visibleItemCount>0
                    && lastVisibleItemPosition >= totalItemCount-1
                    && refreshState == refreshView.STATE_NORMAL      ){              //已到达底部
                isLoading=true;                                                        //控制在加载过程中不会响应二次加载
                loadView.setState(loadView.STATE_LOADING);
                if(onLoadingListener!=null)
                    onLoadingListener.onLoad();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(lastY==-1) lastY=e.getRawY();                  //相对屏幕左上角的y坐标 因触摸时未触发ACTION_DOWN

        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                //按下触摸时更新lastY
                lastY=e.getRawY();                          //相对屏幕左上角的y坐标
                break;
            case MotionEvent.ACTION_MOVE:
                float delta=e.getRawY()-lastY;              //获取当前位置与之前的差值
                lastY=e.getRawY();
                if(isOnTop()&&isRefreshEnabled){            //到达列表顶部且支持下拉刷新

                    refreshView.onMove(delta / 3);      //除以3用于防止滑动过于灵敏的情况
                    if(refreshView.getVisiableHeight()>0&&refreshView.getState()<refreshView.STATE_REFRESHING){
                        return false;
                    }
                }
                break;
            default:            //包括CANCEL、UP事件
                lastY=-1;       //重置y坐标位置
                if(isOnTop()&&isRefreshEnabled){
                    if(refreshView.releaseAction()){
                        //响应下拉刷新监听回调
                        if(onLoadingListener!=null){
                            onLoadingListener.onRefresh();
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    /**
     * 判断是否到达列表顶部
     * @return
     */
    private boolean isOnTop(){
        if(refreshView==null) return false;
        if(refreshView.getParent()!=null){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 筛选出流式布局最后一行最大的index
     * @param into
     * @return
     */
    private int findPositionMax(int[] into){
        int max=0;
        for ( int i : into) {
            max=i>max?i:max;
        }
        return max;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(isRefreshEnabled||isLoadEnabled){
            warpper =new Warpper(adapter,refreshView,loadView);
            super.setAdapter(warpper);
        }else{
            super.setAdapter(adapter);
        }
        adapter.registerAdapterDataObserver(observer);
        observer.onChanged();                                 //setAdapter的时候需手动通知有数据更新
    }

    @Override
    public Adapter getAdapter() {
        if(warpper !=null){
            return warpper.getOriginalAdapter();
        }else{
            return super.getAdapter();
        }
    }

    public Warpper getWarpper() {
        return warpper;
    }


    public boolean isRefreshEnabled() {
        return isRefreshEnabled;
    }

    public boolean isLoadEnabled() {
        return isLoadEnabled;
    }

    class Observer extends RecyclerView.AdapterDataObserver{
        //notifyDataSetChanged()响应
        @Override
        public void onChanged() {
            super.onChanged();
//            Log.i(TAG,"onChanged()");

//            if(refreshView!=null&&refreshView.getState() == refreshView.STATE_REFRESHING) {
//                setRefresh(false);
//            }

            warpper.notifyDataSetChanged();

            checkIsEmpty();
        }

        //notifyItemInserted()响应
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart,itemCount);
//            Log.i(TAG,"onItemRangeInserted:positionStart"+positionStart+" itemCount:"+ itemCount);

            //如果是上拉加载状态自动更新footerview的状态
            if(!warpper.isEmpty() && isLoading){
                setLoadCompelete(itemCount>0?false:true);               //根据需要更新的数量判断有无更多数据加载
            }

            //如果是下拉刷新状态自动更新refreshheader的状态
            if(!isLoading&&refreshView!=null&&refreshView.getState() == refreshView.STATE_REFRESHING) {
                setRefresh(false);
            }
            positionStart=getRealPosition(positionStart);
            warpper.notifyItemRangeInserted(positionStart,itemCount);

            checkIsEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            positionStart=getRealPosition(positionStart);
            warpper.notifyItemRangeRemoved(positionStart,itemCount);
            checkIsEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            positionStart=getRealPosition(positionStart);
            warpper.notifyItemRangeChanged(positionStart,itemCount);
            checkIsEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            this.onItemRangeChanged(positionStart,itemCount);
        }

        /**
         * 判断是否存在refreshHeader计算真实开始更新的位置
         * @param positionStart
         * @return
         */
        private int getRealPosition(int positionStart){
            return refreshView!=null?(positionStart+1):positionStart;
        }

        /**
         * 判断数据是否为空
         */
        private void checkIsEmpty(){
            if(warpper !=null&&emptyView!=null){
                //若适配器里的itemCount等于装饰item的个数,说明现在数据为空
                if(warpper.isEmpty()){
                    RecycleBaubleView.this.setVisibility(GONE);
                    emptyView.setVisibility(VISIBLE);
                }else{
                    RecycleBaubleView.this.setVisibility(VISIBLE);
                    emptyView.setVisibility(GONE);
                }
            }
        }

    }

    public interface onLoadingListener{
        void onRefresh();       //下拉刷新
        void onLoad();          //上拉加载
    }
}

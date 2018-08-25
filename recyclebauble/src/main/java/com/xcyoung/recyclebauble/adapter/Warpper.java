package com.xcyoung.recyclebauble.adapter;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.xcyoung.recyclebauble.footer.BaseLoadView;
import com.xcyoung.recyclebauble.header.BaseRefreshView;

import java.util.List;


/**
 * 作为顶部刷新和底部加载的装饰适配器
 */
public class Warpper extends RecyclerView.Adapter {
    private static final int TYPE_REFRESH_HEADER = 10000;   //设置一个很大的数字,尽可能避免和用户的adapter冲突
    private static final int TYPE_FOOTER = 10001;

    private RecyclerView.Adapter targetAdapter;
    private BaseRefreshView refreshView;
    private BaseLoadView loadView;
    public Warpper(RecyclerView.Adapter adapter,
                   @Nullable BaseRefreshView refreshView,
                   @Nullable BaseLoadView loadView){
        this.targetAdapter=adapter;
        this.refreshView=refreshView;
        this.loadView=loadView;
    }

    /**
     * 获取原始的adapter
     * @return
     */
    public RecyclerView.Adapter getOriginalAdapter(){
        return this.targetAdapter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_REFRESH_HEADER){
            return new SimpleViewHolder(refreshView);
        }else if(viewType == TYPE_FOOTER) {
            return new SimpleViewHolder(loadView);
        }else{              //不是头部和尾部响应目标对应的方法
            return targetAdapter.onCreateViewHolder(parent,viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(isRefreshHeader(position)) return;                                   //如果是头部或尾部则return
        int realPos=refreshView!=null?position-1:position;                      //计算真实位置 因为第0位被refresh占用
        if(targetAdapter!=null){
            int targetItemCount=targetAdapter.getItemCount();           //获取真实的item数量
            if(realPos<targetItemCount){                                //真实位置比真实数量小 说明在真实的列表范围
                targetAdapter.onBindViewHolder(holder,realPos);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (isRefreshHeader(position)) {
            return;
        }

        int realPos=refreshView!=null?position-1:position;                      //计算真实位置 因为第0位被refresh占用
        int adapterCount;
        if (targetAdapter != null) {
            adapterCount = targetAdapter.getItemCount();
            if (realPos < adapterCount) {
                if(payloads.isEmpty()){
                    targetAdapter.onBindViewHolder(holder, realPos);
                }
                else{
                    targetAdapter.onBindViewHolder(holder, realPos,payloads);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        int warpCount;             //装饰item的个数
        if(refreshView!=null&&loadView!=null) warpCount=2;
        else if(refreshView==null&&loadView==null) warpCount=0;
        else warpCount=1;

        if(targetAdapter!=null){
            return targetAdapter.getItemCount()+warpCount;
        }else{
            return warpCount;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int realPos=refreshView!=null?position-1:position;                           //计算真实位置 因为第0位被refresh占用
        if(isRefreshHeader(position)){          //0号位默认为refresh
            return TYPE_REFRESH_HEADER;
        }

        if(isLoadFooter(position)){
            return TYPE_FOOTER;
        }

        if(targetAdapter!=null){
            int targetItemCount=targetAdapter.getItemCount();           //获取真实的item数量
            if(realPos<targetItemCount){                                //真实位置比真实数量小 说明在真实的列表范围
                int itemType=targetAdapter.getItemViewType(realPos);
                if(isReservedItemViewType(itemType)) {
                    throw new IllegalStateException("要求viewType的值不等于10000、10001" );
                }
                return itemType;
            }
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if(isRefreshHeader(position)) return 1;
        if(isLoadFooter(position)) return 2;
        if(targetAdapter!=null&&position>=(refreshView!=null?1:0)){
            int realPos=refreshView!=null?position-1:position;
            int targetItemCount=targetAdapter.getItemCount();
            if(realPos<targetItemCount){
                targetAdapter.getItemId(realPos);
            }
        }
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager=recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager){           //如果改列表为网格布局的话需要设置它在下拉刷新item、底部加载item占满一行
            final GridLayoutManager gridLayoutManager= (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return isRefreshHeader(position)||isLoadFooter(position)?gridLayoutManager.getSpanCount():1;
                }
            });
        }
        targetAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        targetAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    //当列表项出现到可视界面的时候调用
    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        View itemView = holder.itemView;
        ViewGroup.LayoutParams lp = itemView.getLayoutParams();
        if (lp == null) {
            return;
        }
        if (holder instanceof SimpleViewHolder) {               //SimpleViewHolder说明是refresh、load需要占满一行
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {        //只要适配流式布局
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        targetAdapter.onViewAttachedToWindow(holder);
    }

    //当适配器创建的view（即列表项view）被窗口分离（即滑动离开了当前窗口界面）就会被调用
    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        targetAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        targetAdapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
        return targetAdapter.onFailedToRecycleView(holder);
    }

    /**
     * 判断该位置是否为刷新头item
     * @param position
     * @return
     */
    public boolean isRefreshHeader(int position)   {
        return position == 0 && refreshView!=null;
    }

    /**
     * 判断该位置是否为加载尾item
     * @param position
     * @return
     */
    public boolean isLoadFooter(int position){
        return loadView != null && position == (getItemCount()-1);
    }

    /**
     * 判断现在是否为空数据
     * @return
     */
    public boolean isEmpty() {
        int warpCount;             //装饰item的个数
        if(refreshView!=null&&loadView!=null) warpCount=2;
        else if(refreshView==null&&loadView==null) warpCount=0;
        else warpCount=1;

        return warpCount == getItemCount()?true:false;
    }

    //判断是否是XRecyclerView保留的itemViewType
    private boolean isReservedItemViewType(int itemViewType) {
        if(itemViewType == TYPE_REFRESH_HEADER || itemViewType == TYPE_FOOTER) {
            return true;
        } else {
            return false;
        }
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder{

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }
}

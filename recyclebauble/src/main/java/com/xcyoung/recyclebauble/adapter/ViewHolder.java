package com.xcyoung.recyclebauble.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.Unbinder;

public abstract class ViewHolder<Data> extends RecyclerView.ViewHolder {
    public Unbinder unbinder;
    public AdapterCallback<Data> dataAdapterCallback;
    protected Data mData;

    public ViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 绑定数据
     * @param data
     */
    void bind(Data data,int position){
        this.mData=data;
        onBind(data,position);
    }

    /**
     * 当绑定数据时需要进行的操作回调,可编写一些item中元素的设置和点击监听作用与onBindViewHolder类似
     * @param data
     */
    protected abstract void onBind(Data data,int position);

    /**
     * ViewHolder自身对数据更新的回调
     * @param data
     */
    public void updataData(Data data){
        if(this.dataAdapterCallback!=null){
            this.dataAdapterCallback.update(data,this);
        }
    }


}

package com.xcyoung.recyclebauble.adapter;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

public abstract class Adapter<Data> extends RecyclerView.Adapter<ViewHolder<Data>>
        implements AdapterCallback<Data>{
    protected List<Data> list;            //数据源
    private AdapterListener<Data> listener;     //数据更新的回调

    public Adapter() {
        this(null);
    }

    public Adapter(AdapterListener<Data> updateListener) {
        this(new ArrayList<Data>(),updateListener);
    }

    public Adapter(List<Data> list, AdapterListener<Data> listener) {
        this.list = list;
        this.listener = listener;
    }

    /**
     * 布局类型返回
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position,list.get(position));
    }

    /**
     * 子类需重写布局类型返回
     * @param position
     * @param data
     * @return  返回该布局的id即可
     */
    protected abstract int getItemViewType(int position,Data data);

    /**
     * 创建一个ViewHolder
     *
     * @param parent   RecyclerView
     * @param viewType 界面的类型,约定为XML布局的Id
     * @return ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder<Data> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root= LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false);
        //将创建ViewHolder对象交给子类处理，目的是创建ViewHolder的子类
        ViewHolder<Data> holder=onCreateViewHolder(root,viewType);

//        // 设置View的Tag为ViewHolder，进行双向绑定
        root.setTag(holder);
//        //设置点击事件
//        root.setOnClickListener(this);
//        root.setOnLongClickListener(this);

        // 进行界面注解绑定
        holder.unbinder= ButterKnife.bind(holder,root);
        // 绑定callback
        holder.dataAdapterCallback=this;

        return holder;
    }

    /**
     *将创建ViewHolder对象交给子类处理，目的是创建ViewHolder的子类
     */
    protected abstract ViewHolder<Data> onCreateViewHolder(View view,int viewType);

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder<Data> holder, final int position) {
        //将数据与holder绑定
        Data data=list.get(position);
        holder.bind(data,position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Adapter.this.listener!=null){
                    Adapter.this.listener.onItemClick(holder,list.get(position),position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Adapter.this.listener!=null){
                    Adapter.this.listener.onItemLongClick(holder,list.get(position),position);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 获取当前数据源
     * @return
     */
    public List<Data> getItems(){
        return list;
    }

    /**
     * 添加数据
     * @param data
     */
    public void add(Data data){
        list.add(data);
        notifyItemInserted(list.size() - 1);
    }

    public void add(Data... dataList){
        if(dataList!=null&&dataList.length>0){
            int startPos=list.size();
            Collections.addAll(list,dataList);
            notifyItemRangeInserted(startPos, dataList.length);
        }
    }

    public void add(Collection<Data> dataList){
        if (dataList != null && dataList.size() >= 0) {
            int startPos = list.size();
            list.addAll(dataList);
            notifyItemRangeInserted(startPos, dataList.size());
        }
    }

    /**
     * 删除数据
     * @param position
     */
    public void remove(int position){
        if(position < 0) return;
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(int startPosition,int endPostion){
        if(startPosition < 0 || endPostion < 0) return;
        int index=startPosition;
        while (index<=endPostion){
            list.remove(index);
            notifyItemRemoved(index);
            index++;
        }
    }

    /**
     * 清空数据
     */
    public void clear(){
        int preSize=list.size();
        list.clear();
        notifyItemRangeRemoved(0, preSize);
    }

    /**
     * 替换为一个新的集合，其中包括了清空
     *
     * @param dataList 一个新的集合
     */
    public void replace(List<Data> dataList) {
        if (dataList == null || dataList.size() == 0)
            return;
        int preSize=list.size();
        list.clear();
        notifyItemRangeRemoved(0, preSize);
        list.addAll(dataList);
        notifyItemRangeInserted(0,list.size());
    }

    @Deprecated
    @Override
    public void update(Data data, ViewHolder<Data> holder) {
        int pos=holder.getAdapterPosition();
        if(pos>0){
            list.remove(pos);
            list.add(pos,data);
            notifyItemChanged(pos);
        }
    }

    /**
     * 我们的自定义监听器
     *
     * @param <Data> 范型
     */
    public interface AdapterListener<Data> {
        // 当Cell点击的时候触发
        void onItemClick(ViewHolder holder, Data data,int position);

        // 当Cell长按时触发
        void onItemLongClick(ViewHolder holder, Data data,int position);
    }
}

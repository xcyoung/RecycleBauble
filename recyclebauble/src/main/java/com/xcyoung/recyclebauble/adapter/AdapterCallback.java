package com.xcyoung.recyclebauble.adapter;

/**
 * 发生数据改变是的回调
 * @param <Data>
 */
public interface AdapterCallback<Data> {
    void update(Data data,ViewHolder<Data> holder);
}

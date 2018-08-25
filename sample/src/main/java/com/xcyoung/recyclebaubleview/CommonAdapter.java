package com.xcyoung.recyclebaubleview;

import android.view.View;
import android.widget.TextView;

import com.xcyoung.recyclebauble.adapter.Adapter;
import com.xcyoung.recyclebauble.adapter.ViewHolder;

import java.util.List;

public class CommonAdapter extends Adapter<String> {

    public CommonAdapter(List<String> list, AdapterListener<String> listener) {
        super(list, listener);
    }

    public CommonAdapter() {
    }

    public CommonAdapter(AdapterListener<String> updateListener) {
        super(updateListener);
    }

    @Override
    protected int getItemViewType(int position, String s) {
        return R.layout.item_horizontal;
    }

    @Override
    protected ViewHolder<String> onCreateViewHolder(View view, int viewType) {
        return new Holder(view);
    }

    class Holder extends ViewHolder<String>{
        private TextView textView;
        public Holder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.textView);
        }

        @Override
        protected void onBind(String s, int position) {
            textView.setText(list.get(position));
        }
    }
}

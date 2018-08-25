package com.xcyoung.recyclebauble.header;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import com.xcyoung.recyclebauble.R;

public class ProgressRefreshView extends BaseRefreshView {
    ProgressView progressView;
    public ProgressRefreshView(Context context) {
        super(context);
    }

    public ProgressRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void getContainerView() {
//        View container= LayoutInflater.from(getContext()).inflate(R.layout.view_progress_refresh,this);
//        progressView=container.findViewById(R.id.progress);
        progressView=new ProgressView(getContext());
        addView(progressView,new ViewGroup.LayoutParams((int) getContext().getResources().getDimension(R.dimen.dp100), ViewGroup.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    protected void onNormal() {

    }

    @Override
    public void onMoveUI(float progress,float delta) {
        progressView.onMoveUI(progress,delta);
    }

    @Override
    protected void onReleaseRefresh() {

    }

    @Override
    protected void onRefreshing() {
        progressView.refreshStart();
    }

    @Override
    protected void onRefreshDone() {
        progressView.refreshDone(true);
    }

    @Override
    protected void onRefreshError() {
        progressView.refreshDone(false);
    }
}

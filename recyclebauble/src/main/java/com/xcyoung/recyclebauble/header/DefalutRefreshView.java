package com.xcyoung.recyclebauble.header;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.xcyoung.recyclebauble.R;


public class DefalutRefreshView extends BaseRefreshView {
    private static final String TAG= DefalutRefreshView.class.getSimpleName();
    private ImageView arrow;    //箭头
    private ProgressBar loading;
    private TextView nowTime;   //当前时间
    private TextView tips;      //提示语

    public DefalutRefreshView(Context context) {
        this(context,null);
    }

    public DefalutRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void getContainerView() {
        View container= LayoutInflater.from(getContext()).inflate(R.layout.view_defalut_refresh,this);
        arrow=container.findViewById(R.id.arraw);
        nowTime=container.findViewById(R.id.now_time);
        tips=container.findViewById(R.id.tips);
        loading=container.findViewById(R.id.loading);
        nowTime.setText(loadBeforeTime());
        setGravity(Gravity.BOTTOM);
    }

    @Override
    protected void onNormal() {
        nowTime.setText(loadBeforeTime());
        arrow.animate().rotationX(0);
        tips.setText(getContext().getResources().getString(R.string.pull));
    }

    @Override
    protected void onReleaseRefresh() {
        arrow.animate().rotationX(180);
        tips.setText(getContext().getResources().getString(R.string.release));
    }

    @Override
    protected void onRefreshing() {
        saveNowTime();
        arrow.setVisibility(GONE);
        loading.setVisibility(VISIBLE);
        tips.setText(getContext().getResources().getString(R.string.refreshing));
    }

    @Override
    protected void onRefreshDone() {
        arrow.animate().rotationX(0);
        arrow.setVisibility(VISIBLE);
        loading.setVisibility(GONE);
        tips.setText(getContext().getResources().getString(R.string.compelete));
    }

    @Override
    protected void onRefreshError() {
        arrow.animate().rotationX(0);
        arrow.setVisibility(VISIBLE);
        loading.setVisibility(GONE);
        tips.setText(getContext().getResources().getString(R.string.error));
    }

    /**
     * 保存本次更新的时间
     */
    private void saveNowTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SharedPreferences sp=getContext().getSharedPreferences("PULL_REFRESH",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("nowTime",df.format(new Date()));
        editor.apply();
    }

    /**
     * 获取上次更新的时间
     * @return
     */
    private String loadBeforeTime(){
        SharedPreferences sp=getContext().getSharedPreferences("PULL_REFRESH",Context.MODE_PRIVATE);
        return sp.getString("nowTime","无");
    }
}

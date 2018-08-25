package com.xcyoung.recyclebauble.footer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xcyoung.recyclebauble.R;

public class DefalutLoadView extends BaseLoadView {
    private ProgressBar progressBar;
    private TextView tips;
    public DefalutLoadView(Context context) {
        super(context);
    }

    @Override
    protected View getContainerView() {
        Context context=getContext();

        LinearLayout layout = new LinearLayout(context);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);
        layout.setGravity(Gravity.CENTER);
        int padding= (int) getResources().getDimension(R.dimen.dp5);
        layout.setPadding(padding, padding, padding, padding);

        progressBar=new ProgressBar(context);
        int color=context.getResources().getColor(R.color.defalut_blue);
        ColorStateList colorStateList=ColorStateList.valueOf(color);
        progressBar.setIndeterminateTintList(colorStateList);
        progressBar.setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);
        layout.addView(progressBar);

        tips=new TextView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins( (int)getResources().getDimension(R.dimen.dp10),0,0,0 );
        tips.setLayoutParams(lp);
        tips.setTextColor(getResources().getColor(android.R.color.background_dark));
//        tips.setTextSize(getResources().getDimension(R.dimen.sp16));
        layout.addView(tips);

        return layout;
    }

    @Override
    protected void onLoading() {
        tips.setText(getContext().getResources().getText(R.string.refreshing));
    }

    @Override
    protected void onCompelete() {
        tips.setText(getContext().getResources().getText(R.string.compelete));
    }

    @Override
    protected void onNoMore() {
        tips.setText(getContext().getResources().getText(R.string.no_more));
    }
}

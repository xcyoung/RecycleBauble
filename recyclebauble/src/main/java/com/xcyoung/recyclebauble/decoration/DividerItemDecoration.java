package com.xcyoung.recyclebauble.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xcyoung.recyclebauble.R;
import com.xcyoung.recyclebauble.adapter.Warpper;
import com.xcyoung.recyclebauble.view.RecycleBaubleView;

/**
 * 纵向线性列表的分割线ItemDecoration
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG=DividerItemDecoration.class.getSimpleName();
    private int dividerHeight;               //分割线的高度 默认1dp
    private int currentOrientation;
    private float leftMargin;               //分割线右边留10dp外边距
    private Paint dividerPaint;

    public DividerItemDecoration(Context context) {
        this(context,(int) context.getResources().getDimension(R.dimen.dp1),R.color.defalut_divider);
    }

    public DividerItemDecoration(Context context, int dividerHeight){
        this(context,dividerHeight,R.color.defalut_divider);
    }

    public DividerItemDecoration(Context context, int dividerHeight, @ColorRes int dividerColor){
        this.dividerHeight= dividerHeight;
        this.dividerPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint.setColor(context.getResources().getColor(dividerColor));
        this.leftMargin=context.getResources().getDimension(R.dimen.dp10);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position=parent.getChildAdapterPosition(view);

        if(parent instanceof RecycleBaubleView){
            Warpper warpper =((RecycleBaubleView) parent).getWarpper();
            if(warpper !=null && warpper.isRefreshHeader(position)){
                return;
            }
        }

        RecyclerView.LayoutManager layoutManager=parent.getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager){
            if(((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL){
                currentOrientation=LinearLayoutManager.VERTICAL;
                outRect.set(0,0,0,dividerHeight);
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if(currentOrientation == LinearLayoutManager.VERTICAL) drawVertical(c,parent);
    }

//    @Override
//    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
//        super.onDrawOver(c, parent, state);
//
//    }

    private void drawVertical(Canvas c, RecyclerView parent){
        c.save();
        //得到列表所有的条目
        int childCount = parent.getChildCount();
        //得到条目的宽和高
        int left = (int) (parent.getPaddingLeft()+leftMargin);
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            //计算每一个条目的顶点和底部 float值
            float top = view.getBottom();
            float bottom = view.getBottom()+dividerHeight;
            //重新绘制
            c.drawRect(left, top, right, bottom, dividerPaint);         //默认颜色为#999999
        }
        c.restore();
    }

}

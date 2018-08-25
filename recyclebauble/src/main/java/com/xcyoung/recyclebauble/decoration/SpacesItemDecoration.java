package com.xcyoung.recyclebauble.decoration;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xcyoung.recyclebauble.view.RecycleBaubleView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int topBottomSpace;               //顶部和底部的间距
    private int rightLeftSpace;               //左后的间距

    public SpacesItemDecoration(int topBottomSpace, int rightLeftSpace) {
        this.topBottomSpace = topBottomSpace;
        this.rightLeftSpace = rightLeftSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager layoutManager=parent.getLayoutManager();
        //因为LinearLayoutManager是GridLayoutManager父类，需要先判断GridLayoutManager
        if(layoutManager instanceof GridLayoutManager){
            setGridManagerOffsets(outRect,view,parent);
        }else if(layoutManager instanceof LinearLayoutManager){
            setLinearLayoutManagerOffsets(outRect,view,parent);
        }
    }

    /**
     * 设置线性布局的间距
     * @param outRect
     * @param view
     * @param parent
     */
    private void setLinearLayoutManagerOffsets(Rect outRect, View view, RecyclerView parent) {
        LinearLayoutManager linearLayoutManager= (LinearLayoutManager) parent.getLayoutManager();
        boolean hasFooter;
        int lastCount;
        if(parent instanceof RecycleBaubleView){
           hasFooter=((RecycleBaubleView) parent).isLoadEnabled();
           lastCount=hasFooter?linearLayoutManager.getItemCount()+1
                   :linearLayoutManager.getItemCount()-1;
        }else{
            lastCount=linearLayoutManager.getItemCount()-1;
        }

        if(linearLayoutManager.getOrientation()==LinearLayoutManager.VERTICAL){
            if(parent.getChildAdapterPosition(view) == lastCount){
                outRect.bottom= topBottomSpace;
            }
            outRect.top=topBottomSpace;
            outRect.right=rightLeftSpace;
            outRect.left=rightLeftSpace;
        }else if(linearLayoutManager.getOrientation()==LinearLayoutManager.HORIZONTAL){
            if(parent.getChildAdapterPosition(view) == lastCount){
                outRect.right= rightLeftSpace;
            }
            outRect.top=topBottomSpace;
            outRect.bottom=topBottomSpace;
            outRect.left=rightLeftSpace;
        }
    }

    /**
     * 设置网格布局的间距
     * @param outRect
     * @param view
     * @param parent
     */
    private void setGridManagerOffsets(Rect outRect, View view, RecyclerView parent){
        GridLayoutManager gridLayoutManager= (GridLayoutManager) parent.getLayoutManager();
        final GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int childPostion=parent.getChildAdapterPosition(view);      //获取view所在的位置
        int spanCount=gridLayoutManager.getSpanCount();             //获取每行列数
        if(gridLayoutManager.getOrientation() == GridLayoutManager.VERTICAL){
            outRect.bottom=topBottomSpace;
            if(lp.getSpanSize() == spanCount){          //这个item占满一行时
                outRect.left=rightLeftSpace;
                outRect.right=rightLeftSpace;
            }else{
                outRect.left = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * rightLeftSpace);
                outRect.right = (int) (((float) rightLeftSpace * (spanCount + 1) / spanCount) - outRect.left);
            }
        }else if(gridLayoutManager.getOrientation() == GridLayoutManager.HORIZONTAL){
            outRect.right=rightLeftSpace;
            if(lp.getSpanSize() == spanCount){
                outRect.top=topBottomSpace;
                outRect.bottom=topBottomSpace;
            }else{
                outRect.top = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * topBottomSpace);
                outRect.bottom = (int) (((float) topBottomSpace * (spanCount + 1) / spanCount) - outRect.top);
            }
        }
    }
}

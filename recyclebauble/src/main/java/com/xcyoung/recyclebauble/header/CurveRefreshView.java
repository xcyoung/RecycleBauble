//package com.xcyoung.recyclebauble.header;
//
//import android.content.Context;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.xcyoung.recyclebauble.R;
//
//public class CurveRefreshView extends BaseRefreshView {
//    CurveView curveView;
//    public CurveRefreshView(Context context) {
//        super(context);
//    }
//
////    @Override
////    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
////        // 不能删除,用于父控件测量用
////        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//////        int childViewCount = this.getChildCount();
//////        for (int i = 0; i < childViewCount; i++) {
////            View childView = this.getChildAt(0);
////            int width = MeasureSpec.getSize(widthMeasureSpec);
////            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
////                    MeasureSpec.AT_MOST);
////            int height =MeasureSpec.getSize(heightMeasureSpec);
////            heightMeasureSpec =MeasureSpec.makeMeasureSpec(height,
////                    MeasureSpec.AT_MOST);
////            //测量后让子View获得宽高
////            childView.measure(widthMeasureSpec, heightMeasureSpec);
//////        }
////    }
//
//    @Override
//    protected void getContainerView() {
//        curveView=new CurveView(getContext());
//        addView(curveView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//    }
//
//    @Override
//    protected void onMoveUI(float progress, float delta) {
//        super.onMoveUI(progress, delta);
//        curveView.onMoveUI(progress,delta);
//    }
//
//    @Override
//    protected void onNormal() {
//
//    }
//
//    @Override
//    protected void onReleaseRefresh() {
//        curveView.setViewHeight(mMeasuredHeight);
//    }
//
//    @Override
//    protected void onRefreshing() {
//
//    }
//
//    @Override
//    protected void onRefreshDone() {
//
//    }
//
//    @Override
//    protected void onRefreshError() {
//
//    }
//}

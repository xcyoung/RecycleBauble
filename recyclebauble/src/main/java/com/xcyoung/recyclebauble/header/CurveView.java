//package com.xcyoung.recyclebauble.header;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.print.PrinterId;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.View;
//
//import com.xcyoung.recyclebauble.R;
//
//public class CurveView extends View {
//    private static final String TAG=CurveView.class.getSimpleName();
//    private Path curvePath;
//    private Paint mPaint;
//
//    private float viewHeight;                             //控件的高度
//    private float moveProgress =0;                        //下拉占阈值的比值
//    private float maxOnMove;                              //最大还可以下拉的高度
//    private float currentY;                               //
//    private int bgColor;
//
//    //状态
//    private final int STATE_ON_MOVE=1;
//    private final int STATE_CAN_RELEASE=2;
//    private final int STATE_DONE=3;
//    private final int STATE_ERROR=4;
//    private int currentState;
//    public CurveView(Context context) {
//        this(context,null);
//    }
//
//    public CurveView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        initView(context);
//    }
//
//    private void initView(Context context) {
//        bgColor=context.getResources().getColor(R.color.defalut_blue);
//        maxOnMove=context.getResources().getDimension(R.dimen.dp60);
//        curvePath=new Path();
//
//        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setColor(bgColor);
////        setBackgroundColor(bgColor);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if(moveProgress >= 1 && currentState == STATE_CAN_RELEASE){
//            curvePath.reset();
//            curvePath.moveTo(0, viewHeight);
//            curvePath.quadTo(getWidth()/2, currentY, getWidth(), viewHeight);
//            canvas.drawPath(curvePath, mPaint);
//            Log.i(TAG,"viewHeight"+viewHeight);
//            Log.i(TAG,"currentY"+currentY);
//            Log.i(TAG,"getWidth"+getWidth());
//        }
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
////        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //默认铺满父布局
//        int widthSpecSize=MeasureSpec.getSize(widthMeasureSpec);
//        int heightSpecSize=MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(widthSpecSize, (int) getContext().getResources().getDimension(R.dimen.dp100));
//    }
//
//    public void onMoveUI(float progress,float delta){
//        if(currentState!=STATE_ON_MOVE) currentState=STATE_ON_MOVE;
//        this.moveProgress = progress;
//        if(progress >= 1){
//            currentState=STATE_CAN_RELEASE;
//            if(currentY >= viewHeight+maxOnMove){
//                currentY=viewHeight+maxOnMove;
//            }else{
//                currentY+=delta;
//            }
//        }else{
//            this.currentY=getContext().getResources().getDimension(R.dimen.dp60);
//        }
//        invalidate();
//    }
//
//    public void setViewHeight(float viewHeight){
//        this.viewHeight=getContext().getResources().getDimension(R.dimen.dp80);
//        this.currentY=getContext().getResources().getDimension(R.dimen.dp80);
//    }
//}

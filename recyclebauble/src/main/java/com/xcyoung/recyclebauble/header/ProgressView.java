package com.xcyoung.recyclebauble.header;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.xcyoung.recyclebauble.R;

public class ProgressView extends View {
    private Paint mPaint;
    private Paint mBgPaint;                 //背景画笔
    private Paint mHookPaint;               //钩画笔
    private float currentAngle;             //当前旋转的角度 （旋转起点）
    private float currentSweepAngle;        //当前需要转过的角度 （需要旋转多少）
    private float currentAngleOffset;       //当前旋转起点的偏移量
    private float onPullAngle;

    private final float MIN_SWEEP_ANGLE=30;       //最小需要旋转的角度

    private ObjectAnimator currentAngleAnimator;
    private ObjectAnimator currentSweepAngleAnimator;
    private ValueAnimator factionAnimator;

    private final RectF fBounds = new RectF();
    private final RectF fBgBounds = new RectF();
    private float dp15;
    private float dp80;
    private float moveProgress =0;                        //下拉占阈值的比值

    private float faction;                            //打钩动画的进度
    private Path mHook;                               //钩路径
    private Path mWaterDrop;                               //钩路径
    //控件中心点
    private float centerWidth;
    private float centerHeight;

    private float bgCircleRadius;
    private float smallCircleY=centerWidth+bgCircleRadius;                          //水滴效果时的y圆心坐标 默认是中心点
    private float smallCircleRadius;
    private boolean mModeAppearing=true;


    //状态
    private final int STATE_ON_MOVE=1;
    private final int STATE_REFRESHING=2;
    private final int STATE_DONE=3;
    private final int STATE_ERROR=4;
    private int currentState;
    public ProgressView(Context context) {
        this(context,null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.WHITE);

        mBgPaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(context.getResources().getColor(R.color.defalut_blue));

        mHookPaint=new Paint(mPaint);
        mHook=new Path();
        mWaterDrop=new Path();

        dp15=context.getResources().getDimension(R.dimen.dp15);
        dp80=context.getResources().getDimension(R.dimen.dp80);

        setUpAnimation();
    }

    private void setUpAnimation() {
        currentAngleAnimator=ObjectAnimator.ofFloat(this,"currentAngle",360f);
        currentAngleAnimator.setInterpolator(new LinearInterpolator());
        currentAngleAnimator.setDuration(1000);
        currentAngleAnimator.setRepeatMode(ValueAnimator.RESTART);
        currentAngleAnimator.setRepeatCount(ValueAnimator.INFINITE);

        currentSweepAngleAnimator=ObjectAnimator.ofFloat(this,"currentSweepAngle",360f - MIN_SWEEP_ANGLE * 2);
        currentSweepAngleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        currentSweepAngleAnimator.setDuration(1500);
        currentSweepAngleAnimator.setRepeatMode(ValueAnimator.RESTART);
        currentSweepAngleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        currentSweepAngleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            /**
             * 动画重复时
             * @param animation
             */
            @Override
            public void onAnimationRepeat(Animator animation) {
                toggleAppearing();
            }
        });

        factionAnimator=ValueAnimator.ofInt(0,255);
        factionAnimator.setInterpolator(new LinearInterpolator());
        factionAnimator.setDuration(300);
        factionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                faction=animation.getAnimatedFraction();
                mHookPaint.setAlpha((int) animation.getAnimatedValue());
                invalidate();
            }
        });
    }

    /**
     * 当动画重新播放时切换显示模式让动画平滑过渡
     */
    private void toggleAppearing(){
        mModeAppearing=!mModeAppearing;
        if(mModeAppearing){
            currentAngleOffset=(currentAngleOffset + MIN_SWEEP_ANGLE*2)%360;
        }
    }

    /**
     * 刷新完成前的动画
     */
    public void refreshStart(){
        if(currentAngleAnimator.isRunning()) return;
        currentState=STATE_REFRESHING;
        currentAngleAnimator.start();
        currentSweepAngleAnimator.start();
        invalidate();
    }

    /**
     * 刷新完成后的动画
     */
    public void refreshDone(boolean isSuccess){
       if(currentAngleAnimator.isPaused()) return;
       currentState=isSuccess?STATE_DONE:STATE_ERROR;
       currentAngleAnimator.cancel();
       currentSweepAngleAnimator.cancel();
       invalidate();
       //开启打钩动画
        if (!factionAnimator.isRunning()) {
            factionAnimator.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(currentState == STATE_REFRESHING){
            drawArcOnRefresh(canvas);
        }else if(currentState == STATE_ON_MOVE){
            drawArcOnMove(canvas);
        }else if(currentState == STATE_DONE){
            drawOnDone(canvas);
        }else{
            drawOnError(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int spac= (int) dp80;
        setMeasuredDimension(measureHandle(spac,widthMeasureSpec),spac);
    }

    /**
     * 测量尺寸
     * @param defalutSize
     * @param measureSpec
     * @return
     */
    private int measureHandle(int defalutSize,int measureSpec){
        int result;
        int specMode=MeasureSpec.getMode(measureSpec);
        int specSize=MeasureSpec.getSize(measureSpec);
        if(specMode == MeasureSpec.EXACTLY){            //准确值
            result=specSize;
        }else if(specMode == MeasureSpec.AT_MOST){      //可为任意大小
            result=Math.min(defalutSize,specSize);
        }else{                                          //未确定值
            result=defalutSize;
        }
        return result;
    }

    /**
     * 刷新时绘制旋转圆弧
     * @param canvas
     */
    private void drawArcOnRefresh(Canvas canvas){
        float startAngle=currentAngle-currentAngleOffset;
        float sweepAngle=currentSweepAngle;
        //两种显示模式：1、开始角度按偏移量计算，每次只将旋转多少的角度增加最小旋转值(第1种的旋转位移小)
        //2、将起点更新到上一次到达的位置，将旋转多少的角度按360°-上次的旋转角度-最小旋转值(第2种的旋转位移更大)
        if(mModeAppearing){
            sweepAngle += MIN_SWEEP_ANGLE;
        }else{
            startAngle+=sweepAngle;             //本次旋转的起点需要加上上一次旋转过的角度
            sweepAngle=360-sweepAngle-MIN_SWEEP_ANGLE;
        }

        canvas.drawCircle(centerWidth,centerHeight,fBounds.width()/2+20, mBgPaint);

        canvas.drawArc(fBounds,startAngle,sweepAngle,false,mPaint);
    }

    /**
     * 滑动时绘制扇形
     * @param canvas
     */
    private void drawArcOnMove(Canvas canvas) {
//        if(moveProgress >= 1){
//            drawWaterDrop(canvas);
//        }else {
        canvas.drawArc(fBgBounds,0,360* moveProgress,true, mBgPaint);
//        }
        if(moveProgress >= 0.75) {          //下滑进度超过75%才开始画线条
            canvas.drawArc(fBounds, 30, 180 * moveProgress, false, mPaint);
        }
    }

    /**
     * 刷新完成时画钩
     * @param canvas
     */
    private void drawOnDone(Canvas canvas) {
        canvas.drawCircle(centerWidth,centerHeight,fBounds.width()/2+20, mBgPaint);
        mHook.reset();
        mHook.moveTo(fBounds.centerX() - fBounds.width() * 0.25f * faction, fBounds.centerY());
        mHook.lineTo(fBounds.centerX() - fBounds.width() * 0.1f * faction, fBounds.centerY() + fBounds.height() * 0.18f * faction);
        mHook.lineTo(fBounds.centerX() + fBounds.width() * 0.25f * faction, fBounds.centerY() - fBounds.height() * 0.20f * faction);
        canvas.drawPath(mHook, mHookPaint);
    }

    /**
     * 刷新失败后调用 画叉叉
     * @param canvas
     */
    private void drawOnError(Canvas canvas) {
        canvas.drawCircle(centerWidth,centerHeight,fBounds.width()/2+20, mBgPaint);
        mHook.reset();
        mHook.moveTo(fBounds.centerX() + fBounds.width() * 0.2f * faction, fBounds.centerY() - fBounds.height() * 0.2f * faction);
        mHook.lineTo(fBounds.centerX() - fBounds.width() * 0.2f * faction, fBounds.centerY() + fBounds.height() * 0.2f * faction);
        mHook.moveTo(fBounds.centerX() - fBounds.width() * 0.2f * faction, fBounds.centerY() - fBounds.height() * 0.2f * faction);
        mHook.lineTo(fBounds.centerX() + fBounds.width() * 0.2f * faction, fBounds.centerY() + fBounds.height() * 0.2f * faction);
        canvas.drawPath(mHook, mHookPaint);
    }

    /**
     * 绘制水滴效果
     * @param canvas
     */
    private void drawWaterDrop(Canvas canvas){
//        Log.i("aaa",smallCircleY+"");
        //路径重置
        mWaterDrop.reset();

        //绘制大圆圆弧
        mWaterDrop.arcTo(new RectF(centerWidth - bgCircleRadius, centerHeight - bgCircleRadius,
                centerWidth + bgCircleRadius, centerHeight + bgCircleRadius), 0, -180);
        //绘制左边的二次曲线
        mWaterDrop.quadTo(centerWidth - smallCircleRadius, (centerHeight + smallCircleY) / 2, centerWidth - smallCircleRadius, smallCircleY);
        //把点移动到大半圆的右边
        mWaterDrop.moveTo(centerWidth + bgCircleRadius, centerHeight);
        //绘制右边的二次曲线
        mWaterDrop.quadTo(centerWidth + smallCircleRadius, (centerWidth + smallCircleY) / 2, centerWidth + smallCircleRadius, smallCircleY);
        //绘制小圆圆弧
        mWaterDrop.arcTo(new RectF(centerWidth - smallCircleRadius, smallCircleY - smallCircleRadius,
                centerWidth + smallCircleRadius, smallCircleY + smallCircleRadius), 0, 180);

        canvas.drawPath(mWaterDrop, mBgPaint);

    }

    public void onMoveUI(float progress,float delta){
        if(currentState!=STATE_ON_MOVE) currentState=STATE_ON_MOVE;
        this.moveProgress = progress;
//        if(progress >= 1){
//            smallCircleY+=delta;
//            if(smallCircleY+smallCircleRadius >= dp80){
//                smallCircleY = dp80-smallCircleRadius;
//            }
//        }else{
//            smallCircleY=centerWidth+bgCircleRadius;
//        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        int min = Math.min(w, h);
        centerWidth=getWidth()/2;
        centerHeight=getHeight()/3;
        float spac= dp15;
        fBounds.left = centerWidth-spac;
        fBounds.right = centerWidth+spac;
        fBounds.top = centerHeight-spac;
        fBounds.bottom = centerHeight+spac;

//        fBounds.left = 10 * 2f + .5f;
//        fBounds.right = min - 10 * 2f - .5f;
//        fBounds.top = 10 * 2f + .5f;
//        fBounds.bottom = min - 10 * 2f - .5f;

        fBgBounds.left = centerWidth-spac-20;
        fBgBounds.right = centerWidth+spac+20;
        fBgBounds.top = centerHeight-spac-20;
        fBgBounds.bottom = centerHeight+spac+20;

        bgCircleRadius= fBounds.width()/2+20;
//        smallCircleRadius= fBounds.width()/2;
//        Log.i("aaa",centerWidth+bgCircleRadius+"");
    }

    /**以下属性get--set方法用于属性动画取值**/
    public float getCurrentAngle() {
        return currentAngle;
    }

    public void setCurrentAngle(float currentAngle) {
        this.currentAngle = currentAngle;
        invalidate();
    }

    public float getCurrentSweepAngle() {
        return currentSweepAngle;
    }

    public void setCurrentSweepAngle(float currentSweepAngle) {
        this.currentSweepAngle = currentSweepAngle;
        invalidate();
    }

    public float getOnPullAngle() {
        return onPullAngle;
    }

    public void setOnPullAngle(float onPullAngle) {
        this.onPullAngle = onPullAngle;
        invalidate();
    }
}

package com.zerostic.goodmorning.Application;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.zerostic.goodmorning.R;

import androidx.annotation.Nullable;

import java.util.Calendar;

/**
 Coded by iamjayantjha
 */

public class AnalogClockView extends View {

    private int mRadius;
    private double mAngle;
    private int mCentreX;
    private int mCentreY;
    private boolean mIsInit;
    private Paint mPaint;
    private Rect mRect;
    private int mHourHandSize;
    private int mHandSize;

    private final TypedArray style;
    private boolean mDrawSecond;
    private boolean mShowNumbers;
    private int mNumbersColor;
    private int mSecondColor;
    private int mMinuteColor;
    private int mHourColor;
    private int mBorderColor;
    private int mBackgroundColor;
    private int mStrokeWidth;
    private int mSecondStrokeWidth;
    private int mMinuteStrokeWidth;
    private int mHourStrokeWidth;
    private int mNumbersFontSize;

    public AnalogClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.style = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AnalogClock, 0, 0);
    }

    private void init(){
        int mHeight = getHeight();
        int mWidth = getWidth();
        int mPadding = 50;

        this.mCentreX = mWidth /2;
        this.mCentreY = mHeight /2;

        int mMinimum = Math.min(mHeight, mWidth);
        this.mRadius = mMinimum /2 - mPadding;
        this.mAngle = (float) ((Math.PI/30) - (Math.PI/2));
        this.mPaint = new Paint();
        this.mRect = new Rect();
        this.mHourHandSize = this.mRadius - this.mRadius/2;
        this.mHandSize = this.mRadius - this.mRadius/4;

        this.mDrawSecond = this.style.getBoolean(R.styleable.AnalogClock_showSecondPointer, false);
        this.mShowNumbers = this.style.getBoolean(R.styleable.AnalogClock_showNumbers, false);
        this.mNumbersColor = this.style.getColor(R.styleable.AnalogClock_numbersColor, Color.BLACK);
        this.mSecondColor = this.style.getColor(R.styleable.AnalogClock_secondColor, Color.RED);
        this.mMinuteColor  = this.style.getColor(R.styleable.AnalogClock_minuteColor, Color.BLACK);
        this.mHourColor  = this.style.getColor(R.styleable.AnalogClock_hourColor, Color.BLACK);
        this.mBorderColor  = this.style.getColor(R.styleable.AnalogClock_borderColor, Color.BLACK);
        this.mBackgroundColor  = this.style.getColor(R.styleable.AnalogClock_clockBackgroundColor,Color.WHITE);
        this.mStrokeWidth = this.style.getInt(R.styleable.AnalogClock_borderStrokeWidth, 8);
        this.mSecondStrokeWidth = this.style.getInt(R.styleable.AnalogClock_secondStrokeWidth, 7);
        this.mMinuteStrokeWidth = this.style.getInt(R.styleable.AnalogClock_minuteStrokeWidth, 9);
        this.mHourStrokeWidth = this.style.getInt(R.styleable.AnalogClock_hourStrokeWidth, 12);
        this.mNumbersFontSize = mMinimum/12;

        this.style.recycle();

        this.mIsInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!this.mIsInit){
            init();
        }

        this.drawCircle(canvas);
        this.drawPointer(canvas);

        if (this.mDrawSecond){
            postInvalidateDelayed(500);
        }else{
            postInvalidateDelayed(5000);
        }

    }

    private void drawCircle(Canvas canvas){
        //background
        this.setPaintAttributes(this.mBackgroundColor, Paint.Style.FILL, this.mStrokeWidth);
        canvas.drawCircle(this.mCentreX,this.mCentreY,this.mRadius,this.mPaint);
        //border
        this.setPaintAttributes(this.mBorderColor, Paint.Style.STROKE, this.mStrokeWidth);
        canvas.drawCircle(this.mCentreX,this.mCentreY,this.mRadius,this.mPaint);
    }

    private void drawPointer(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        float mHour = calendar.get(Calendar.HOUR_OF_DAY);    //convert to 12hour format from 24 hour format
        mHour = mHour > 12 ? mHour - 12 : mHour;
        float mMinute = calendar.get(Calendar.MINUTE);
        float mSecond = calendar.get(Calendar.SECOND);

        drawHourPointer(canvas, (mHour + mMinute / 60.0) * 5f);
        drawMinutePointer(canvas, mMinute);
        if (mDrawSecond){drawSecondsPointer(canvas, mSecond);}
        if (mShowNumbers){drawNumbers(canvas);}
    }

    private void drawHourPointer(Canvas canvas, double position) {
        this.setPaintAttributes(this.mHourColor, Paint.Style.STROKE,this.mHourStrokeWidth);
        this.mAngle = Math.PI * position / 30 - Math.PI / 2;
        canvas.drawLine(this.mCentreX, this.mCentreY,(float) (this.mCentreX + Math.cos(this.mAngle) * this.mHourHandSize)
                , (float) (this.mCentreY + Math.sin(this.mAngle) * this.mHourHandSize), this.mPaint);}

    private void drawMinutePointer(Canvas canvas, float position) {
        this.setPaintAttributes(this.mMinuteColor, Paint.Style.STROKE,this.mMinuteStrokeWidth);
        this.mAngle = Math.PI * position / 30 - Math.PI / 2;
        canvas.drawLine(this.mCentreX, this.mCentreY,(float)(this.mCentreX + Math.cos(this.mAngle)* this.mHandSize), (float)(this.mCentreY+Math.sin(this.mAngle) * this.mHourHandSize), this.mPaint);
    }

    private void drawSecondsPointer(Canvas canvas, float position) {
        this.mPaint.reset();
        this.setPaintAttributes(this.mSecondColor, Paint.Style.STROKE,this.mSecondStrokeWidth);
        mAngle = Math.PI * position / 30 - Math.PI / 2;
        canvas.drawLine(this.mCentreX, this.mCentreY, (float)(this.mCentreX + Math.cos(this.mAngle) * this.mHandSize)
                , (float)(this.mCentreY + Math.sin(this.mAngle) * this.mHourHandSize), this.mPaint);
    }

    private void drawNumbers(Canvas canvas) {
        this.mPaint.reset();
        this.mPaint.setColor(this.mNumbersColor);
        this.mPaint.setTextSize(this.mNumbersFontSize);
        for(int i = 1; i <= 12; i++){
            String num = String.valueOf(i);
            this.mPaint.getTextBounds(num, 0, num.length(), this.mRect);
            double angle = Math.PI / 6 * (i - 3);
            int x = (int) (this.mCentreX + Math.cos(angle) * this.mRadius/1.2 - this.mRect.width() / 2);
            int y = (int) (this.mCentreY + Math.sin(angle) * this.mRadius/1.2 + this.mRect.height() / 2);
            canvas.drawText(num, x, y, this.mPaint);
        }

    }

    private void setPaintAttributes(int color, Paint.Style stroke, int strokeWidth) {
        this.mPaint.reset();
        this.mPaint.setColor(color);
        this.mPaint.setStyle(stroke);
        this.mPaint.setStrokeWidth(strokeWidth);
        this.mPaint.setAntiAlias(true);
    }
}
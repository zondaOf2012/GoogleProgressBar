package com.jpardogo.android.googleprogressbar.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class GoogleProgressBar extends ProgressBar {
    private static final int FOLDED_TRANSPARENCY = 235;
    private static final int OPAQUE = 255;
    private int mProgressType;
    private int mY = Integer.MAX_VALUE;
    private int mX = Integer.MAX_VALUE;
    private Paint mPaint;
    private Paint mPaint2;
    private Paint mPaint3;
    private RectF mOval = new RectF();
    private int mDiameter;
    private Path mPath;
    private int mHalf;
    private ProgressStates mCurrentState = ProgressStates.FOLDING_DOWN;
    private ProgressStates mLastState;
    private int mSpeed;
    private int mUnfoldCounter = 0;
    private int mControlPointMinimun;
    private int mContorlPointMaximun;
    
    private PaintFlagsDrawFilter pfdfilter;


    private enum ProgressTypes {
        CIRCLES,
        NEXUS_CIRCLES,
        NEXUS_CROSS,
        NEXUS_ROATION_CROSS;
    }
    private enum ProgressColors {
        RED("#C93437"),
        BLUE("#375BF1"),
        YELLOW("#F7D23E"),
        GREEN("#34A350");

        private String color;

        private ProgressColors(final String htmlCode) {
            this.color = htmlCode;
        }

        @Override
        public String toString() {
            return color;
        }
    }


    private enum ProgressStates {
        FOLDING_DOWN,
        FOLDING_UP,
        FOLDING_LEFT,
        FOLDING_RIGHT,
        NOT_FOLDING;
    }

    public GoogleProgressBar(Context context) {
        super(context);
        init(context);
    }

    public GoogleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.GoogleProgressBar,0, 0);
        try {
            mProgressType = a.getInt(R.styleable.GoogleProgressBar_progressType, 0);
        } finally {
            a.recycle();
        }
        init(context);
    }

    public GoogleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setIndeterminateDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        switch (ProgressTypes.values()[mProgressType]) {
            case CIRCLES:
                initCirclesProgress();
                break;
        }
    }

    private void initCirclesProgress() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor(ProgressColors.RED.toString()));
        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setColor(Color.parseColor(ProgressColors.BLUE.toString()));
        mPaint3 = new Paint();
        mPaint3.setAntiAlias(true);
        mPaint3.setColor(Color.parseColor(ProgressColors.BLUE.toString()));
        
        pfdfilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        switch (ProgressTypes.values()[mProgressType]) {
            case CIRCLES:
                measureCircleProgress();
                break;
        }
    }

    private void measureCircleProgress() {
        mDiameter = Math.min(getWidth(), getHeight());
        mHalf = mDiameter / 2;
        mOval.set(0, 0, mDiameter, mDiameter);
        mSpeed = mDiameter / 40;
        mControlPointMinimun = -mDiameter / 6;
        mContorlPointMaximun = mDiameter + mDiameter / 6;
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	
    	canvas.setDrawFilter(pfdfilter);
    	
        switch (ProgressTypes.values()[mProgressType]){
            case CIRCLES:
                makeCirclesProgress(canvas);
                break;
            case NEXUS_CIRCLES:
                //http://ikslawok.free.fr/my_nexus_fr/nexus_5/bootanimation_nexus_5.gif
                //TODO: Nexus 5 circles boot progress animation (Just the circles bouncing)
                break;
            case NEXUS_CROSS:
                //http://devfest.gdgthess.org/wp-content/uploads/2013/11/nexus-4-boot-animation.gif
                //TODO: Galaxy nexus shinny cross boot animation
                break;
            case NEXUS_ROATION_CROSS:
                //http://deathlyspectator.files.wordpress.com/2012/03/sampleb.gif
                //TODO: Nexus one rotation cross animation (Just the cross rotation)
                break;
        }
        super.onDraw(canvas);
    }

    private void makeCirclesProgress(Canvas canvas) {
        if (mCurrentState == ProgressStates.NOT_FOLDING) {
            notFold(canvas);
        } else if (mCurrentState == ProgressStates.FOLDING_DOWN) {
            mY = foldingToMaximun(mY,ProgressStates.FOLDING_DOWN,Color.parseColor(ProgressColors.RED.toString()));
            drawYMotion(canvas);
        } else if (mCurrentState == ProgressStates.FOLDING_RIGHT) {
            mX = foldingToMaximun(mX,ProgressStates.FOLDING_RIGHT,Color.parseColor(ProgressColors.BLUE.toString()));
            drawXMotion(canvas);
        }else if (mCurrentState == ProgressStates.FOLDING_UP) {
            mY = foldingToMinimun(mY, ProgressStates.FOLDING_UP, Color.parseColor(ProgressColors.GREEN.toString()));
            drawYMotion(canvas);
        } else if (mCurrentState == ProgressStates.FOLDING_LEFT) {
            mX = foldingToMinimun(mX, ProgressStates.FOLDING_LEFT, Color.parseColor(ProgressColors.YELLOW.toString()));
            drawXMotion(canvas);
        }

        if (mCurrentState != ProgressStates.NOT_FOLDING) {
            canvas.drawPath(mPath, mPaint3);
        }
    }

    private void notFold(Canvas canvas) {
        canvas.drawArc(mOval, 90, 180, true, mPaint);
        canvas.drawArc(mOval, -270, -180, true, mPaint2);
        mUnfoldCounter++;
        if (mUnfoldCounter == 10) {
            mUnfoldCounter = 0;
            if (mLastState == ProgressStates.FOLDING_DOWN) {
                mPaint2.setColor(Color.parseColor(ProgressColors.YELLOW.toString()));
                mCurrentState = ProgressStates.FOLDING_LEFT;
            } else if (mLastState == ProgressStates.FOLDING_LEFT) {
                mPaint2.setColor(Color.parseColor(ProgressColors.GREEN.toString()));
                mCurrentState = ProgressStates.FOLDING_UP;
            } else if (mLastState == ProgressStates.FOLDING_UP) {
                mPaint.setColor(Color.parseColor(ProgressColors.BLUE.toString()));
                mCurrentState = ProgressStates.FOLDING_RIGHT;
            } else if (mLastState == ProgressStates.FOLDING_RIGHT) {
                mPaint.setColor(Color.parseColor(ProgressColors.RED.toString()));
                mCurrentState = ProgressStates.FOLDING_DOWN;
            }
        }
    }

    private int foldingToMaximun(int axisValue, ProgressStates lastState, int nextColor) {
        //Start
        if (axisValue <= mControlPointMinimun) {
            axisValue=mControlPointMinimun;
            mPaint3.setAlpha(FOLDED_TRANSPARENCY);
        }

        //Half
        if (axisValue >= mHalf) {
            mPaint3.setColor(nextColor);
            mPaint3.setAlpha(FOLDED_TRANSPARENCY);
        }
        //End
        if (axisValue >= mContorlPointMaximun) {
            axisValue = mContorlPointMaximun;
            mCurrentState = ProgressStates.NOT_FOLDING;
            mLastState = lastState;
            mPaint2.setColor(mPaint3.getColor());
            mPaint2.setAlpha(OPAQUE);

        }

        return axisValue+mSpeed;
    }

    private int foldingToMinimun(int axisValue, ProgressStates lastState, int nextColor) {

        //Start
        if (axisValue >= mContorlPointMaximun) {
            axisValue = mContorlPointMaximun;
            mPaint3.setAlpha(FOLDED_TRANSPARENCY);
        }

        //Half
        if (axisValue <= mHalf) {
            mPaint3.setColor(nextColor);
            mPaint3.setAlpha(FOLDED_TRANSPARENCY);
        }

        //End
        if (axisValue <= mControlPointMinimun) {
            axisValue = mControlPointMinimun;
            mCurrentState = ProgressStates.NOT_FOLDING;
            mLastState = lastState;
            mPaint.setColor(mPaint3.getColor());
            mPaint.setAlpha(OPAQUE);
        }

        return axisValue - mSpeed;
    }

    private void drawXMotion(Canvas canvas) {
        canvas.drawArc(mOval, 90, 180, true, mPaint);
        canvas.drawArc(mOval, -270, -180, true, mPaint2);
        mPath.reset();
        mPath.moveTo(mHalf, 0);
        mPath.cubicTo(mX, 0, mX, mDiameter, mHalf, mDiameter);
    }

    private void drawYMotion(Canvas canvas) {
        canvas.drawArc(mOval, 0, -180, true, mPaint);
        canvas.drawArc(mOval, -180, -180, true, mPaint2);
        mPath.reset();
        mPath.moveTo(0, mHalf);
        mPath.cubicTo(0, mY, mDiameter, mY, mDiameter, mHalf);
    }
}

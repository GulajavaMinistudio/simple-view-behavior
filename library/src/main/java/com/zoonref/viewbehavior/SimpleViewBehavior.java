package com.zoonref.viewbehavior;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zoonooz on 12/30/15 AD.
 * Easy CoordinatorLayout Behavior
 */
public class SimpleViewBehavior extends PercentageViewBehavior<View> {

    private int mStartX;
    private int mStartY;
    private int mStartWidth;
    private int mStartHeight;
    private int mStartBackgroundColor;
    private float mStartAlpha;
    private float mStartRotateX;
    private float mStartRotateY;

    public int targetX;
    public int targetY;
    public int targetWidth;
    public int targetHeight;
    public int targetBackgroundColor;
    public float targetAlpha;
    public float targetRotateX;
    public float targetRotateY;

    /**
     * Creates a new behavior whose parameters come from the specified context and
     * attributes set.
     *
     * @param context the application environment
     * @param attrs   the set of attributes holding the target and animation parameters
     */
    public SimpleViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        // setting values
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EasyCoordinatorView);
        targetX = a.getDimensionPixelOffset(R.styleable.EasyCoordinatorView_targetX, UNSPECIFIED_INT);
        targetY = a.getDimensionPixelOffset(R.styleable.EasyCoordinatorView_targetY, UNSPECIFIED_INT);
        targetWidth = a.getDimensionPixelOffset(R.styleable.EasyCoordinatorView_targetWidth, UNSPECIFIED_INT);
        targetHeight = a.getDimensionPixelOffset(R.styleable.EasyCoordinatorView_targetHeight, UNSPECIFIED_INT);
        targetBackgroundColor = a.getColor(R.styleable.EasyCoordinatorView_targetBackgroundColor, UNSPECIFIED_INT);
        targetAlpha = a.getFloat(R.styleable.EasyCoordinatorView_targetAlpha, UNSPECIFIED_FLOAT);
        targetRotateX = a.getFloat(R.styleable.EasyCoordinatorView_targetRotateX, UNSPECIFIED_FLOAT);
        targetRotateY = a.getFloat(R.styleable.EasyCoordinatorView_targetRotateY, UNSPECIFIED_FLOAT);
        a.recycle();
    }

    @Override
    void prepare(CoordinatorLayout parent, View child, View dependency) {
        super.prepare(parent, child, dependency);

        mStartX = (int) child.getX();
        mStartY = (int) child.getY();
        mStartWidth = child.getWidth();
        mStartHeight = child.getHeight();
        mStartAlpha = child.getAlpha();
        mStartRotateX = child.getRotationX();
        mStartRotateY = child.getRotationY();

        // only set the start background color when the background is color drawable
        Drawable background = child.getBackground();
        if (background instanceof ColorDrawable) {
            mStartBackgroundColor = ((ColorDrawable) background).getColor();
        }

        // if parent fitsSystemWindows is true, add status bar height to target y if specified
        if (Build.VERSION.SDK_INT > 16 && parent.getFitsSystemWindows() && targetY != UNSPECIFIED_INT) {
            int result = 0;
            Resources resources = parent.getContext().getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId);
            }
            targetY += result;
        }
    }

    @Override
    void updateViewWithPercent(View child, float percent) {
        float newX = targetX == UNSPECIFIED_INT ? 0 : (targetX - mStartX) * percent;
        float newY = targetY == UNSPECIFIED_INT ? 0 : (targetY - mStartY) * percent;

        // set scale
        if (targetWidth != UNSPECIFIED_INT || targetHeight != UNSPECIFIED_INT) {
            float newWidth = mStartWidth + ((targetWidth - mStartWidth) * percent);
            float newHeight = mStartHeight + ((targetHeight - mStartHeight) * percent);

            child.setScaleX(newWidth / mStartWidth);
            child.setScaleY(newHeight / mStartHeight);
            // make up position for scale change
            newX -= (mStartWidth - newWidth) / 2;
            newY -= (mStartHeight - newHeight) / 2;
        }

        // set new position
        child.setTranslationX(newX);
        child.setTranslationY(newY);

        // set alpha
        if (targetAlpha != UNSPECIFIED_FLOAT) {
            child.setAlpha(mStartAlpha + (targetAlpha - mStartAlpha) * percent);
        }

        // set background color
        if (targetBackgroundColor != UNSPECIFIED_INT && mStartBackgroundColor != 0) {
            ArgbEvaluator evaluator = new ArgbEvaluator();
            int color = (int) evaluator.evaluate(percent, mStartBackgroundColor, targetBackgroundColor);
            child.setBackgroundColor(color);
        }

        // set rotation
        if (targetRotateX != UNSPECIFIED_FLOAT) {
            child.setRotationX(mStartRotateX + (targetRotateX - mStartRotateX) * percent);
        }
        if (targetRotateY != UNSPECIFIED_FLOAT) {
            child.setRotationX(mStartRotateY + (targetRotateY - mStartRotateY) * percent);
        }

        child.requestLayout();
    }
}

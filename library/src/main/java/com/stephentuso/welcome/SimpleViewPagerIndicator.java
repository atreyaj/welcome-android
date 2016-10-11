package com.stephentuso.welcome;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by stephentuso on 11/16/15.
 * A quick and dirty ViewPager indicator.
 */
public class SimpleViewPagerIndicator extends View implements ViewPager.OnPageChangeListener {

    private Paint paint;

    private int currentPageColor = 0x99ffffff;
    private int otherPageColor = 0x22000000;

    private int totalPages = 0;
    private int currentPage = 0;
    private int displayedPage = 0;
    private float currentPageOffset = 0;

    //Used to show correct position when rtl and swipeToDismiss
    private int pageIndexOffset = 0;

    private int spacing = 16;
    private int size = 4;

    private boolean animated = false;
    private boolean isRtl = false;

    public SimpleViewPagerIndicator(Context context) {
        this(context, null);
    }

    public SimpleViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.welcomeIndicatorStyle);
    }

    public SimpleViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleViewPagerIndicator, defStyleAttr, 0);

            currentPageColor = a.getColor(R.styleable.SimpleViewPagerIndicator_currentPageColor, currentPageColor);
            otherPageColor = a.getColor(R.styleable.SimpleViewPagerIndicator_indicatorColor, otherPageColor);
            animated = a.getBoolean(R.styleable.SimpleViewPagerIndicator_animated, animated);

            a.recycle();
        }

        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);

        float density = context.getResources().getDisplayMetrics().density;
        spacing *= density;
        size *= density;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (animated) {
            setPosition(position);
            currentPageOffset = canShowAnimation() ? 0 : positionOffset;
            invalidate();
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (!animated) {
            setPosition(position);
            currentPageOffset = 0;
            invalidate();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //Not used
    }

    private Point getCenter() {
        return new Point((getRight() - getLeft())/2, (getBottom() - getTop())/2);
    }

    public void setPosition(int position) {
        currentPage = position + pageIndexOffset;
        displayedPage = isRtl ? Math.max(currentPage, 0) : Math.min(currentPage, totalPages - 1);
        invalidate();
    }

    public int getPosition() {
        return currentPage;
    }

    public int getDisplayedPosition() {
        return displayedPage;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
        invalidate();
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setRtl(boolean rtl) {
        this.isRtl = rtl;
    }

    public boolean isRtl() {
        return isRtl;
    }

    public void setPageIndexOffset(int offset) {
        this.pageIndexOffset = offset;
    }

    public int getPageIndexOffset() {
        return pageIndexOffset;
    }

    private boolean canShowAnimation() {
        return isRtl ? currentPage < 0 : currentPage == totalPages - 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Point center = getCenter();
        float startX = getFirstDotPosition(center.x);

        paint.setColor(otherPageColor);
        for (int i = 0; i < totalPages; i++) {
            canvas.drawCircle(startX + spacing * i, center.y, size, paint);
        }

        paint.setColor(currentPageColor);
        canvas.drawCircle(startX + (spacing * (displayedPage + currentPageOffset)), center.y, size, paint);
    }

    private float getFirstDotPosition(float centerX) {
        float centerIndex = totalPages % 2 == 0 ? (totalPages -1)/2 : totalPages /2;
        float spacingMult = (float) Math.floor(centerIndex);
        if (totalPages % 2 == 0)
            spacingMult += 0.5;
        return centerX - (spacing * spacingMult);
    }

}
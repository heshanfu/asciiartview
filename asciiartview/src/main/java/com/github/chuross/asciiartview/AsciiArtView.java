package com.github.chuross.asciiartview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class AsciiArtView extends View {

    private static final int DEFAULT_TEXT_SIZE = 40;
    private Paint defaultAsciiArtPaint = new Paint();
    private Paint asciiArtPaint = new Paint();
    private String asciiArt;
    private float scale;

    public AsciiArtView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public AsciiArtView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AsciiArtView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        defaultAsciiArtPaint.setTextSize(DEFAULT_TEXT_SIZE);
        defaultAsciiArtPaint.setColor(Color.BLACK);
        defaultAsciiArtPaint.setAntiAlias(true);

        if (attrs == null) return;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AsciiArtView, defStyleAttr, 0);
        defaultAsciiArtPaint.setColor(typedArray.getColor(R.styleable.AsciiArtView_aav_color, Color.BLACK));
        setTypefaceFromAttribute(context, typedArray.getString(R.styleable.AsciiArtView_aav_typeface));
        typedArray.recycle();
    }

    private void setTypefaceFromAttribute(Context context, String typefaceName) {
        if (TextUtils.isEmpty(typefaceName)) return;
        setTypeface(Typeface.createFromAsset(context.getAssets(), typefaceName));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        asciiArtPaint.reset();

        if (asciiArt == null) return;

        asciiArtPaint.set(defaultAsciiArtPaint);

        Rect asciiArtRect = getAsciiArtRect();

        scale = (float) getMeasuredWidth() / (float) asciiArtRect.width();

        float width = getMeasuredWidth();
        float height = asciiArtRect.height() * scale;

        setMeasuredDimension(Math.round(width), Math.round(height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (asciiArt == null) return;

        canvas.scale(scale, scale);

        int y = getPaddingTop();
        String[] lines = getLines();
        for (String line : lines) {
            Rect textBounds = getTextBounds(line);
            y += line.isEmpty() ? DEFAULT_TEXT_SIZE : textBounds.height();

            canvas.drawText(line, getPaddingLeft(), y, asciiArtPaint);
        }
    }

    @NonNull
    private Rect getAsciiArtRect() {
        Rect asciiArtRect = new Rect();

        if (asciiArt == null) return asciiArtRect;

        String[] lines = getLines();
        if (lines.length == 0) return asciiArtRect;

        int width = 0;
        int height = 0;
        for (String line : lines) {
            Rect textBounds = getTextBounds(line);
            width = Math.max(width, textBounds.width());
            height += line.isEmpty() ? DEFAULT_TEXT_SIZE : textBounds.height();
        }

        width = getPaddingLeft() + width + getPaddingRight();
        height = getPaddingTop() + height + getPaddingBottom();
        asciiArtRect.set(0, 0, width, height);

        return asciiArtRect;
    }

    private String[] getLines() {
        return asciiArt != null ? asciiArt.split("\n") : new String[] {};
    }

    private Rect getTextBounds(String text) {
        Paint.FontMetrics metrics = asciiArtPaint.getFontMetrics();
        float width = asciiArtPaint.measureText(text);
        float height =  Math.abs(metrics.top) + metrics.bottom;
        return new Rect(0, 0, Math.round(width), Math.round(height));
    }

    public void setAsciiArt(String asciiArt) {
        this.asciiArt = asciiArt;
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        defaultAsciiArtPaint.setTypeface(typeface);
        invalidate();
    }

    public void setTextColor(@ColorInt int color) {
        defaultAsciiArtPaint.setColor(color);
        invalidate();
    }
}

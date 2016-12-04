package com.triplesnake.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.triplesnake.game.numbersearch.R;

public class MenuButton extends View {
	private String mLeft = "   ", mCenter = "BUTTON", mRight = "   ";
	private float mCellWidth, mCellHeight;

	private Paint mPaint;
	private Rect bounds = new Rect();
	private RectF clickArea;

	private OnClickListener mOnClickListener;

	private int pressColor, normalColor;
	private boolean isPressed = false;

	public MenuButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.MenuButton, defStyle, 0);
		init(a);
	}

	public MenuButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.MenuButton, 0, 0);
		init(a);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		mOnClickListener = l;
	}
	
	public void setText(String left, String center, String right) {
		mLeft = left;
		mCenter = center;
		mRight = right;
		invalidate();
	}
	
	public void setSelector(int normalColor, int pressedColor){
		this.normalColor = normalColor;
		this.pressColor = pressedColor;
		invalidate();
	}

	private void init(TypedArray a) {
		try {
			mLeft = a.getString(R.styleable.MenuButton_leftText);
			mCenter = a.getString(R.styleable.MenuButton_centerText);
			mRight = a.getString(R.styleable.MenuButton_rightText);
			normalColor = a.getColor(R.styleable.MenuButton_normalColor, Color.GREEN);
			pressColor = a.getColor(R.styleable.MenuButton_pressedColor, Color.GRAY);
		} finally {
			a.recycle();
		}

		mPaint = new Paint();
		if (isInEditMode()) {
			mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		} else {
			Typeface arial = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/arial.ttf");
			Typeface bold = Typeface.create(arial, Typeface.ITALIC);
			mPaint.setTypeface(bold);
		}
		mPaint.setFakeBoldText(true);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setStyle(Style.FILL);
		mPaint.setAntiAlias(true);

		clickArea = new RectF();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (event.getX() >= clickArea.left && event.getX() <= clickArea.right) {
				isPressed = true;
				invalidate();
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (isPressed) {
				if (!(event.getX() >= clickArea.left && event.getX() <= clickArea.right)) {
					isPressed = false;
					invalidate();
				} else
					return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isPressed) {
				isPressed = false;
				invalidate();
				if (event.getX() >= clickArea.left && event.getX() <= clickArea.right) {
					if (mOnClickListener != null)
						mOnClickListener.onClick(this);
					return true;
				}
			}
			isPressed = false;
			break;
		}
		return false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int length = mLeft.length() + mCenter.length() + mRight.length();
		int w = this.getMeasuredWidth();
		mCellWidth = w / (float) length;
		mCellHeight = 1.5f * mCellWidth;
		mPaint.setTextSize(mCellWidth);
		setMeasuredDimension(w, (int) mCellHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		String text = mLeft + mCenter + mRight;
		int color;
		String chr;
		mPaint.setColor(0xffff0000);
		clickArea.left = mCellWidth * mLeft.length();
		clickArea.top = 0;
		clickArea.right = clickArea.left + mCenter.length() * mCellWidth;
		clickArea.bottom = mCellHeight;
		if(isPressed)
			mPaint.setColor(pressColor);
		else
			mPaint.setColor(normalColor);
		canvas.drawRoundRect(clickArea, mCellHeight / 2, mCellHeight / 2, mPaint);
		for (int i = 0; i < text.length(); i++) {
			chr = text.charAt(i) + "";
			color = i - mLeft.length() >= 0 && i - mLeft.length() < mCenter.length() ? 0xff000000
					: 0xff888888;
			mPaint.setColor(color);
			mPaint.getTextBounds(chr, 0, 1, bounds);
			canvas.drawText(chr, mCellWidth * (i + 0.5f), mCellHeight * 0.5f
					+ (mCellHeight - bounds.height()) / 2f, mPaint);
		}
	}
}

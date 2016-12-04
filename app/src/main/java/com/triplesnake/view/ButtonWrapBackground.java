package com.triplesnake.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;

import com.triplesnake.game.numbersearch.R;

public class ButtonWrapBackground extends Button {

	private float mBackgroundRatio = 1;
	
	public ButtonWrapBackground(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode())
			return;
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.ButtonWrapBackground, 0, 0);
		try {
			TypedValue typedValue = new TypedValue();
			getResources().getValue(R.dimen.button_wrap_background_ratio, typedValue,
					true);
			float ratio = typedValue.getFloat();
			mBackgroundRatio = a.getFloat(R.styleable.ButtonWrapBackground_ratio,
					ratio);
		} finally {
			a.recycle();
		}
	}

	public ButtonWrapBackground(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode())
			return;
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.ButtonWrapBackground, 0, 0);
		try {
			TypedValue typedValue = new TypedValue();
			getResources().getValue(R.dimen.button_wrap_background_ratio, typedValue,
					true);
			float ratio = typedValue.getFloat();
			mBackgroundRatio = a.getFloat(R.styleable.ButtonWrapBackground_ratio,
					ratio);
		} finally {
			a.recycle();
		}
	}

	public ButtonWrapBackground(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		float bgWidth = getBackground().getIntrinsicWidth();
		float bgHight = getBackground().getIntrinsicHeight();
		
		if(heightSize == 0 || bgHight == 00 || bgWidth == 0) {
			setMeasuredDimension(widthSize, heightSize);
			return;
		}
		
		int width, height;
		if (widthMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.EXACTLY) {
			if (widthSize / heightSize > bgWidth / bgHight) {
				height = (int) heightSize;
				width = (int) (heightSize * bgWidth / bgHight);
			} else {
				width = (int) widthSize;
				height = (int) (widthSize * bgHight / bgWidth);
			}
		} else {
			width = (int) (bgWidth * mBackgroundRatio);
			height = (int) (bgHight * mBackgroundRatio);
		}

		setMeasuredDimension(width, height);
	}
}

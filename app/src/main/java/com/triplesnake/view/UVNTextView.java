package com.triplesnake.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class UVNTextView extends TextView {

	public UVNTextView(Context context) {
		super(context);
	}

	public UVNTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UVNTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setTypeface(Typeface tf, int style) {
		if (isInEditMode())
			super.setTypeface(tf, style);
		else
			super
					.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
							"fonts/UVNAiCapNang.TTF"), style);
	}

}

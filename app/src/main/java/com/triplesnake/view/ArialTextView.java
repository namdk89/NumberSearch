package com.triplesnake.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ArialTextView extends TextView {

	public ArialTextView(Context context) {
		super(context);
	}

	public ArialTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ArialTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setTypeface(Typeface tf, int style) {
		if (isInEditMode())
			super.setTypeface(tf, style);
		else
			super
					.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
							"fonts/arial.ttf"), style);
	}

}

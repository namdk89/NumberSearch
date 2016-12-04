package com.triplesnake.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class RadioButtonNoPersistentText extends RadioButton {

	public RadioButtonNoPersistentText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public RadioButtonNoPersistentText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RadioButtonNoPersistentText(Context context) {
		super(context);
	}
	
	@Override
  public void onRestoreInstanceState(final Parcelable state) {

      final CharSequence text = getText();

      super.onRestoreInstanceState(state);

      setText(text);

  }

}

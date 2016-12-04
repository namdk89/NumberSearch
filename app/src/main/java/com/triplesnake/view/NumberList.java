package com.triplesnake.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;

public class NumberList extends ArialTextView {
	private Paint paint = new Paint();

	private float offset = 0;
	private float horizontalOffset = 0;
	private float verticalOffset = 0;
	private float horizontalFontOffset = 0;
	private float dirtyRegionWidth = 0;

	private Bitmap cache = null;
	private boolean cacheEnabled = false;

	private ArrayList<NumberInfo> mNumbers = new ArrayList<NumberInfo>();
	private ArrayList<Float> mSizes = new ArrayList<Float>();

	public NumberList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public NumberList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NumberList(Context context) {
		super(context);
	}

	@Override
	public void setDrawingCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	public void setWordList(ArrayList<NumberInfo> numbers) {
		mNumbers = numbers;
		// Pull widget properties
		paint.setColor(getCurrentTextColor());
		paint.setTypeface(getTypeface());
		paint.setTextSize(getTextSize());
		paint.setFakeBoldText(true);
		mSizes = new ArrayList<Float>();
		for (int i = 0; i < mNumbers.size(); i++) {
			mSizes.add(paint.measureText(mNumbers.get(i).mWord));
		}
		
		float space = 0;
		int start = 0;
		String text = "";
		for (int i = 0; i < mNumbers.size(); i++) {
			if (i == start) {
				space = getWidth() - getPaddingLeft() - getPaddingRight();
			}
			if (space - mSizes.get(i) < offset) {
				text += '\n';
				start = i;
				i--;
			} else {
				space -= mSizes.get(i) + offset;
			}
		}
		setText(text);
		
		invalidate();
	}

	public ArrayList<NumberInfo> getWordList(){
		return mNumbers;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if (mNumbers.isEmpty())
			return;
		// Active canas needs to be set
		// based on cacheEnabled
		Canvas activeCanvas = null;
		// Set the active canvas based on
		// whether cache is enabled
		if (cacheEnabled) {
			if (cache != null) {
				// Draw to the OS provided canvas
				// if the cache is not empty
				canvas.drawBitmap(cache, 0, 0, paint);
				return;
			} else {
				// Create a bitmap and set the activeCanvas
				// to the one derived from the bitmap
				cache = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
				activeCanvas = new Canvas(cache);
			}
		} else {
			// Active canvas is the OS
			// provided canvas
			activeCanvas = canvas;
		}

		dirtyRegionWidth = getWidth() - getPaddingLeft() - getPaddingRight();

		// Temp fix
		verticalOffset = horizontalFontOffset = getLineHeight() - 0.5f;
		horizontalOffset = getPaddingLeft();
		offset = paint.measureText("    ");

		float space = 0;
		int start = 0, stop = 0;
		for (int i = 0; i < mNumbers.size(); i++) {
			if (i == start) {
				space = dirtyRegionWidth;
			}
			if (space - mSizes.get(i) < offset) {
				stop = i - 1;
				space = dirtyRegionWidth;
				for (int j = start; j <= stop; j++) {
					space -= mSizes.get(j);
				}
				space = space / (stop - start + 2);
				horizontalOffset = getPaddingLeft() + space;
				for (int j = start; j <= stop; j++) {
					drawWord(activeCanvas, space, j);
				}

				verticalOffset += horizontalFontOffset;
				start = i;
				stop = i;
				i--;
			} else {
				space -= mSizes.get(i) + offset;
			}
		}
		
		stop = mNumbers.size() - 1;
		space = dirtyRegionWidth;
		for (int j = start; j <= stop; j++) {
			space -= mSizes.get(j);
		}
		space = space / (stop - start + 2);
		horizontalOffset = getPaddingLeft() + space;
		for (int j = start; j <= stop; j++) {
			drawWord(activeCanvas, space, j);
		}

		if (cacheEnabled) {
			canvas.drawBitmap(cache, 0, 0, paint);
		}
	}

	private void drawWord(Canvas activeCanvas, float space, int j) {
		String word = mNumbers.get(j).mWord;
		paint.setPathEffect(null);
		paint.setStyle(Style.FILL);
		paint.setTypeface(getTypeface());
		paint.setColor(getCurrentTextColor());
		activeCanvas.drawText(word, horizontalOffset, verticalOffset, paint);
		if (mNumbers.get(j).mIsFound) {
			int height = getLineHeight();
			paint.setPathEffect(new CornerPathEffect(height / 6));
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(height / 3);
			paint.setColor(Color.parseColor("#99000000"));
			Path path = new Path();
			path.moveTo(horizontalOffset, verticalOffset - height / 3);
			path.lineTo(horizontalOffset + mSizes.get(j), verticalOffset - height / 3);
			path.close();
			activeCanvas.drawPath(path, paint);
		}
		horizontalOffset += space + mSizes.get(j);
	}

}
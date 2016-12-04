package com.triplesnake.view;

import android.graphics.Point;

public class NumberInfo {
	public float[] mStart;
	public float[] mStop;
	public int mColor;
	public String mWord;
	public boolean mIsFound;

	public NumberInfo(String word, Point start, Point stop, int color) {
		mWord = word;
		set(start, stop, color);
	}
	
	public void set(Point start, Point stop, int color){
		mColor = color;
		if (start == null || stop == null)
			return;
		mStart = new float[2];
		mStop = new float[2];
		mStart[0] = start.x;
		mStart[1] = start.y;
		mStop[0] = stop.x;
		mStop[1] = stop.y;
		if (stop.x == start.x) {
			mStart[1] -= stop.y > start.y ? 0.125f : -0.125f;
			mStop[1] += stop.y > start.y ? 0.125f : -0.125f;
		} else if (stop.y == start.y) {
			mStart[0] -= stop.x > start.x ? 0.125f : -0.125f;
			mStop[0] += stop.x > start.x ? 0.125f : -0.125f;
		} else {
			mStart[1] -= stop.y > start.y ? 0.125f : -0.125f;
			mStop[1] += stop.y > start.y ? 0.125f : -0.125f;
			mStart[0] -= stop.x > start.x ? 0.125f : -0.125f;
			mStop[0] += stop.x > start.x ? 0.125f : -0.125f;
		}
	}
	
	public void detected(){
		mIsFound = true;
	}
	
	@Override
	public boolean equals(Object o) {
		return mWord.equals(o);
	}
}
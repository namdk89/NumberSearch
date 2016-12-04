package com.triplesnake.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.triplesnake.game.numbersearch.R;

public class NumberBoardView extends View {
	private final ArrayList<Point> DIRECTION = new ArrayList<Point>(
			Arrays.asList(new Point[] { new Point(1, 1), new Point(1, 0),
					new Point(1, -1), new Point(0, 1), new Point(0, -1),
					new Point(-1, 1), new Point(-1, 0), new Point(-1, -1) }));
	private int mBoardHeight = 5;
	private int mBoardWidth = 5;
	private Paint mPaint = new Paint();
	private int mSelectorColor = 0x88ff0000;
	private int mCharColor = 0xff5f5f5f;
	private Path mPath = new Path();
	private CornerPathEffect mPathEffect;
	private char[][] mCharacters = new char[][] { { ' ', ' ', ' ', ' ', ' ' },
			{ ' ', ' ', ' ', ' ', ' ' }, { ' ', ' ', ' ', ' ', ' ' },
			{ ' ', ' ', ' ', ' ', ' ' }, { ' ', ' ', ' ', ' ', ' ' }, };
	private boolean mIsStartSelect = false;
	private float mStartX, mStartY, mStopX, mStopY;
	private float mCellSize;
	private ArrayList<NumberInfo> mResult;
	private ArrayList<Integer> mColors;
	private Random mRandom = new Random();

	private Callback mCallback;

	public interface Callback {
		public void onSelectChange();

		public void onNumberFound(String word, boolean isLast);
		
		public void onSpecialNumberFound(String word);
		
		public boolean isHackEnable();
	}

	public void setCallback(Callback listener) {
		mCallback = listener;
	}

	public NumberBoardView(Context context) {
		super(context);
		init();
	}

	public NumberBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NumberBoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public int getDetectedWord() {
		int detect = 0;
		for (int i = 0; i < mResult.size(); i++) {
			if (mResult.get(i).mIsFound)
				detect++;
		}
		return detect;
	}

	public void setWordList(ArrayList<NumberInfo> words, int width, int height) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		if (metrics.widthPixels / (float) metrics.heightPixels < 0.7) {
			mBoardWidth = width;
			mBoardHeight = height;
		} else {
			mBoardWidth = height;
			mBoardHeight = width;
		}

		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).mWord.length() > height)
				throw new IllegalArgumentException("Size must bigger than longest word");
		}
		orderWords(words);
		generateRandomColors(words.size());
		if (!initCharacterMap(new char[mBoardWidth][mBoardHeight], words, 0)) {
			throw new IllegalArgumentException("Cannot generate grib!");
		}
		// Fill empty cell with random character
		for (int i = 0; i < mBoardWidth; i++) {
			for (int j = 0; j < mBoardHeight; j++) {
				if (mCharacters[i][j] == 0)
					mCharacters[i][j] = "0123456789".charAt(mRandom.nextInt(10));
			}
		}
	}

	public void setTextColor(int color){
		mCharColor = color;
		invalidate();
	}

	private void generateRandomColors(int size) {
		mColors = new ArrayList<Integer>();
		ArrayList<Integer> result = new ArrayList<Integer>();
		int[] colors = getResources().getIntArray(R.array.word_search_colors); 
		for (int i = 0; i < colors.length; i++) {
			result.add(colors[i]);
		}
		while (size > 0) {
			mColors.add(result.remove(mRandom.nextInt(result.size())));
			size--;
		}
	}

	private void orderWords(ArrayList<NumberInfo> words) {
		ArrayList<NumberInfo> copy = new ArrayList<NumberInfo>(words);
		words.clear();
		while (!copy.isEmpty()) {
			int id = 0, maxLength = Integer.MIN_VALUE;
			for (int i = 0; i < copy.size(); i++) {
				if (copy.get(i).mWord.length() > maxLength) {
					id = i;
					maxLength = copy.get(i).mWord.length();
				}
			}
			words.add(copy.remove(id));
		}
	}

	/**
	 * Init character map
	 * 
	 * @param grid
	 * @param words
	 *          true if random position first, false if direction first
	 * @return
	 */
	private boolean initCharacterMap(char[][] grid, ArrayList<NumberInfo> words, int index) {
		if (index == words.size()) {
			mCharacters = grid;
			mResult = words;
			return true;
		}
		NumberInfo word = words.get(index);
		Point start, direct;
		int x = 0, y = 0;
		char[][] gridCopy;

		ArrayList<Point> dir = new ArrayList<Point>(DIRECTION);
		ArrayList<Point> POS = new ArrayList<Point>();
		for (int i = 0; i < mBoardWidth; i++) {
			for (int j = 0; j < mBoardHeight; j++) {
				if (grid[i][j] == 0)
					POS.add(new Point(i, j));
			}
		}

		do {
			direct = dir.remove(mRandom.nextInt(dir.size()));
			ArrayList<Point> pos = new ArrayList<Point>(POS);
			do {
				start = pos.remove(mRandom.nextInt(pos.size()));
				gridCopy = copy(grid);
				boolean success = true;
				for (int i = 0; i < word.mWord.length(); i++) {
					x = start.x + i * direct.x;
					y = start.y + i * direct.y;
					if (x < 0 || x >= mBoardWidth || y < 0 || y >= mBoardHeight) {
						success = false;
						break;
					}
					if (gridCopy[x][y] == 0 || gridCopy[x][y] == word.mWord.charAt(i))
						gridCopy[x][y] = word.mWord.charAt(i);
					else {
						success = false;
						break;
					}
				}
				if (success) {
					word.set(new Point(start), new Point(x, y),
							mColors.get(index));
					if (initCharacterMap(gridCopy, words, index + 1)) {
						return true;
					}
				}
			} while (!pos.isEmpty());
		} while (!dir.isEmpty());
		return false;
	}

	private char[][] copy(char[][] ori) {
		char[][] copy = new char[mBoardWidth][mBoardHeight];
		for (int i = 0; i < mBoardWidth; i++) {
			for (int j = 0; j < mBoardHeight; j++) {
				copy[i][j] = ori[i][j];
			}
		}
		return copy;
	}

	public void init() {
		mPaint.setColor(0xff000000);
		mPaint.setStrokeWidth(0);
		mPaint.setStyle(Style.FILL);
		if (!isInEditMode()) {
			Typeface arial = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/arial.ttf");
			Typeface bold = Typeface.create(arial, Typeface.BOLD);
			mPaint.setTypeface(bold);
			mPaint.setFakeBoldText(true);
		}
		requestLayout();
	}

	public void setSelectorColor(int color) {
		mSelectorColor = color;
		invalidate();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// The square board should fully fill the smaller dimension
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		int width = Math.min(parentWidth, parentHeight);
		int height = (width * mBoardHeight) / mBoardWidth;
		mCellSize = (width / (float) mCharacters.length + height
				/ (float) mCharacters[0].length) / 2;
		mPaint.setTextSize(2 * mCellSize / 3f);
		mPathEffect = new CornerPathEffect(mCellSize / 3f);
		this.setMeasuredDimension(width, height);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsStartSelect = true;
			mStartX = event.getX();
			mStartY = event.getY();
			mStopX = -1;
			sx = (int) (mStartX / mCellSize);
			sy = (int) (mStartY / mCellSize);
			if (mCallback != null)
				mCallback.onSelectChange();
			break;
		case MotionEvent.ACTION_MOVE:
			boolean start = mStopX != -1;
			mStopX = event.getX();
			mStopY = event.getY();
			int oldX = tx,
			oldY = ty;
			invalidSelect();
			if ((oldX != tx || oldY != ty) && start)
				if (mCallback != null)
					mCallback.onSelectChange();
			break;
		case MotionEvent.ACTION_UP:
			invalidSelect();
			onStopSelect();
			mIsStartSelect = false;
			break;
		default:
			break;
		}
		invalidate();
		return true;
	}

	private void onStopSelect() {
		int dx = tx - sx;
		int dy = ty - sy;
		int size = Math.max(Math.abs(dx), Math.abs(dy));
		dx = dx == 0 ? 0 : dx / Math.abs(dx);
		dy = dy == 0 ? 0 : dy / Math.abs(dy);
		String number = "";
		for (int i = 0; i <= size; i++) {
			number += mCharacters[sx + i * dx][sy + i * dy];
		}
		int index = -1;
		for (int i = 0; i < mResult.size(); i++) {
			if(mResult.get(i).equals(number)){
				index = i;
				break;
			}
		}
		if (index >= 0) {
			if (!mResult.get(index).mIsFound) {
				number = new String(mResult.get(index).mWord);
				mResult.get(index).detected();
				if (mCallback != null)
					mCallback.onNumberFound(number, isAllFound());
				invalidate();
			} else
				return;
		}
		
		if (number.length() > 2) {
			boolean specialNumber = true;
			for (int i = 1; i < number.length(); i++) {
				if(number.charAt(i) != number.charAt(0)){
					specialNumber = false;
					break;
				}
			}
			if(specialNumber){
				NumberInfo numberInfo = new NumberInfo(number, new Point(sx, sy),
						new Point(tx, ty), 0x88842dce);
				numberInfo.mIsFound = true;
				mResult.add(numberInfo);
				
				if (mCallback != null)
					mCallback.onSpecialNumberFound(number);
				invalidate();
			}
		}
	}

	public boolean isAllFound(){
		for (int i = 0; i < mResult.size(); i++) {
			if (!mResult.get(i).mIsFound) {
				return false;
			}
		}
		return true;
	}
	
	private int sx, sy, tx, ty;

	private void invalidSelect() {
		sx = (int) (mStartX / mCellSize);
		sy = (int) (mStartY / mCellSize);
		tx = (int) (mStopX / mCellSize);
		ty = (int) (mStopY / mCellSize);
		if (ty < 0)
			ty = 0;
		else if (ty > mCharacters[0].length - 1)
			ty = mCharacters[0].length - 1;
		if (!(tx - sx == ty - sy || tx - sx == sy - ty || tx == sx || ty == sy)) {
			double angle = Math
					.atan2(mStopY - ((int) (mStartY / mCellSize) + 0.5) * mCellSize,
							mStopX - ((int) (mStartX / mCellSize) + 0.5) * mCellSize);
			angle = angle * 180 / Math.PI;
			if (angle >= -22.5 && angle < 22.5) {
				ty = sy;
			} else if (angle >= -67.5 && angle < -22.5) {
				if (sy - tx + sx >= 0)
					ty = sy - tx + sx;
				else
					tx = sy + sx - ty;
			} else if (angle >= -112.5 && angle < -67.5) {
				tx = sx;
				if (ty < 0)
					ty = 0;
				else if (ty > mCharacters[0].length - 1)
					ty = mCharacters[0].length - 1;
			} else if (angle >= -135 && angle < -112.5) {
				if (sx - sy + ty >= 0)
					tx = sx - sy + ty;
				else
					ty = tx - sx + sy;
			} else if (angle >= -157.5 && angle < -135) {
				if (tx - sx + sy >= 0)
					ty = tx - sx + sy;
				else
					tx = ty + sx - sy;
			} else if (angle >= -180 && angle < -157.5) {
				ty = sy;
			} else if (angle < 180 && angle >= 157.5) {
				ty = sy;
			} else if (angle < 157.5 && angle >= 135) {
				if (sy + sx - tx <= mCharacters[0].length - 0.5f)
					ty = sy + sx - tx;
				else
					tx = sy + sx - ty;
			} else if (angle < 135 && angle >= 112.5) {
				if (sy + sx - ty >= 0)
					tx = sy + sx - ty;
				else
					ty = sy + sx - tx;
			} else if (angle < 112.5 && angle >= 67.5) {
				tx = sx;
				if (ty < 0)
					ty = 0;
				else if (ty > mCharacters[0].length - 1)
					ty = mCharacters[0].length - 1;
			} else if (angle < 67.5 && angle >= 45) {
				if (sx + ty - sy <= mCharacters.length - 0.5f)
					tx = sx + ty - sy;
				else
					ty = tx - sx + sy;
			} else {
				if (tx - sx + sy <= mCharacters[0].length - 0.5f)
					ty = tx - sx + sy;
				else
					tx = sx + ty - sy;
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawFound(canvas);
		
		drawCharacters(canvas);
		
		drawSelector(canvas);
	}

	private void drawFound(Canvas canvas) {
		float startX, startY, stopX, stopY;
		mPaint.setPathEffect(mPathEffect);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth((mCellSize * 2) / 3);
		if (mResult != null) {
			for (int i = 0; i < mResult.size(); i++) {
				if (mResult.get(i).mIsFound) {
					mPaint.setColor(mResult.get(i).mColor);
					startX = (mResult.get(i).mStart[0] + 0.5f) * mCellSize;
					startY = (mResult.get(i).mStart[1] + 0.5f) * mCellSize;
					stopX = (mResult.get(i).mStop[0] + 0.5f) * mCellSize;
					stopY = (mResult.get(i).mStop[1] + 0.5f) * mCellSize;
					mPath.reset();
					mPath.moveTo(startX, startY);
					mPath.lineTo(stopX, stopY);
					mPath.close();
					canvas.drawPath(mPath, mPaint);
				}  else if (mCallback != null && mCallback.isHackEnable()){
					mPaint.setColor(Color.GREEN);
					startX = (mResult.get(i).mStart[0] + 0.5f) * mCellSize;
					startY = (mResult.get(i).mStart[1] + 0.5f) * mCellSize;
					stopX = (mResult.get(i).mStop[0] + 0.5f) * mCellSize;
					stopY = (mResult.get(i).mStop[1] + 0.5f) * mCellSize;
					mPath.reset();
					mPath.moveTo(startX, startY);
					mPath.lineTo(stopX, stopY);
					mPath.close();
					canvas.drawPath(mPath, mPaint);
					mPaint.setStyle(Style.FILL);
					mPaint.setColor(Color.BLACK);
					canvas.drawCircle(startX, startY, mCellSize / 3, mPaint);
				}
			}
		}
	}

	private void drawSelector(Canvas canvas) {
		float startX = (sx + 0.5f) * mCellSize;
		float startY = (sy + 0.5f) * mCellSize;
		float stopX = (tx + 0.5f) * mCellSize;
		float stopY = (ty + 0.5f) * mCellSize;
		
		mPaint.setColor(mSelectorColor);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.FILL);
		if (mIsStartSelect) {
			if ((sx != tx || sy != ty) && mStopX >= 0) {
				int dx = tx - sx;
				int dy = ty - sy;
				dx = dx == 0 ? 0 : dx / Math.abs(dx);
				dy = dy == 0 ? 0 : dy / Math.abs(dy);
				startX -= dx * 0.135f * mCellSize;
				startY -= dy * 0.135f * mCellSize;
				stopX += dx * 0.135f * mCellSize;
				stopY += dy * 0.135f * mCellSize;
				mPaint.setStyle(Style.STROKE);
				mPaint.setPathEffect(mPathEffect);
				mPath.reset();
				mPath.moveTo(startX, startY);
				mPath.lineTo(stopX, stopY);
				mPath.close();
				canvas.drawPath(mPath, mPaint);
			} else
				canvas.drawCircle(startX, startY, mCellSize / 3, mPaint);
		}
	}
	
	private void drawCharacters(Canvas canvas) {
		mPaint.setPathEffect(null);
		Rect bounds = new Rect();
		String chr;
		mPaint.setColor(mCharColor);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setStyle(Style.FILL);
		int i = 0;
		int j = 0;
		for (i = 0; i < mCharacters.length; i++) {
			for (j = 0; j < mCharacters[i].length; j++) {
				chr = mCharacters[i][j] + "";
				mPaint.getTextBounds(chr, 0, 1, bounds);
				canvas.drawText(chr, mCellSize * (i + 0.5f),
						mCellSize * (j + 0.5f) + (mCellSize - bounds.height()) / 2f, mPaint);
			}
		}
	}

	public ArrayList<NumberInfo> getCurrentWords(){
		return mResult;
	}
}

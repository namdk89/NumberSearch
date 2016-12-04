package com.triplesnake.game.numbersearch;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;
import com.triplesnake.game.numbersearch.utils.GameInfo;
import com.triplesnake.game.numbersearch.utils.Setting;
import com.triplesnake.numbersearch.menu.AccomplishmentsOutbox;
import com.triplesnake.numbersearch.menu.SettingFragment;
import com.triplesnake.view.NumberBoardView;
import com.triplesnake.view.NumberBoardView.Callback;
import com.triplesnake.view.NumberInfo;
import com.triplesnake.view.NumberList;

public class GameActivity extends FragmentActivity implements Callback,
		GameHelperListener{//}, OnBackStackChangedListener {
	private View btnPause;
	private NumberBoardView mGameView;
	private NumberList mNumbersArea;
	private TextView mTime, mLevel;
	private int mListTextColor = 0xff000000;
	private Handler mHandler = new Handler();
	private Bundle gameArgs;

	private boolean mIsMultiplayer;

	private SoundPool mSoundPool;
	private int mSelectSoundId, mSuccessNormalId, mSuccessSpecialId, mTicktackId;
	private AsyncTask<?, ?, ?> mInitGameTask;

	private Runnable clockRunnable, timeupRunnable;

	private GameHelper gameHelper;
	private AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

	private AdView mAdView;
	private View mCloseAd;
	private InterstitialAd mInterstitialAd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		mGameView = (NumberBoardView) findViewById(R.id.game_view);
		mGameView.setCallback(this);
		mNumbersArea = (NumberList) findViewById(R.id.list_view);
		mNumbersArea.setMovementMethod(new ScrollingMovementMethod());
		mLevel = (TextView) findViewById(R.id.level);
		mTime = (TextView) findViewById(R.id.time);
		btnPause = findViewById(R.id.btn_pause);
		btnPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPauseFragment();
			}
		});

		String gameType = getIntent().getExtras().getString("gameType");

		mIsMultiplayer = gameType.equals(GameInfo.WORD_SEARCH_MULTIPLAYER);
		if (mIsMultiplayer) {
			gameArgs = getIntent().getExtras();
			int level = gameArgs.getInt("level");
			if (level == -1)
				level = new Random().nextInt(GameInfo.MAX_LEVEL + 1);
			gameArgs.putInt("level", level);
			GameInfo.loadMultiPlayerGame(this, gameArgs);
		} else {
			if(gameType.equals(GameInfo.WORD_SEARCH_ENDLESS))
				mTime.setVisibility(View.INVISIBLE);

			gameArgs = GameInfo.loadGame(this, gameType);
		}

		updateClock();
		// if in multi player mode, show opponent name other wise show level
		mLevel.setText(mIsMultiplayer ? gameArgs.getString("p2Name") : getString(
				R.string.level, Integer.toString(gameArgs.getInt("level") + 1)));
		mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		mSelectSoundId = mSoundPool.load(this, R.raw.select, 1);
		mSuccessNormalId = mSoundPool.load(this, R.raw.success_normal, 1);
		mSuccessSpecialId = mSoundPool.load(this, R.raw.success_special, 1);
		mTicktackId = mSoundPool.load(this, R.raw.ticktac, 1);

		gameHelper = new GameHelper(this, BaseGameActivity.CLIENT_GAMES);
		gameHelper.setup(this);
		gameHelper.setMaxAutoSignInAttempts(0);

//		getSupportFragmentManager().addOnBackStackChangedListener(this);

		initAds();
		showBanner();
	}

	public void newGame() {
		GameInfo.resetGame(gameArgs);
		GameInfo.saveGame(this, gameArgs);
		gameArgs = GameInfo.loadGame(this, gameArgs.getString("gameType"));

		mLevel.setText(mIsMultiplayer ? gameArgs.getString("p2Name") : getString(
				R.string.level, Integer.toString(gameArgs.getInt("level") + 1)));

		initGame();
		startGame();
	}

//	@Override
//	public void onBackStackChanged() {
//		FragmentManager fm = getSupportFragmentManager();
//		int count = fm.getBackStackEntryCount();
//		if (count == 0)
//			initGame();
//	}

	private void initAds() {
		// Create banner ads
		mAdView = (AdView) findViewById(R.id.banner);
		mCloseAd = findViewById(R.id.close_banner);
		mCloseAd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hideBanner();
			}
		});
		// Create full screen ads.
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId(getString(R.string.ad_id));
	}

	@Override
	public void onStart(){
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	public void onSignInFailed() {
	}

	@Override
	public void onSignInSucceeded() {
	}

	@Override
	public void onStop(){
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		gameHelper.onActivityResult(request, response, data);
	}

	private void showPauseFragment() {
		stopCountDown();
		showAds(5);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment pause;
		if (mIsMultiplayer) {
			pause = new PauseMultiplayerFragment();
			pause.setArguments(gameArgs);
			GameInfo.saveMultiPlayerGame(this, gameArgs);
		} else {
			GameInfo.saveGame(this, gameArgs);
			pause = new PauseFragment();
		}

		ft.replace(R.id.main, pause, PauseFragment.class.getName());
		ft.addToBackStack(PauseFragment.class.getName());
		ft.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		initGame();
		startGame();
	}

	@Override
	public boolean isHackEnable() {
		return true;
	}

	@Override
	public void onSelectChange() {
		playSound(mSelectSoundId);
	}

	@Override
	public void onNumberFound(String word, boolean isLast) {
		playSound(mSuccessNormalId);
		gameArgs.putInt("totalScore",
				gameArgs.getInt("totalScore") + gameArgs.getInt("levelScore"));
		ArrayList<String> numbers = gameArgs.getStringArrayList("words");
		numbers.remove("-" + word);
		numbers.add("+" + word);
		gameArgs.putStringArrayList("words", numbers);
		mNumbersArea.setWordList(mGameView.getCurrentWords());
		if (isLast) {
			stopCountDown();
			gameArgs.putInt("totalScore",
					gameArgs.getInt("totalScore") + gameArgs.getInt("timeLeft"));
			gameArgs.putInt("score",
					gameArgs.getInt("levelScore") * gameArgs.getInt("wordCount"));
			gameArgs.putInt("bonus",
					gameArgs.getInt("bonus") + gameArgs.getInt("timeLeft"));
			if (mIsMultiplayer) {
				gameArgs.putInt("found", mGameView.getDetectedWord());
				gameOver(new Bundle(gameArgs));
				GameInfo.clearMultiPlayerGame(this, gameArgs);
			} else {
				showLevelResult(new Bundle(gameArgs));
				GameInfo.nextLevel(gameArgs);
				GameInfo.saveGame(this, gameArgs);
			}
		}
	}

	@Override
	public void onSpecialNumberFound(String number) {
		playSound(mSuccessSpecialId);
		ArrayList<String> numbers = gameArgs.getStringArrayList("words");
		numbers.add("+" + number);
		gameArgs.putStringArrayList("words", numbers);

		mNumbersArea.setWordList(mGameView.getCurrentWords());
		Setting.enableTheme(4, this);
		if (number.length() < 7)
			gameArgs.putInt("bonus", gameArgs.getInt("bonus")
					+ new int[] { 25, 125, 500, 1500 }[number.length() - 3]);
		else
			gameArgs.putInt("bonus", gameArgs.getInt("bonus") + 1500);
		if (number.contains("777"))
			unlockLuckyAchievement();
	}

	public void unlockLuckyAchievement() {
		mOutbox.mLucky = true;
		pushAccomplishments();
	}

	private void pushAccomplishments() {
		if (gameHelper.isSignedIn())
			mOutbox.pushAccomplishments(this, gameHelper.getApiClient());
		else
			mOutbox.saveLocal(this);
	}

	private void showSpinner(){
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
		findViewById(R.id.main).setBackgroundColor(0x90000000);
	}

	private void dismissSpinner(){
		findViewById(R.id.progress).setVisibility(View.GONE);
		findViewById(R.id.main).setBackgroundColor(0x00000000);
	}

	public void initGame(){
		mInitGameTask = new AsyncTask<Void, Void, ArrayList<NumberInfo>>(){

			protected void onPreExecute() {
				super.onPreExecute();
				loadTheme();
				showSpinner();
			};

			@Override
			protected ArrayList<NumberInfo> doInBackground(Void... params) {
				Random random = new Random();
				int length = gameArgs.getInt("gridWidth") - 1;
				ArrayList<NumberInfo> numberInfos = new ArrayList<NumberInfo>();
				ArrayList<String> numbers = gameArgs.getStringArrayList("words");
				String word;
				while (numbers.size() < gameArgs.getInt("wordCount")) {
					word = Integer.toString(random.nextInt(9) + 1);
					for (int i = length - 2; i >= 0; i--) {
						word += random.nextInt(10);
					}
					boolean pass = false;
					// Check same digits
					for (int i = 1; i < word.length(); i++) {
						if (word.charAt(i) != word.charAt(0)) {
							pass = true;
							break;
						}
					}
					if (pass) {
						for (int j = 0; j < numbers.size(); j++) {
							if (numbers.get(j).contains(word)
									|| word.contains(numbers.get(j))) {
								pass = false;
								break;
							}
						}
						if (pass) {
							numbers.add("-" + word);
							if (length > 5)
								length--;
							else
								length = Math.random() > 0.5 ? 5 : 4;
						}
					}
				}
				NumberInfo temp;
				for (int i = 0; i < numbers.size(); i++) {
					temp = new NumberInfo(numbers.get(i).substring(1), null, null, 0);
					temp.mIsFound = numbers.get(i).charAt(0) == '+';
					numberInfos.add(temp);
				}
				return numberInfos;
			}

			protected void onPostExecute(ArrayList<NumberInfo> numberInfos) {
				super.onPostExecute(numberInfos);
				mGameView.setWordList(numberInfos, gameArgs.getInt("gridWidth"),
						gameArgs.getInt("gridHeight"));
				dismissSpinner();
				mGameView.setCallback(GameActivity.this);
				mGameView.init();
				mNumbersArea.setWordList(mGameView.getCurrentWords());
				startGame();
			};

		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void nextLevel() {
		mLevel.setText(mIsMultiplayer ? gameArgs.getString("p2Name") : getString(
				R.string.level, Integer.toString(gameArgs.getInt("level") + 1)));
		initGame();
		startGame();
	}

	private void hidePanels() {
		FragmentManager fm = getSupportFragmentManager();
		fm.popBackStack(ResultFragment.class.getName(),
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		fm.popBackStack(SettingFragment.class.getName(),
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		fm.popBackStack(PauseFragment.class.getName(),
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		fm.popBackStack(PauseMultiplayerFragment.class.getName(),
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public void onPause() {
		super.onPause();
		stopCountDown();
		if (mInitGameTask != null)
			mInitGameTask.cancel(true);
		GameInfo.saveGame(this, gameArgs);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mSoundPool.release();
	}

	private void startGame(){
		gameArgs.putBoolean("continue", true);
		hidePanels();

		if(gameArgs.getString("gameType").equals(GameInfo.WORD_SEARCH_ENDLESS))
			return;

		stopCountDown();
		if (clockRunnable == null)
			clockRunnable = new Runnable() {

				@Override
				public void run() {
					gameArgs.putInt("timeLeft", gameArgs.getInt("timeLeft") - 1);
					if (gameArgs.getInt("timeLeft") <= 10)
						playSound(mTicktackId);
					updateClock();

					if (gameArgs.getInt("timeLeft") > 0)
						mHandler.postDelayed(this, 1000);
					else {
						if (timeupRunnable == null)
							timeupRunnable = new Runnable() {

								@Override
								public void run() {
									updateClock();
									onTimeUp();
								}
							};
						mHandler.postDelayed(timeupRunnable, 1000);
					}
				}
			};
		mHandler.postDelayed(clockRunnable, 1000);
	}

	private void onTimeUp(){
		if (mIsMultiplayer) {
			gameArgs.putInt("found", mGameView.getDetectedWord());
			gameOver(gameArgs);
			GameInfo.clearMultiPlayerGame(this, gameArgs);
		} else {
			showLevelResult(new Bundle(gameArgs));
			GameInfo.resetGame(gameArgs);
			GameInfo.saveGame(this, gameArgs);
		}
	}

	protected void stopCountDown() {
		mHandler.removeCallbacks(clockRunnable);
		mHandler.removeCallbacks(timeupRunnable);
	}

	public void playSound(int soundId) {
		if (Setting.isSoundEnable(this)) {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	        float leftVolume = curVolume/maxVolume;
	        float rightVolume = curVolume/maxVolume;
			mSoundPool.play(soundId, leftVolume, rightVolume, 0, 0, 1);
		}
	}

	private void updateClock(){
		// update countdown
		if(gameArgs.getString("gameType").equals(GameInfo.WORD_SEARCH_NORMAL)) {
			String time = gameArgs.getInt("timeLeft") + "";
			if (gameArgs.getInt("timeLeft") < 100)
				time = "0" + time;
			if (gameArgs.getInt("timeLeft") < 10)
				time = "0" + time;
			mTime.setText(time);
		}
	}

	private void loadTheme(){
		int id = Setting.getThemeSelected(this);
		Bundle theme = Setting.getTheme(id, getResources());
		View title = findViewById(R.id.title);

		boolean normalMdpi = false;
		int screenLayout = getResources().getConfiguration().screenLayout;
		normalMdpi = (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		normalMdpi = normalMdpi && (metrics.density * 160f == DisplayMetrics.DENSITY_MEDIUM);
		BitmapDrawable tileTitle = null;
		if (normalMdpi) {
			tileTitle = new BitmapDrawable(getResources(),
					BitmapFactory.decodeResource(getResources(),
							theme.getInt("titleBackground")));
			tileTitle.setTileModeX(Shader.TileMode.REPEAT);
			tileTitle.setTileModeY(Shader.TileMode.REPEAT);
		}

		if (id < 2) {
			title.setBackgroundColor(theme.getInt("titleBackground"));
			findViewById(R.id.root)
					.setBackgroundColor(theme.getInt("gridBackground"));
		} else {
			if (normalMdpi)
				setBackground(title, tileTitle);
			else
				title.setBackgroundResource(theme.getInt("titleBackground"));
			findViewById(R.id.root).setBackgroundResource(
					theme.getInt("gridBackground"));
		}
		mNumbersArea.setBackgroundColor(
				theme.getInt("listBackground"));
		mLevel.setTextColor(theme.getInt("titleTextColor"));
		mTime.setTextColor(theme.getInt("titleTextColor"));
		mTime.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				theme.getInt("icClockResId"), 0);
		btnPause.setBackgroundResource(theme.getInt("icPauseResId"));
		mGameView.setTextColor(theme.getInt("gridTextColor"));
		mGameView.setSelectorColor(theme.getInt("gridSelectorColor"));
		mListTextColor = theme.getInt("listTextColor");
		mNumbersArea.setTextColor(mListTextColor);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setBackground(View v, BitmapDrawable bg){
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk >= Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackground(bg);
		} else {
			v.setBackgroundDrawable(bg);
		}
	}

	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		int size = fm.getBackStackEntryCount();
//		if (size == 0) {
//			showPauseFragment();
//			return;
//		}
		if (size > 0) {
			BackStackEntry entry = fm.getBackStackEntryAt(size - 1);
			Fragment fragment = fm.findFragmentByTag(entry.getName());
			if (fragment instanceof ResultFragment
					|| fragment instanceof ResultMultiplayerFragment)
				return;
		}
		super.onBackPressed();
	}

	public void gameOver(Bundle gameArgs) {
		NumberSearchTurn data = new NumberSearchTurn();
		data.mLevel = gameArgs.getInt("level");

		if (gameArgs.getInt("p1TimeLeft") == -1) {
			gameArgs.putInt("p1Found", gameArgs.getInt("found"));
			gameArgs.putInt("p1TimeLeft", gameArgs.getInt("timeLeft"));
			data.mP1Found = gameArgs.getInt("found");
			data.mP1TimeLeft = gameArgs.getInt("timeLeft");
			Games.TurnBasedMultiplayer.takeTurn(gameHelper.getApiClient(),
					gameArgs.getString("matchId"), data.persist(),
					gameArgs.getString("oppId"));
		} else {
			data.mP1Found = gameArgs.getInt("p1Found");
			data.mP1TimeLeft = gameArgs.getInt("p1TimeLeft");
			data.mP2Found = gameArgs.getInt("found");
			data.mP2TimeLeft = gameArgs.getInt("timeLeft");
			int result;
			if (data.mP2Found + data.mP2TimeLeft > data.mP1Found + data.mP1TimeLeft)
				result = 1;
			else if (data.mP2Found + data.mP2TimeLeft == data.mP1Found
					+ data.mP1TimeLeft)
				result = 0;
			else
				result = -1;
			gameArgs.putInt("p2Found", gameArgs.getInt("found"));
			gameArgs.putInt("p2TimeLeft", gameArgs.getInt("timeLeft"));
			gameArgs.putInt("result", result);
			finishGame(data, gameArgs.getString("matchId"),
					gameArgs.getString("myId"), gameArgs.getString("oppId"), result);
			gameArgs.putInt("p2Found", gameArgs.getInt("p1Found"));
			gameArgs.putInt("p2TimeLeft", gameArgs.getInt("p1TimeLeft"));
			gameArgs.putInt("p1Found", gameArgs.getInt("found"));
			gameArgs.putInt("p1TimeLeft", gameArgs.getInt("timeLeft"));
		}
		showMultiplayerResult(gameArgs);
	}

	public void finishGame(NumberSearchTurn data, String matchId, String myId,
			String oppId, int result) {
		showSpinner();
		int myResult, oppResult;
		switch (result) {
		case 1:
			myResult = ParticipantResult.MATCH_RESULT_WIN;
			oppResult = ParticipantResult.MATCH_RESULT_LOSS;
			break;
		case -1:
			oppResult = ParticipantResult.MATCH_RESULT_WIN;
			myResult = ParticipantResult.MATCH_RESULT_LOSS;
			break;

		default:
			oppResult = ParticipantResult.MATCH_RESULT_TIE;
			myResult = ParticipantResult.MATCH_RESULT_TIE;
			break;
		}
		ParticipantResult my = new ParticipantResult(myId, myResult,
				ParticipantResult.PLACING_UNINITIALIZED);
		ParticipantResult opp = new ParticipantResult(oppId, oppResult,
				ParticipantResult.PLACING_UNINITIALIZED);
		Games.TurnBasedMultiplayer.finishMatch(gameHelper.getApiClient(), matchId,
				data.persist(), my, opp);
	}

	private void showMultiplayerResult(Bundle args) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ResultMultiplayerFragment rmf = new ResultMultiplayerFragment();
		rmf.setArguments(args);
		ft.replace(R.id.main, rmf, ResultMultiplayerFragment.class.getName());
		ft.addToBackStack(ResultMultiplayerFragment.class.getName());
		ft.commit();
	}

	public void showSettingFragment() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		SettingFragment rmf = new SettingFragment();
		ft.replace(R.id.main, rmf, SettingFragment.class.getName());
		ft.addToBackStack(SettingFragment.class.getName());
		ft.commit();
	}

	public void showLevelResult(Bundle gameArgs) {
		showAds(40);
		// check for achievements
		checkForAchievements(gameArgs);
		// update leader boards
		updateLeaderboards(gameArgs);
		// push those accomplishments to the cloud, if signed in
		pushAccomplishments();
		// check for unlock theme
		checkForUnlocks(gameArgs);
		// switch to result screen
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ResultFragment rf = new ResultFragment();
		rf.setArguments(gameArgs);
		ft.replace(R.id.main, rf, ResultFragment.class.getName());
		ft.addToBackStack(ResultFragment.class.getName());
		ft.commit();
	}

	private void showAds(int fullRate) {
		if (new Random().nextInt(100) < fullRate)
			showInterstitial();
//		showBanner();
	}

	private void showBanner() {
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		mAdView.loadAd(adRequestBuilder.build());
		mAdView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				mAdView.setVisibility(View.VISIBLE);
				mCloseAd.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				super.onAdFailedToLoad(errorCode);
				mCloseAd.setVisibility(View.GONE);
				mAdView.setVisibility(View.GONE);
			}
		});
	}

	private void hideBanner() {
		if (mAdView != null) {
			mAdView.setVisibility(View.GONE);
			mAdView.setAdListener(null);
		}
		if (mCloseAd != null)
			mCloseAd.setVisibility(View.GONE);
	}

	public void showInterstitial() {
		if (mInterstitialAd.isLoaded())
			mInterstitialAd.show();
	}

	/**
	 * Update leader boards with the user's score.
	 *
	 *          The score the user got.
	 */
	private void updateLeaderboards(Bundle gameArgs) {
		if(gameArgs.getString("gameType").equals(GameInfo.WORD_SEARCH_NORMAL))
			if (mOutbox.mSinglePlayerScore < gameArgs.getInt("totalScore"))
				mOutbox.mSinglePlayerScore = gameArgs.getInt("totalScore");
	}

	private void checkForAchievements(Bundle gameArgs) {
		if (gameArgs.getInt("timeLeft") > 0) {
			int level = gameArgs.getInt("level");
			if (level >= 4)
				mOutbox.mEasy = true;
			if (level >= 8)
				mOutbox.mMasterNumbers = true;
			if (level >= 10)
				mOutbox.mVisible = true;
		}
	}

	private void checkForUnlocks(Bundle gameArgs) {
		if (gameArgs.getInt("totalScore") >= 1500)
			Setting.enableTheme(5, this);

		if (gameArgs.getInt("timeLeft") > 0) {
			int level = gameArgs.getInt("level");
			if (level > 0)
				Setting.enableTheme(1, this);
			if (level > 2)
				Setting.enableTheme(3, this);
		}
	}
}

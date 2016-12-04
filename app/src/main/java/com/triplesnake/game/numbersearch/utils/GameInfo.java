package com.triplesnake.game.numbersearch.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

public class GameInfo {
	public static final int MAX_LEVEL = 10;
	private static final int[] GRID_WIDTH = new int[] { 7, 7, 7, 8, 8, 8, 9, 9,
			9, 10, 10, 10, 10 };
	private static final int[] GRID_HEIGHT = new int[] { 8, 8, 8, 9, 9, 9, 10,
			10, 10, 11, 11, 11, 12 };
	private static final int[] WORD_COUNT = new int[] { 5, 6, 7, 8, 9, 10, 10, 11,
			11, 12, 12, 13, 13, 14, 15, 16, 16, 17 };
	private static final int[] WORD_SCORE = new int[] { 5, 6, 7, 8, 9, 10,
			11, 12, 13, 14, 15 };
	private static final int[] LEVEL_TIME = new int[] { 60, 90, 120, 150, 180,
			210, 240, 270, 270, 280, 280, 290, 290, 300};

	public static final String WORD_SEARCH_NORMAL = "WORD_SEARCH_NORMAL";
	public static final String WORD_SEARCH_ENDLESS = "WORD_SEARCH_ENDLESS";
	public static final String WORD_SEARCH_MULTIPLAYER = "WORD_SEARCH_MULTIPLAYER";

	public static Bundle loadGame(int level) {
		Bundle data = new Bundle();
		if (level > 10)
			level = 10;
		data.putInt("gridWidth", GRID_WIDTH[level]);
		data.putInt("gridHeight", GRID_HEIGHT[level]);
		data.putInt("wordCount", WORD_COUNT[level]);
		data.putInt("wordScore", WORD_SCORE[level]);
		data.putInt("levelTime", LEVEL_TIME[level]);
		return data;
	}
	
	public static int getLevelScore(int level) {
		return level < WORD_SCORE.length ? WORD_SCORE[level]
				: WORD_SCORE[WORD_SCORE.length - 1];
	}
	
	public static void loadMultiPlayerGame(Activity activity, Bundle gameArgs) {
		SharedPreferences prfs = activity.getSharedPreferences(WORD_SEARCH_MULTIPLAYER,
				Context.MODE_PRIVATE);

		int level, levelScore, gridWidth, gridHeight, wordCount, timeLeft;
		level = gameArgs.getInt("level");
		levelScore = level < WORD_SCORE.length ? WORD_SCORE[level]
				: WORD_SCORE[WORD_SCORE.length - 1];
		gridWidth = level < GRID_WIDTH.length ? GRID_WIDTH[level]
				: GRID_WIDTH[GRID_WIDTH.length - 1];
		gridHeight = level < GRID_HEIGHT.length ? GRID_HEIGHT[level]
				: GRID_HEIGHT[GRID_HEIGHT.length - 1];
		wordCount = level < WORD_COUNT.length ? WORD_COUNT[level]
				: WORD_COUNT[WORD_COUNT.length - 1];
		timeLeft = level < LEVEL_TIME.length ? LEVEL_TIME[level]
				: LEVEL_TIME[LEVEL_TIME.length - 1];
		ArrayList<String> words = new ArrayList<String>();

		gameArgs.putInt("levelScore", levelScore);
		if (prfs.getBoolean(gameArgs.getString("matchId"), false)) {
			words.addAll(prfs.getStringSet("words_" + gameArgs.getString("matchId"),
					new HashSet<String>()));

			gameArgs.putInt("totalScore",
					prfs.getInt("totalScore_" + gameArgs.getString("matchId"), 0));
			gameArgs.putInt("timeLeft",
					prfs.getInt("timeLeft_" + gameArgs.getString("matchId"), timeLeft));
			gameArgs.putStringArrayList("words", words);
		} else {
			gameArgs.putInt("totalScore", 0);
			gameArgs.putInt("timeLeft", timeLeft);
			gameArgs.putStringArrayList("words", words);
		}
		gameArgs.putInt("gridWidth", gridWidth);
		gameArgs.putInt("gridHeight", gridHeight);
		gameArgs.putInt("wordCount", wordCount);
	}
	
	public static void saveMultiPlayerGame(Activity activity, Bundle gameArgs) {
		Editor edt = activity.getSharedPreferences(WORD_SEARCH_MULTIPLAYER,
				Context.MODE_PRIVATE).edit();
		edt.putBoolean(gameArgs.getString("matchId"), true);
		edt.putInt("totalScore_" + gameArgs.getString("matchId"),
				gameArgs.getInt("totalScore"));
		edt.putInt("timeLeft_" + gameArgs.getString("matchId"),
				gameArgs.getInt("timeLeft"));
		edt.putStringSet("words_" + gameArgs.getString("matchId"),
				new HashSet<String>(gameArgs.getStringArrayList("words")));
		edt.commit();
	}
	
	public static void clearMultiPlayerGame(Activity activity, Bundle gameArgs) {
		Editor edt = activity.getSharedPreferences(WORD_SEARCH_MULTIPLAYER,
				Context.MODE_PRIVATE).edit();
		edt.remove(gameArgs.getString("matchId"));
		edt.remove("totalScore_" + gameArgs.getString("matchId"));
		edt.remove("timeLeft_" + gameArgs.getString("matchId"));
		edt.remove("words_" + gameArgs.getString("matchId"));
		edt.commit();
	}
	
	public static void saveGame(Activity activity, Bundle gameArgs) {
		Editor edt = activity.getSharedPreferences(gameArgs.getString("gameType"),
				Context.MODE_PRIVATE).edit();
		edt.putBoolean("continue", gameArgs.getBoolean("continue"));
		edt.putInt("level", gameArgs.getInt("level"));
		edt.putInt("totalScore", gameArgs.getInt("totalScore"));
		edt.putInt("timeLeft", gameArgs.getInt("timeLeft"));
		edt.putStringSet("words",
				new HashSet<String>(gameArgs.getStringArrayList("words")));
		edt.commit();
	}
	
	public static Bundle loadGame(Activity activity, String gameType) {
		SharedPreferences prfs = activity.getSharedPreferences(gameType,
				Context.MODE_PRIVATE);
		ArrayList<String> words = new ArrayList<String>();
		words.addAll(prfs.getStringSet("words", new HashSet<String>()));
		int level, levelScore, gridWidth, gridHeight, wordCount, timeLeft;
		level = prfs.getInt("level", 0);
		levelScore = level < WORD_SCORE.length ? WORD_SCORE[level]
				: WORD_SCORE[WORD_SCORE.length - 1];
		gridWidth = level < GRID_WIDTH.length ? GRID_WIDTH[level]
				: GRID_WIDTH[GRID_WIDTH.length - 1];
		gridHeight = level < GRID_HEIGHT.length ? GRID_HEIGHT[level]
				: GRID_HEIGHT[GRID_HEIGHT.length - 1];
		wordCount = level < WORD_COUNT.length ? WORD_COUNT[level]
				: WORD_COUNT[WORD_COUNT.length - 1];
		timeLeft = level < LEVEL_TIME.length ? LEVEL_TIME[level]
				: LEVEL_TIME[LEVEL_TIME.length - 1];
		
		Bundle data = new Bundle();
		data.putString("gameType", gameType);
		data.putBoolean("continue", prfs.getBoolean("continue", false));
		data.putInt("level", level);
		data.putInt("levelScore", levelScore);
		data.putInt("totalScore", prfs.getInt("totalScore", 0));

		if(gameType.equals(WORD_SEARCH_NORMAL))
			data.putInt("timeLeft", prfs.getInt("timeLeft", timeLeft));
		else
			data.putInt("timeLeft", 0);

		data.putStringArrayList("words", words);
		data.putInt("gridWidth", gridWidth);
		data.putInt("gridHeight", gridHeight);
		data.putInt("wordCount", wordCount);
		return data;
	}
	
	public static void nextLevel(Bundle data){
		int level, levelScore, gridWidth, gridHeight, wordCount, timeLeft;
		level = data.getInt("level") + 1;
		levelScore = level < WORD_SCORE.length ? WORD_SCORE[level]
				: WORD_SCORE[WORD_SCORE.length - 1];
		gridWidth = level < GRID_WIDTH.length ? GRID_WIDTH[level]
				: GRID_WIDTH[GRID_WIDTH.length - 1];
		gridHeight = level < GRID_HEIGHT.length ? GRID_HEIGHT[level]
				: GRID_HEIGHT[GRID_HEIGHT.length - 1];
		wordCount = level < WORD_COUNT.length ? WORD_COUNT[level]
				: WORD_COUNT[WORD_COUNT.length - 1];
		timeLeft = level < LEVEL_TIME.length ? LEVEL_TIME[level]
				: LEVEL_TIME[LEVEL_TIME.length - 1];
		data.putBoolean("continue", true);
		data.putInt("level", level);

		if(data.getString("gameType").equals(WORD_SEARCH_NORMAL))
			data.putInt("timeLeft", timeLeft);
		else
			data.putInt("timeLeft", 0);

		data.putInt("gridWidth", gridWidth);
		data.putInt("gridHeight", gridHeight);
		data.putInt("wordCount", wordCount);
		data.putInt("levelScore", levelScore);
		data.putStringArrayList("words", new ArrayList<String>());
	}
	
	public static void resetGame(Bundle data){
		data.putBoolean("continue", false);
		data.putInt("level", 0);
		data.putInt("levelScore", WORD_SCORE[0]);
		data.putInt("totalScore", 0);

		if(data.getString("gameType").equals(WORD_SEARCH_NORMAL))
			data.putInt("timeLeft", LEVEL_TIME[0]);
		else
			data.putInt("timeLeft", 0);

		data.putStringArrayList("words", new ArrayList<String>());
		data.putInt("gridWidth", GRID_WIDTH[0]);
		data.putInt("gridHeight", GRID_HEIGHT[0]);
		data.putInt("wordCount", WORD_COUNT[0]);
	}
	
	public static String getFunFact(Context ctx) {
		String fact = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(ctx.getAssets().open(
					"number_facts.txt")));
			String line = reader.readLine();
			fact = line;
			int count = 1;
			while (line != null) {
				line = reader.readLine();
				count++;
				if (Math.random() < 1d / count)
					fact = line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fact;
	}
}

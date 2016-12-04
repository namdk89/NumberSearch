package com.triplesnake.numbersearch.menu;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.triplesnake.game.numbersearch.R;

public class AccomplishmentsOutbox {
	private static final String KEY = "com.leostyle.games.findwords.key";
	
	public boolean mWinner = false;
	public boolean mEasy = false;
	public int mFightingStep = 0;
	public boolean mMasterNumbers = false;
	public boolean mVisible = false;
	public boolean mLucky = false;
	public int mSinglePlayerScore = -1;
	
	boolean isEmpty() {
		return !mWinner && !mEasy && mFightingStep == 0 && !mMasterNumbers
				&& !mVisible && !mLucky && mSinglePlayerScore < 0;
	}

	public void pushAccomplishments(Activity activity, GoogleApiClient apiClient) {
		if (mWinner) {
			Games.Achievements.unlock(apiClient, activity
					.getString(R.string.achievement_winner));
			mWinner = false;
		}
		if (mEasy) {
			Games.Achievements.unlock(apiClient, activity
					.getString(R.string.achievement_easy));
			mEasy = false;
		}
		if (mFightingStep > 0) {
			Games.Achievements.increment(apiClient,
					activity.getString(R.string.achievement_like_a_scanner),
					mFightingStep);
			Games.Achievements.increment(apiClient, activity
					.getString(R.string.achievement_who_is_faster_than_me),
					mFightingStep);
			mFightingStep = 0;
		}
		if (mMasterNumbers) {
			Games.Achievements.unlock(apiClient, activity
					.getString(R.string.achievement_master_numbers));
			mMasterNumbers = false;
		}
		if (mVisible) {
			Games.Achievements.unlock(apiClient, activity
					.getString(R.string.achievement_no_number_can_hide));
			mVisible = false;
		}
		if (mLucky) {
			Games.Achievements.unlock(apiClient, activity
					.getString(R.string.achievement_lucky));
			mLucky = false;
		}

		if (mSinglePlayerScore >= 0) {
			Games.Leaderboards.submitScore(apiClient,
					activity.getString(R.string.leaderboard_high_scores),
					mSinglePlayerScore);
			mSinglePlayerScore = -1;
		}
		saveLocal(activity);
	}
	
	public void saveLocal(Activity activity) {
		Editor achievement = activity.getSharedPreferences(
				"ACHIEVEMENT", Context.MODE_PRIVATE).edit();
		try {
			achievement.putString("facingChallenge", encrypt(Boolean.toString(mWinner)));
			achievement.putString("gage", encrypt(Boolean.toString(mEasy)));
			achievement.putString("fightingStep", encrypt(Integer.toString(mFightingStep)));
			achievement.putString("vocabularyDictionary1", encrypt(Boolean.toString(mMasterNumbers)));
			achievement.putString("vocabularyDictionary2", encrypt(Boolean.toString(mVisible)));
			achievement.putString("vocabularyDictionary3", encrypt(Boolean.toString(mLucky)));
			achievement.putString("singlePlayerScore", encrypt(Integer.toString(mSinglePlayerScore)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		achievement.commit();
	}

	public void loadLocal(Activity activity) {
		SharedPreferences achievement = activity.getSharedPreferences(
				"ACHIEVEMENT", Context.MODE_PRIVATE);
		try {
			mWinner = Boolean.valueOf(decrypt(achievement.getString("facingChallenge", Boolean.toString(false))));
			mEasy = Boolean.valueOf(decrypt(achievement.getString("gage", Boolean.toString(false))));
			mFightingStep = Integer.valueOf(decrypt(achievement.getString("fightingStep", Integer.toString(0))));
			mMasterNumbers = Boolean.valueOf(decrypt(achievement.getString("vocabularyDictionary1", Boolean.toString(false))));
			mVisible = Boolean.valueOf(decrypt(achievement.getString("vocabularyDictionary2", Boolean.toString(false))));
			mLucky = Boolean.valueOf(decrypt(achievement.getString("vocabularyDictionary3", Boolean.toString(false))));
			mSinglePlayerScore = Integer.valueOf(decrypt(achievement.getString("singlePlayerScore", Integer.toString(-1))));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static String encrypt(String value) throws GeneralSecurityException,
			UnsupportedEncodingException {

		byte[] raw = KEY.getBytes("US-ASCII");
		if (raw.length != KEY.length())
			throw new IllegalArgumentException("Invalid key size.");

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
				new byte[16]));
		return new String(cipher.doFinal(value.getBytes("US-ASCII")));
	}

	public static String decrypt(String value) throws GeneralSecurityException,
			UnsupportedEncodingException {
		byte[] encrypted = value.getBytes("US-ASCII");
		byte[] raw = KEY.getBytes("US-ASCII");
		if (raw.length != KEY.length()) {
			throw new IllegalArgumentException("Invalid key size.");
		}
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(
				new byte[16]));
		byte[] original = cipher.doFinal(encrypted);

		return new String(original, "US-ASCII");
	}
}

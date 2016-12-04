package com.triplesnake.game.numbersearch.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.triplesnake.game.numbersearch.R;

public class Setting {
	public static final String SETTING = "Setting";
	private static Boolean mIsSoundEnable;

	public static boolean isSoundEnable(Activity activity) {
		if(mIsSoundEnable == null){
			SharedPreferences setting = activity.getSharedPreferences(SETTING, Activity.MODE_PRIVATE);
			mIsSoundEnable = setting.getBoolean("isSoundEnable", true);
		}
		return mIsSoundEnable;
	}
	
	public static void setSoundEnable(Activity activity, boolean state) {
		mIsSoundEnable = state;
		SharedPreferences setting = activity.getSharedPreferences(SETTING,
				Activity.MODE_PRIVATE);
		Editor editor = setting.edit();
		editor.putBoolean("isSoundEnable", state);
		editor.commit();
	}
	
	public static int isRated(Activity activity) {
		int launched =  activity.getSharedPreferences(SETTING, Activity.MODE_PRIVATE)
				.getInt("launched", 1);
		if(launched >= 0)
			activity.getSharedPreferences(SETTING, Activity.MODE_PRIVATE).edit()
			.putInt("launched", launched + 1).commit();
		return launched;
	}

	public static void setRated(Activity activity) {
		activity.getSharedPreferences(SETTING, Activity.MODE_PRIVATE).edit()
				.putInt("launched", -1).commit();
	}
	
	public static boolean[] getThemeEnables(Activity activity) {
		boolean[] result = new boolean[6];
		result[0] = true;
		SharedPreferences setting = activity.getSharedPreferences(SETTING, Activity.MODE_PRIVATE);
		result[1] = setting.getBoolean("isRedEnable", false);
		result[2] = setting.getBoolean("isLeatherEnable", false);
		result[3] = setting.getBoolean("isMetalEnable", false);
		result[4] = setting.getBoolean("isPaperEnable", false);
		result[5] = setting.getBoolean("isWoodEnable", false);
		return result;
	}
	
	public static void enableTheme(int index, Activity activity) {
		String[] theme = new String[] { "isGreenEnable", "isRedEnable",
				"isLeatherEnable", "isMetalEnable", "isPaperEnable",
				"isWoodEnable" };
		SharedPreferences settings = activity.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
		if (!settings.getBoolean(theme[index], false)){
			Toast.makeText(
					activity,
					activity.getResources()
							.getStringArray(R.array.theme_unlock_msg)[index],
					Toast.LENGTH_SHORT).show();
			Editor editor = settings.edit();
			editor.putBoolean(theme[index], true);
			editor.commit();
		}
	}
	
	public static void setThemeSelected(Activity activity, int id) {
		SharedPreferences setting = activity.getSharedPreferences(SETTING,
				Activity.MODE_PRIVATE);
		Editor editor = setting.edit();
		editor.putInt("themeSelected", id);
		editor.commit();
	}
	
	public static int getThemeSelected(Activity activity) {
		SharedPreferences setting = activity.getSharedPreferences(SETTING,
				Activity.MODE_PRIVATE);
		return setting.getInt("themeSelected", 0);
	}
	
	public static Bundle getTheme(int id, Resources res){
		Bundle data = new Bundle();
		switch (id) {
		case 0:
			data.putInt("titleBackground", Color.parseColor("#13c7af"));
			data.putInt("titleTextColor", Color.parseColor("#ffffff"));
			data.putInt("icPauseResId", R.drawable.theme_basic_ic_pause);
			data.putInt("icClockResId", R.drawable.theme_basic_ic_clock);
			data.putInt("gridBackground", Color.parseColor("#edebeb"));
			data.putInt("gridTextColor", Color.parseColor("#454545"));
			data.putInt("gridSelectorColor", Color.parseColor("#882fe0c8"));
			data.putInt("listBackground", Color.parseColor("#049d89"));
			data.putInt("listTextColor", Color.parseColor("#ffffff"));
			break;
		case 1:
			data.putInt("titleBackground", Color.parseColor("#ff5b54"));
			data.putInt("titleTextColor", Color.parseColor("#ffffff"));
			data.putInt("icPauseResId", R.drawable.theme_basic_ic_pause);
			data.putInt("icClockResId", R.drawable.theme_basic_ic_clock);
			data.putInt("gridBackground", Color.parseColor("#edebec"));
			data.putInt("gridTextColor", Color.parseColor("#454545"));
			data.putInt("gridSelectorColor", Color.parseColor("#88ff5b54"));
			data.putInt("listBackground", Color.parseColor("#f45750"));
			data.putInt("listTextColor", Color.parseColor("#ffffff"));
			break;
		case 2:
			data.putInt("titleBackground", R.drawable.theme_leather_title);
			data.putInt("titleTextColor", Color.parseColor("#e3be9b"));
			data.putInt("icPauseResId", R.drawable.theme_leather_ic_pause);
			data.putInt("icClockResId", R.drawable.theme_leather_ic_clock);
			data.putInt("gridBackground", R.drawable.theme_leather_grid_bg);
			data.putInt("gridTextColor", Color.parseColor("#ffffff"));
			data.putInt("gridSelectorColor", Color.parseColor("#88bdc22e"));
			data.putInt("listBackground", Color.parseColor("#99000000"));
			data.putInt("listTextColor", Color.parseColor("#ffffff"));
			break;
		case 3:
			data.putInt("titleBackground", R.drawable.theme_metal_title);
			data.putInt("titleTextColor", Color.parseColor("#ececec"));
			data.putInt("icPauseResId", R.drawable.theme_metal_ic_pause);
			data.putInt("icClockResId", R.drawable.theme_metal_ic_clock);
			data.putInt("gridBackground", R.drawable.theme_metal_grid_bg);
			data.putInt("gridTextColor", Color.parseColor("#242a3f"));
			data.putInt("gridSelectorColor", Color.parseColor("#886f96f9"));
			data.putInt("listBackground", Color.parseColor("#99000000"));
			data.putInt("listTextColor", Color.parseColor("#ffffff"));
			break;
		case 4:
			data.putInt("titleBackground", R.drawable.theme_paper_title);
			data.putInt("titleTextColor", Color.parseColor("#524a32"));
			data.putInt("icPauseResId", R.drawable.theme_paper_ic_pause);
			data.putInt("icClockResId", R.drawable.theme_paper_ic_clock);
			data.putInt("gridBackground", R.drawable.theme_paper_grid_bg);
			data.putInt("gridTextColor", Color.parseColor("#7b4c22"));
			data.putInt("gridSelectorColor", Color.parseColor("#88bfc654"));
			data.putInt("listBackground", Color.parseColor("#99000000"));
			data.putInt("listTextColor", Color.parseColor("#ffffff"));
			break;
		case 5:
			data.putInt("titleBackground", R.drawable.theme_wood_title);
			data.putInt("titleTextColor", Color.parseColor("#f3cc88"));
			data.putInt("icPauseResId", R.drawable.theme_wood_ic_pause);
			data.putInt("icClockResId", R.drawable.theme_wood_ic_clock);
			data.putInt("gridBackground", R.drawable.theme_wood_grid_bg);
			data.putInt("gridTextColor", Color.parseColor("#4f391e"));
			data.putInt("gridSelectorColor", Color.parseColor("#88d7ab76"));
			data.putInt("listBackground", Color.parseColor("#99000000"));
			data.putInt("listTextColor", Color.parseColor("#ffffff"));
			break;
		default:
			break;
		}
		return data;
	}
}

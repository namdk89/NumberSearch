package com.triplesnake.numbersearch.menu;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.triplesnake.game.numbersearch.GameActivity;
import com.triplesnake.game.numbersearch.R;
import com.triplesnake.game.numbersearch.utils.Setting;

public class SettingFragment extends Fragment implements OnClickListener {
	private View[] mThemes;
	private int[] mSelectedResId = new int[] { R.drawable.theme_green_selected,
			R.drawable.theme_red_selected, R.drawable.theme_leather_selected,
			R.drawable.theme_metal_selected, R.drawable.theme_paper_selected,
			R.drawable.theme_wood_selected };
	private int[] mEnableResId = new int[] { R.drawable.theme_green_ic,
			R.drawable.theme_red_ic, R.drawable.theme_leather_ic,
			R.drawable.theme_metal_ic, R.drawable.theme_paper_ic,
			R.drawable.theme_wood_ic };
	private int[] mLockResId = new int[] { 0, R.drawable.theme_red_lock,
			R.drawable.theme_leather_lock, R.drawable.theme_metal_lock,
			R.drawable.theme_paper_lock, R.drawable.theme_wood_lock };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_setting, container, false);
		mThemes = new View[6];
		mThemes[0] = parent.findViewById(R.id.theme_green);
		mThemes[1] = parent.findViewById(R.id.theme_red);
		mThemes[2] = parent.findViewById(R.id.theme_leather);
		mThemes[3] = parent.findViewById(R.id.theme_metal);
		mThemes[4] = parent.findViewById(R.id.theme_paper);
		mThemes[5] = parent.findViewById(R.id.theme_wood);
		int selected = Setting.getThemeSelected(getActivity());
		boolean[] states = Setting.getThemeEnables(getActivity());
		for (int i = 0; i < mThemes.length; i++) {
			if (states[i])
				if (selected == i)
					mThemes[i].setBackgroundResource(mSelectedResId[i]);
				else
					mThemes[i].setBackgroundResource(mEnableResId[i]);
			else
				mThemes[i].setBackgroundResource(mLockResId[i]);
			mThemes[i].setOnClickListener(this);
		}

		if (getActivity() instanceof GameActivity) {
			parent.findViewById(R.id.title_top).setVisibility(View.VISIBLE);
			parent.findViewById(R.id.title_bottom).setVisibility(View.VISIBLE);
			parent.setBackgroundColor(0xfffdebc7);
		} else {
			parent.findViewById(R.id.title_top).setVisibility(View.GONE);
			parent.findViewById(R.id.title_bottom).setVisibility(View.GONE);
			parent.setBackgroundColor(0x00000000);
		}

		return parent;
	}

	@Override
	public void onClick(View v) {
		boolean[] states = Setting.getThemeEnables(getActivity());
		int select = -1;
		int require = -1;
		boolean hideNoButton = true;
		switch (v.getId()) {
		case R.id.theme_green:
			if (states[0])
				select = 0;
			else
				require = 0;
			break;
		case R.id.theme_red:
			if (states[1])
				select = 1;
			else
				require = 1;
			break;
		case R.id.theme_leather:
			if (states[2])
				select = 2;
			else {
				require = 2;
				hideNoButton = false;
			}
			break;
		case R.id.theme_metal:
			if (states[3])
				select = 3;
			else
				require = 3;
			break;
		case R.id.theme_paper:
			if (states[4])
				select = 4;
			else
				require = 4;
			break;
		case R.id.theme_wood:
			if (states[5])
				select = 4;
			else
				require = 5;
			break;
		}

		if (select >= 0) {
			Setting.setThemeSelected(getActivity(), select);
			mThemes[select].setBackgroundResource(mSelectedResId[select]);
			if (getActivity() instanceof GameActivity)
				((GameActivity) getActivity()).initGame();
		} else {
			AlertFragment af = new AlertFragment();
			af.setTitle(R.string.theme_unavailable);
			String tag = AlertFragment.class.getName();
			String[] requires = getActivity().getResources().getStringArray(
					R.array.theme_requies);
			af.setTitle(R.string.theme_unavailable).setMessage(requires[require]);
			if (hideNoButton)
				af.hideNoBtn();
			if (require == 2)
				af.setPositiveButton(R.string.ok, new OnClickListener() {

					@Override
					public void onClick(View v) {
						Uri marketUri = Uri.parse(getString(R.string.market_url));
						Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
						try {
							startActivity(marketIntent);
							Setting.enableTheme(2, getActivity());
							mThemes[2].setBackgroundResource(mEnableResId[2]);
						} catch (ActivityNotFoundException e) {
							Toast.makeText(getActivity(), "Unable to find market app",
									Toast.LENGTH_LONG).show();
						}
					}
				});
			else
				af.setPositiveButton(R.string.ok, null);
			af.show(getChildFragmentManager(), tag);
		}
	}
}

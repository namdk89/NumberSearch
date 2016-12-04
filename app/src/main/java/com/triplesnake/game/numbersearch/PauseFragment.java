package com.triplesnake.game.numbersearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.triplesnake.game.numbersearch.utils.Setting;
import com.triplesnake.numbersearch.menu.AlertFragment;

public class PauseFragment extends Fragment implements OnClickListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_pause, container, false);

		TextView hint = (TextView) parent.findViewById(R.id.introduce);
		hint.setMovementMethod(new ScrollingMovementMethod());

		parent.findViewById(R.id.btn_nextgame).setOnClickListener(this);
		parent.findViewById(R.id.btn_setting).setOnClickListener(this);
		parent.findViewById(R.id.btn_exit).setOnClickListener(this);

		CheckBox soundSetting = (CheckBox) parent.findViewById(R.id.btn_sound);
		soundSetting.setChecked(Setting.isSoundEnable(getActivity()));
		soundSetting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Setting.setSoundEnable(getActivity(), isChecked);
			}
		});

		return parent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_nextgame:
//			((GameActivity) getActivity()).initGame();
//			getActivity().onBackPressed();
			confirmNewgame();
			break;

		case R.id.btn_setting:
			((GameActivity) getActivity()).showSettingFragment();
			break;

		case R.id.btn_exit:
			getActivity().finish();
//			confirmExit();
			break;
		}
	}

	private void confirmExit() {
		new AlertFragment()
				.setTitle(R.string.confirm_exit_game_title)
				.setMessage(R.string.confirm_exit_game_content)
				.setPositiveButton(R.string.yes, new OnClickListener() {

					@Override
					public void onClick(View v) {
						getActivity().finish();
					}
				}).setNegativeButton(R.string.no, null)
				.show(getChildFragmentManager(), AlertFragment.class.getName());
	}

	private void confirmNewgame() {
		new AlertFragment()
				.setTitle(R.string.confirm_new_game_title)
				.setMessage(R.string.confirm_new_game_content)
				.setPositiveButton(R.string.yes, new OnClickListener() {

					@Override
					public void onClick(View v) {
						((GameActivity) getActivity()).newGame();
						getActivity().getSupportFragmentManager().beginTransaction().remove(PauseFragment.this).commit();
					}
				}).setNegativeButton(R.string.no, null)
				.show(getChildFragmentManager(), AlertFragment.class.getName());
	}
}

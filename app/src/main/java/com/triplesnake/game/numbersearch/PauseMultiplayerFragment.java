package com.triplesnake.game.numbersearch;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.android.gms.common.images.ImageManager;
import com.triplesnake.game.numbersearch.utils.Setting;
import com.triplesnake.numbersearch.menu.AlertFragment;
import com.triplesnake.numbersearch.menu.MainActivity;

public class PauseMultiplayerFragment extends Fragment implements
		OnClickListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_multiplayer_pause,
				container, false);
		TextView p1Name, p2Name;
		p1Name = (TextView) parent.findViewById(R.id.p1_name);
		p2Name = (TextView) parent.findViewById(R.id.p2_name);
		p1Name.setText(getArguments().getString("p1Name"));
		p2Name.setText(getArguments().getString("p2Name"));

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
		
		String p1AvatarUri = getArguments().getString("p1AvatarUri");
		if (p1AvatarUri != null) {
			ImageView p1Avatar = (ImageView) parent.findViewById(R.id.p1_avatar);
			ImageManager.create(getActivity()).loadImage(p1Avatar,
					Uri.parse(p1AvatarUri), R.drawable.ic_anonymous);
		}

		String p2AvatarUri = getArguments().getString("p2AvatarUri");
		if (p2AvatarUri != null) {
			ImageView p2Avatar = (ImageView) parent.findViewById(R.id.p2_avatar);
			ImageManager.create(getActivity()).loadImage(p2Avatar,
					Uri.parse(p2AvatarUri), R.drawable.ic_anonymous);
		}
		
		return parent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_nextgame:
			((GameActivity) getActivity()).initGame();
			getActivity().onBackPressed();
			break;
		case R.id.btn_setting:
			((MainActivity) getActivity()).showSettingFragment();
			break;
		case R.id.btn_exit:
			confirmExit();
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
}

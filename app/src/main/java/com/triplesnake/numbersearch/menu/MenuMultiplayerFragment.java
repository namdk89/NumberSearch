package com.triplesnake.numbersearch.menu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.triplesnake.game.numbersearch.R;

public class MenuMultiplayerFragment extends Fragment implements
		OnClickListener {
	private TextView btnQuickGame, btnInvitePlayer, btnSeeInvitation;
	private MainActivity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MainActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_menu_multiplayer,
				container, false);
		btnQuickGame = (TextView) parent.findViewById(R.id.menu_btn_quick_game);
		btnQuickGame.setOnClickListener(this);
		btnInvitePlayer = (TextView) parent
				.findViewById(R.id.menu_btn_invite_player);
		btnInvitePlayer.setOnClickListener(this);
		btnSeeInvitation = (TextView) parent
				.findViewById(R.id.menu_btn_see_invitation);
		btnSeeInvitation.setOnClickListener(this);
		return parent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_btn_quick_game:
			mActivity.onQuickMatchClicked();
			break;
		case R.id.menu_btn_invite_player:
			mActivity.onStartMatchClicked();
			break;
		case R.id.menu_btn_see_invitation:
			mActivity.onCheckGamesClicked();
			break;
		default:
			break;
		}
	}
}

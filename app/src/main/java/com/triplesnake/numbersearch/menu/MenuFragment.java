package com.triplesnake.numbersearch.menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.triplesnake.game.numbersearch.R;
import com.triplesnake.game.numbersearch.utils.GameInfo;

public class MenuFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_menu, container, false);

		parent.findViewById(R.id.menu_btn_start).setOnClickListener(this);
		parent.findViewById(R.id.menu_btn_endless).setOnClickListener(this);
		parent.findViewById(R.id.menu_btn_setting).setOnClickListener(this);
		parent.findViewById(R.id.menu_btn_lib).setOnClickListener(this);
		parent.findViewById(R.id.btn_leaderboard).setOnClickListener(this);
		parent.findViewById(R.id.btn_achievement).setOnClickListener(this);
		parent.findViewById(R.id.btn_multiplayer).setOnClickListener(this);
		return parent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_btn_start:
//			Bundle playArgs = GameInfo.loadGame(getActivity());
//			if (playArgs.getBoolean("continue"))
//				askToContinue(playArgs);
//			else
//				((MainActivity) getActivity()).startGame(null);

			Bundle argsLimit = new Bundle();
			argsLimit.putString("gameType", GameInfo.WORD_SEARCH_NORMAL);
			((MainActivity) getActivity()).startGame(argsLimit);
			break;
		case R.id.menu_btn_endless:
			Bundle argsLmitness = new Bundle();
			argsLmitness.putString("gameType", GameInfo.WORD_SEARCH_ENDLESS);
			((MainActivity) getActivity()).startGame(argsLmitness);
			break;
		case R.id.menu_btn_setting:
			((MainActivity) getActivity()).showSettingFragment();
			break;
		case R.id.menu_btn_lib:
			((MainActivity) getActivity()).showMore();
			break;
		case R.id.btn_leaderboard:
			((MainActivity) getActivity()).showLeaderboard();
			break;
		case R.id.btn_achievement:
			((MainActivity) getActivity()).showAchievement();
			break;
		case R.id.btn_multiplayer:
			((MainActivity) getActivity()).showMultiplayer();
			break;
		default:
			break;
		}
	}

	private void askToContinue(final Bundle gameArgs) {
		new AlertFragment().setTitle(R.string.continue_title)
				.setMessage(R.string.continue_msg)
				.setPositiveButton(R.string.yes, new OnClickListener() {

					@Override
					public void onClick(View v) {
						Bundle gameType = new Bundle();
						gameType.putString("gameType", GameInfo.WORD_SEARCH_NORMAL);
						((MainActivity) getActivity()).startGame(gameType);
					}
				}).setNegativeButton(R.string.no, new OnClickListener() {

					@Override
					public void onClick(View v) {
						GameInfo.resetGame(gameArgs);
						GameInfo.saveGame(getActivity(), gameArgs);
						Bundle gameType = new Bundle();
						gameType.putString("gameType", GameInfo.WORD_SEARCH_NORMAL);
						((MainActivity) getActivity()).startGame(gameType);
					}
				}).show(getChildFragmentManager(), AlertFragment.class.getName());
	}
}

package com.triplesnake.game.numbersearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.triplesnake.game.numbersearch.utils.GameInfo;

public class ResultFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_result, container, false);
		TextView titleTv, levelTv, scoreTv, bonusTv, totalTv, funFact;
		titleTv = (TextView) parent.findViewById(R.id.title);
		levelTv = (TextView) parent.findViewById(R.id.level);
		scoreTv = (TextView) parent.findViewById(R.id.score);
		bonusTv = (TextView) parent.findViewById(R.id.bonus);
		totalTv = (TextView) parent.findViewById(R.id.total);
		funFact = (TextView) parent.findViewById(R.id.fun_fact);

		parent.findViewById(R.id.btn_nextgame).setOnClickListener(this);
		parent.findViewById(R.id.btn_exit).setOnClickListener(this);

		if (getArguments().getString("gameType").equals(GameInfo.WORD_SEARCH_ENDLESS) ||
				getArguments().getInt("timeLeft") > 0) {
			titleTv.setText(R.string.result_title_win);
		} else {
			titleTv.setText(R.string.you_lose);
			parent.findViewById(R.id.btn_nextgame).setVisibility(View.GONE);
		}
		levelTv.setText((getArguments().getInt("level") + 1) + "");
		scoreTv.setText(getArguments().getInt("score") + "");
		bonusTv.setText("+" + getArguments().getInt("bonus"));
		totalTv.setText(getArguments().getInt("totalScore") + "");

		String fact = GameInfo.getFunFact(getActivity());
		if (fact.charAt(0) == '*')
			fact = "2013: The year in numbers\n" + fact.substring(1);
		else
			fact = "Number fun fact\n" + fact;
		funFact.setText(fact);

		return parent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_nextgame:
			if (getArguments().getString("gameType").equals(GameInfo.WORD_SEARCH_ENDLESS) ||
					getArguments().getInt("timeLeft") > 0) {
				((GameActivity) getActivity()).nextLevel();
			}
			break;

		case R.id.btn_exit:
			getActivity().finish();
			break;
		}
	}
}

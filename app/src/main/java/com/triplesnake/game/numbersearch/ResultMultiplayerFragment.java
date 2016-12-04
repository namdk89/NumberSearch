package com.triplesnake.game.numbersearch;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.images.ImageManager;
import com.triplesnake.game.numbersearch.utils.GameInfo;
import com.triplesnake.view.AutoScrollingTextView;
import com.triplesnake.view.MenuButton;

public class ResultMultiplayerFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_multiplayer_result,
				container, false);
		TextView result;
		TextView p1WordScore, p1TimeLeft;
		TextView p2WordScore, p2TimeLeft;
		TextView p1Score, p2Score;
		AutoScrollingTextView p1Name, p2Name;
		result = (TextView) parent.findViewById(R.id.result);
		p1Name = (AutoScrollingTextView) parent.findViewById(R.id.p1_name);
		p1WordScore = (TextView) parent.findViewById(R.id.p1_found);
		p1TimeLeft = (TextView) parent.findViewById(R.id.p1_time_left);
		p1Score = (TextView) parent.findViewById(R.id.p1_total_score);
		p2Name = (AutoScrollingTextView) parent.findViewById(R.id.p2_name);
		p2WordScore = (TextView) parent.findViewById(R.id.p2_found);
		p2TimeLeft = (TextView) parent.findViewById(R.id.p2_time_left);
		p2Score = (TextView) parent.findViewById(R.id.p2_total_score);

		int levelScore = GameInfo.getLevelScore(getArguments().getInt("level"));

		int p1WordScoreInt = getArguments().getInt("p1Found") * levelScore;
		int p1TimeLeftInt = getArguments().getInt("p1TimeLeft");
		p1Name.setText(getArguments().getString("p1Name"));
		p1WordScore.setText(p1WordScoreInt + "");
		p1TimeLeft.setText("+" + p1TimeLeftInt);
		p1Score.setText((p1WordScoreInt + p1TimeLeftInt) + "");

		String p2NameStr = getArguments().getString("p2Name");
		int p2WordScoreInt = getArguments().getInt("p2Found", -1);
		int p2TimeLeftInt = getArguments().getInt("p2TimeLeft", -1);
		if (p2NameStr != null)
			p2Name.setText(p2NameStr);
		if (p2WordScoreInt != -1) {
			p2WordScoreInt *= levelScore;
			p2WordScore.setText(p2WordScoreInt + "");
			p2TimeLeft.setText("+" + p2TimeLeftInt);
			p2Score.setText((p2WordScoreInt + p2TimeLeftInt) + "");
		}

		MenuButton btnExit = (MenuButton) parent.findViewById(R.id.btn_ok);
		btnExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Activity activity = getActivity();
				if (activity instanceof GameActivity)
					activity.finish();
				else
					activity.onBackPressed();
			}
		});

		switch (getArguments().getInt("result", -2)) {
		case -1:
			result.setText(R.string.you_lose);
			break;
		case 0:
			result.setText(R.string.draw);
			break;
		case 1:
			result.setText(R.string.you_win);
			break;
		}
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
}

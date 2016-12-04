package com.triplesnake.numbersearch.menu;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.triplesnake.game.numbersearch.R;

public class AlertFragment extends DialogFragment implements OnClickListener {

	private OnClickListener btnPositiveListener = null;
	private OnClickListener btnNegativeListener = null;
	private String btnPositiveTitle, btnNegativeTitle, mMessage, mTitle;
	private int btnPositiveTitleStrId, btnNegativeTitleStrId, mMessageStrId,
			mTitleStrId;
	private boolean mIsHideBtnNo = false;
	private boolean mIsHideBtnYes = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_alert, container, false);
		getDialog().getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		TextView title = (TextView) parent.findViewById(R.id.title);
		if (mTitle != null)
			title.setText(mTitle);
		else if (mTitleStrId > 0)
			title.setText(mTitleStrId);

		TextView content = (TextView) parent.findViewById(R.id.content);
		if (mMessage != null)
			content.setText(mMessage);
		else if (mMessageStrId > 0)
			content.setText(mMessageStrId);

		Typeface arial = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/arial.ttf");

		Button btnYes = (Button) parent.findViewById(R.id.btn_yes);
		btnYes.setTypeface(arial);
		Button btnNo = (Button) parent.findViewById(R.id.btn_no);
		btnNo.setTypeface(arial);
		if (mIsHideBtnYes) {
			btnYes.setVisibility(View.GONE);
		} else {
			if (btnPositiveTitle != null)
				btnYes.setText(btnPositiveTitle);
			else if (btnPositiveTitleStrId > 0)
				btnYes.setText(btnPositiveTitleStrId);
			btnYes.setOnClickListener(this);
		}
		if (mIsHideBtnNo) {
			btnNo.setVisibility(View.GONE);
		} else {
			if (btnNegativeTitle != null)
				btnNo.setText(btnNegativeTitle);
			else if (btnNegativeTitleStrId > 0)
				btnNo.setText(btnNegativeTitleStrId);
			btnNo.setOnClickListener(this);
		}
		return parent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_yes:
			if (btnPositiveListener != null)
				btnPositiveListener.onClick(v);
			dismiss();
			break;

		case R.id.btn_no:
			if (btnNegativeListener != null)
				btnNegativeListener.onClick(v);
			dismiss();
			break;
		}
	}

	public AlertFragment setTitle(String title) {
		mTitle = title;
		mTitleStrId = 0;
		return this;
	}

	public AlertFragment setTitle(int title) {
		mTitleStrId = title;
		mTitle = null;
		return this;
	}

	public AlertFragment setMessage(String msg) {
		mMessage = msg;
		mMessageStrId = 0;
		return this;
	}

	public AlertFragment setMessage(int msg) {
		mMessageStrId = msg;
		mMessage = null;
		return this;
	}

	public AlertFragment hideNoBtn() {
		mIsHideBtnNo = true;
		return this;
	}

	public AlertFragment hideYesBtn() {
		mIsHideBtnYes = true;
		return this;
	}

	public AlertFragment setNegativeButton(String title, OnClickListener onclick) {
		btnNegativeTitle = title;
		btnNegativeTitleStrId = 0;
		btnNegativeListener = onclick;
		return this;
	}

	public AlertFragment setPositiveButton(String title, OnClickListener onclick) {
		btnPositiveTitle = title;
		btnPositiveTitleStrId = 0;
		btnPositiveListener = onclick;
		return this;
	}

	public AlertFragment setNegativeButton(int title, OnClickListener onclick) {
		btnNegativeTitleStrId = title;
		btnNegativeTitle = null;
		btnNegativeListener = onclick;
		return this;
	}

	public AlertFragment setPositiveButton(int title, OnClickListener onclick) {
		btnPositiveTitleStrId = title;
		btnPositiveTitle = null;
		btnPositiveListener = onclick;
		return this;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		((MainActivity) getActivity()).cancelPending();
	}
}

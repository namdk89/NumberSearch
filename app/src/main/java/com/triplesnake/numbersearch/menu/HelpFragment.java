package com.triplesnake.numbersearch.menu;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.triplesnake.game.numbersearch.R;

public class HelpFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(STYLE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_help, container, false);
	}
}

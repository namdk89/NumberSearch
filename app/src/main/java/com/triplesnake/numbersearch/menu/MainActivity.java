package com.triplesnake.numbersearch.menu;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.LoadMatchesResponse;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.UpdateMatchResult;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.triplesnake.game.numbersearch.GameActivity;
import com.triplesnake.game.numbersearch.NumberSearchTurn;
import com.triplesnake.game.numbersearch.R;
import com.triplesnake.game.numbersearch.ResultMultiplayerFragment;
import com.triplesnake.game.numbersearch.utils.GameInfo;
import com.triplesnake.game.numbersearch.utils.Setting;

public class MainActivity extends BaseGameActivity implements
		OnInvitationReceivedListener, OnTurnBasedMatchUpdateReceivedListener,
		ResultCallback<InitiateMatchResult> {

	private enum Pending {
		NONE, LEADERBOARD, ACHIEVEMENT, MULTIPLAYER, QUICKMATCH, STARTMATCH, CHECKGAME
	};

	private Pending mPending = Pending.NONE;

	private AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

	// For our intents
	final static int RC_SELECT_PLAYERS = 10000;
	final static int RC_LOOK_AT_MATCHES = 10001;

	// request codes we use when invoking an external activity
	final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

//	private AdView mAdView;
//	private View mCloseAd;
	private InterstitialAd mInterstitialAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		initAds();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		getGameHelper().setMaxAutoSignInAttempts(0);
		
		CheckBox soundSetting = (CheckBox) findViewById(R.id.btn_sound);
		soundSetting.setChecked(Setting.isSoundEnable(this));
		soundSetting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Setting.setSoundEnable(MainActivity.this, isChecked);
			}
		});

		View help = findViewById(R.id.btn_help);
		help.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new HelpFragment().show(getSupportFragmentManager(),
						HelpFragment.class.getName());
			}
		});
		
		showMenuFragment();
	}

	private void initAds() {
		// Create banner ads
//		mAdView = (AdView) findViewById(R.id.banner);
//		mCloseAd = findViewById(R.id.close_banner);
//		mCloseAd.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				hideBanner();
//			}
//		});
		// Create full screen ads.
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId(getString(R.string.ad_id));
	}

	private void showAds(int fullRate) {
		if (new Random().nextInt(100) < fullRate)
			showInterstitial();
//		showBanner();
	}

//	private void showBanner() {
//		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
//		mAdView.loadAd(adRequestBuilder.build());
//		mAdView.setAdListener(new AdListener() {
//			@Override
//			public void onAdLoaded() {
//				super.onAdLoaded();
//				mAdView.setVisibility(View.VISIBLE);
//				mCloseAd.setVisibility(View.VISIBLE);
//			}
//
//			@Override
//			public void onAdFailedToLoad(int errorCode) {
//				super.onAdFailedToLoad(errorCode);
//				mCloseAd.setVisibility(View.GONE);
//				mAdView.setVisibility(View.GONE);
//			}
//		});
//	}

//	private void hideBanner() {
//		if (mAdView != null) {
//			mAdView.setVisibility(View.GONE);
//			mAdView.setAdListener(null);
//		}
//		if (mCloseAd != null)
//			mCloseAd.setVisibility(View.GONE);
//	}

	public void loadInterstitial() {
		mInterstitialAd.loadAd(new AdRequest.Builder().build());
	}

	public void showInterstitial() {
		if (mInterstitialAd.isLoaded())
			mInterstitialAd.show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		loadInterstitial();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dismissSpinner();
		FragmentManager fm = getSupportFragmentManager();
		boolean ask = fm.findFragmentByTag(AlertFragment.class.getName()) == null;
		ask = ask
				&& fm.findFragmentByTag(ResultMultiplayerFragment.class.getName()) == null;
		if (ask)
			if (!askForRankApp())
				checkOthersApp();
//		mAdView.resume();
//		loadInterstitial();
	}

	private void checkOthersApp() {
//		if (new Random().nextInt(10) > 8)
//			new AlertFragment().setTitle(R.string.check_other_title)
//					.setMessage(R.string.check_other_content)
//					.setPositiveButton(R.string.yes, new View.OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							showMore();
//						}
//					}).setNegativeButton(R.string.no, null)
//					.show(getSupportFragmentManager(), AlertFragment.class.getName());
	}

	private boolean askForRankApp() {
		int launch = Setting.isRated(this);
		if (launch == -1 || launch % 20 != 0)
			return false;
		new AlertFragment()
				.setTitle(R.string.rate_title)
				.setMessage(getString(R.string.rate_content, "" + launch))
				.setPositiveButton(R.string.yes, new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Uri uri = Uri.parse("market://details?id="
								+ getApplicationContext().getPackageName());
						Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
						goToMarket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						try {
							getApplicationContext().startActivity(goToMarket);
							Setting.setRated(MainActivity.this);
						} catch (ActivityNotFoundException e) {
							Toast.makeText(getApplicationContext(), R.string.rate_error,
									Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton(R.string.no, null)
				.show(getSupportFragmentManager(), AlertFragment.class.getName());

		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
//		mAdView.pause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		mAdView.destroy();
	}

	public void showMenuFragment() {
//		showBanner();
		FragmentManager fm = getSupportFragmentManager();
		fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		FragmentTransaction ft = fm.beginTransaction();
		MenuFragment sf = new MenuFragment();
		ft.replace(R.id.main, sf, MenuFragment.class.getName());
		ft.commit();
	}

	public void startGame(Bundle args) {
		Intent game = new Intent(this, GameActivity.class);
		if (args != null)
			game.putExtras(args);
		startActivity(game);
	}

	public void removeFragment(String tag) {
		FragmentManager fm = getSupportFragmentManager();
		Fragment f = fm.findFragmentByTag(tag);
		if (f == null)
			return;
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(f);
		ft.commit();
	}

	public void showSettingFragment() {
		showAds(10);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		SettingFragment sf = new SettingFragment();
		ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
				R.anim.slide_in_left, R.anim.slide_out_right);
		ft.replace(R.id.main, sf, SettingFragment.class.getName());
		ft.addToBackStack(SettingFragment.class.getName());
		ft.commit();
	}

	public void unlockLuckyAchievement() {
		mOutbox.mLucky = true;
		pushAccomplishments();
	}

	// Displays your in box. You will get back onActivityResult where
	// you will need to figure out what you clicked on.
	public void onCheckGamesClicked() {
		try {
			if(isSignedIn()) {
				Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(getApiClient());
				startActivityForResult(intent, RC_LOOK_AT_MATCHES);
			}else{
				getGameHelper().beginUserInitiatedSignIn();
				mPending = Pending.CHECKGAME;
			}
		}catch (SecurityException e){
			getGameHelper().beginUserInitiatedSignIn();
			mPending = Pending.CHECKGAME;
		}
	}

	// Open the create-game UI. You will get back an onActivityResult
	// and figure out what to do.
	public void onStartMatchClicked() {
		try {
			if(isSignedIn()) {
				Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(
						getApiClient(), 1, 1, true);
				startActivityForResult(intent, RC_SELECT_PLAYERS);
			}else{
				getGameHelper().beginUserInitiatedSignIn();
				mPending = Pending.STARTMATCH;
			}
		}catch (SecurityException e){
			getGameHelper().beginUserInitiatedSignIn();
			mPending = Pending.STARTMATCH;
		}
	}

	// Create a one-on-one auto match game.
	public void onQuickMatchClicked() {
		try {
			if(isSignedIn()) {
				Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, 0);

				TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
						.setAutoMatchCriteria(autoMatchCriteria).build();
				showSpinner();
				Games.TurnBasedMultiplayer.createMatch(getApiClient(), tbmc)
						.setResultCallback(this);
			}else{
				getGameHelper().beginUserInitiatedSignIn();
				mPending = Pending.QUICKMATCH;
			}
		} catch (SecurityException e){
			dismissSpinner();
			getGameHelper().beginUserInitiatedSignIn();
			mPending = Pending.QUICKMATCH;
		}
	}

	@Override
	public void onResult(InitiateMatchResult result) {
		TurnBasedMatch match = result.getMatch();
		dismissSpinner();

		if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
			return;
		}

		if (match.getData() != null) {
			// This is a game that has already started, so I'll just start
			updateMatch(match);
			return;
		}

		startMatch(match);
	}

	// startMatch() happens in response to the createTurnBasedMatch()
	// above. This is only called on success, so we should have a
	// valid match object. We're taking this opportunity to setup the
	// game, saving our initial state. Calling takeTurn() will
	// callback to OnTurnBasedMatchUpdated(), which will show the game
	// UI.
	public void startMatch(TurnBasedMatch match) {
		showSpinner();
		String myPlayerId = Games.Players.getCurrentPlayerId(getApiClient());
		NumberSearchTurn data = new NumberSearchTurn();
		Games.TurnBasedMultiplayer.takeTurn(getApiClient(), match.getMatchId(),
				data.persist(), match.getParticipantId(myPlayerId)).setResultCallback(
				new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {

					@Override
					public void onResult(UpdateMatchResult result) {
						TurnBasedMatch match = result.getMatch();
						if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
							return;
						}
						if (match.getData() != null) {
							// This is a game that has already started, so I'll just start
							updateMatch(match);
							return;
						}
					}
				});
	}

	private void pushAccomplishments() {
		if (isSignedIn())
			mOutbox.pushAccomplishments(this, getApiClient());
		else
			mOutbox.saveLocal(this);
	}

	public void showMore() {
		Uri uri = Uri.parse("market://search?q=pub:LeoStyle Studio");
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		goToMarket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			getApplicationContext().startActivity(goToMarket);
			Setting.setRated(MainActivity.this);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getApplicationContext(), R.string.rate_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	public void showMenuMultiplayerFragment() {
		showAds(10);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		MenuMultiplayerFragment lf = new MenuMultiplayerFragment();
		ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
				R.anim.slide_in_left, R.anim.slide_out_right);
		ft.replace(R.id.main, lf, MenuMultiplayerFragment.class.getName());
		ft.addToBackStack(MenuMultiplayerFragment.class.getName());
		ft.commit();
	}

	public void showLeaderboard() {
		try {
			if(isSignedIn()) {
				startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
						getApiClient(), getString(R.string.leaderboard_high_scores)),
						RC_UNUSED);
			}else{
				getGameHelper().beginUserInitiatedSignIn();
				mPending = Pending.LEADERBOARD;
			}
		} catch (SecurityException e){
			getGameHelper().beginUserInitiatedSignIn();
			mPending = Pending.LEADERBOARD;
		}
	}

	public void showAchievement() {
		try {
			if(isSignedIn()) {
				startActivityForResult(
						Games.Achievements.getAchievementsIntent(getApiClient()), RC_UNUSED);
			}
			else{
				getGameHelper().beginUserInitiatedSignIn();
				mPending = Pending.ACHIEVEMENT;
			}
		} catch (SecurityException e){
			getGameHelper().beginUserInitiatedSignIn();
			mPending = Pending.ACHIEVEMENT;
		}
	}

	public void showMultiplayer() {
		try {
			if(isSignedIn())
				showMenuMultiplayerFragment();
			else {
				getGameHelper().beginUserInitiatedSignIn();
				mPending = Pending.MULTIPLAYER;
			}
		} catch (SecurityException e){
			getGameHelper().beginUserInitiatedSignIn();
			mPending = Pending.MULTIPLAYER;
		}
	}

	public void cancelPending() {
		mPending = Pending.NONE;
	}

	@Override
	public void onBackPressed() {
		if(isSpinerShowing())
			return;
		FragmentManager fm = getSupportFragmentManager();
		int stackCount = fm.getBackStackEntryCount();
		if (stackCount == 0) {
			confirmExit();
			return;
		}
		super.onBackPressed();
	}

	private void confirmExit() {
		new AlertFragment().setTitle(R.string.confirm_exit_title)
				.setMessage(R.string.confirm_exit_content)
				.setPositiveButton(R.string.yes, new View.OnClickListener() {

					@Override
					public void onClick(View v) {
					finish();
					}
				}).setNegativeButton(R.string.no, null)
				.show(getSupportFragmentManager(), AlertFragment.class.getName());
	}

	// Helpful dialogs
	public void showSpinner() {
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
	}

	private boolean isSpinerShowing(){
		return findViewById(R.id.progress).getVisibility() == View.VISIBLE;
	}

	public void dismissSpinner() {
		findViewById(R.id.progress).setVisibility(View.GONE);
	}

	// Handle notification events.
	@Override
	public void onInvitationReceived(Invitation invitation) {
		Toast.makeText(
				this,
				"An invitation has arrived from "
						+ invitation.getInviter().getDisplayName(), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onInvitationRemoved(String invitationId) {
		Toast.makeText(this, "An invitation was removed.", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onTurnBasedMatchReceived(TurnBasedMatch match) {
		Toast.makeText(this, "A match was updated.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onTurnBasedMatchRemoved(String matchId) {
		Toast.makeText(this, "A match was removed.", Toast.LENGTH_SHORT).show();
	}

	public void onTurnBasedMatchUpdated(int statusCode, TurnBasedMatch match) {
		dismissSpinner();
		if (!checkStatusCode(match, statusCode)) {
			return;
		}
		if (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN)
			updateMatch(match);
	}

	public void onTurnBasedMatchesLoaded(int statusCode,
			LoadMatchesResponse response) {
		// Not use
	}

	@Override
	public void onSignInFailed() {
		cancelPending();
	}

	@Override
	public void onSignInSucceeded() {
		if (mPending == Pending.LEADERBOARD) {
			showLeaderboard();
		} else if (mPending == Pending.ACHIEVEMENT) {
			showAchievement();
		} else if (mPending == Pending.MULTIPLAYER) {
			showMultiplayer();
		} else if (mPending == Pending.QUICKMATCH) {
			onQuickMatchClicked();
		} else if (mPending == Pending.STARTMATCH) {
			onStartMatchClicked();
		} else if (mPending == Pending.CHECKGAME) {
			onCheckGamesClicked();
		}
		cancelPending();
	}

	// Returns false if something went wrong, probably. This should handle
	// more cases, and probably report more accurate results.
	private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
		switch (statusCode) {
		case GamesStatusCodes.STATUS_OK:
			return true;
		case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
			// This is OK; the action is stored by Google Play Services and will
			// be dealt with later.
			showErrorMessage(match, statusCode,
					R.string.game_network_error);
			return true;
		case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
			showErrorMessage(match, statusCode,
					R.string.status_multiplayer_error_not_trusted_tester);
			break;
		case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
			showErrorMessage(match, statusCode,
					R.string.match_error_already_rematched);
			break;
		case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
			showErrorMessage(match, statusCode,
					R.string.network_error_operation_failed);
			break;
		case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
			getGameHelper().reconnectClient();
			showErrorMessage(match, statusCode, R.string.client_reconnect_required);
			break;
		case GamesStatusCodes.STATUS_INTERNAL_ERROR:
			showErrorMessage(match, statusCode, R.string.internal_error);
			break;
		case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
			showErrorMessage(match, statusCode, R.string.match_error_inactive_match);
			break;
		case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
			showErrorMessage(match, statusCode, R.string.match_error_locally_modified);
			break;
		default:
			showErrorMessage(match, statusCode, R.string.unexpected_status);
			Log.d("Log", "Did not have warning or string to deal with: " + statusCode);
			break;
		}
		return false;
	}

	public void showErrorMessage(TurnBasedMatch match, int statusCode,
			int stringId) {
		showWarning(getResources().getString(R.string.warning), getResources().getString(stringId));
	}

	public void showWarning(String title, String message) {
		new AlertFragment().setTitle(title).setMessage(message).hideNoBtn()
				.setPositiveButton(R.string.ok, null)
				.show(getSupportFragmentManager(), AlertFragment.class.getName());
	}

	public void continueMatch(NumberSearchTurn data, String myId, String matchId) {
		Games.TurnBasedMultiplayer.takeTurn(getApiClient(), matchId,
				data.persist(), myId);
	}

	/**
	 * Get the next participant. In this function, we assume that we are
	 * round-robin, with all known players going before all automatch players.
	 * This is not a requirement; players can go in any order. However, you can
	 * take turns in any order.
	 * 
	 * @return participantId of next player, or null if automatching
	 */
	public String getNextParticipantId(TurnBasedMatch match) {
		String myParticipantId = match.getParticipantId(Games.Players
				.getCurrentPlayerId(getApiClient()));
		ArrayList<String> participantIds = match.getParticipantIds();
		int desiredIndex = -1;
		for (int i = 0; i < participantIds.size(); i++) {
			if (participantIds.get(i).equals(myParticipantId)) {
				desiredIndex = i + 1;
			}
		}
		if (desiredIndex < participantIds.size())
			return participantIds.get(desiredIndex);
		if (match.getAvailableAutoMatchSlots() <= 0)
			// You've run out of automatch slots, so we start over.
			return participantIds.get(0);
		else
			// You have not yet fully automatched, so null will find a new
			// person to play against.
			return null;
	}

	// This is the main function that gets called when players choose a match
	// from the in box, or else create a match and want to start it.
	public void updateMatch(TurnBasedMatch match) {
		int status = match.getStatus();
		int turnStatus = match.getTurnStatus();

		switch (status) {
		case TurnBasedMatch.MATCH_STATUS_CANCELED:
			showWarning(getString(R.string.canceled_title),
					getString(R.string.canceled_content));
			return;
		case TurnBasedMatch.MATCH_STATUS_EXPIRED:
			showWarning(getString(R.string.expire_title),
					getString(R.string.expire_content));
			return;
		case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
			showWarning(getString(R.string.auto_matching_title),
					getString(R.string.auto_matching_content));
			return;
		case TurnBasedMatch.MATCH_STATUS_COMPLETE:
			if (turnStatus != TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
				Games.TurnBasedMultiplayer.finishMatch(getApiClient(),
						match.getMatchId());
			}
			NumberSearchTurn data = NumberSearchTurn.unpersist(match.getData());
			String myParticipantId = match.getParticipantId(Games.Players
					.getCurrentPlayerId(getApiClient()));
			String oppParticipantId = getNextParticipantId(match);
			String p1Name = match.getParticipant(myParticipantId).getDisplayName();
			String p2Name = match.getParticipant(oppParticipantId).getDisplayName();
			Bundle args = new Bundle();
			args.putString("gameType", GameInfo.WORD_SEARCH_MULTIPLAYER);
			args.putString("p1Name", p1Name);
			args.putString("p2Name", p2Name);
			if (myParticipantId.equals(match.getCreatorId())) {
				args.putInt("p1Found", data.mP1Found);
				args.putInt("p1TimeLeft", data.mP1TimeLeft);
				args.putInt("p2Found", data.mP2Found);
				args.putInt("p2TimeLeft", data.mP2TimeLeft);
			} else {
				args.putInt("p1Found", data.mP2Found);
				args.putInt("p1TimeLeft", data.mP2TimeLeft);
				args.putInt("p2Found", data.mP1Found);
				args.putInt("p2TimeLeft", data.mP1TimeLeft);
			}
			int result;
			if (args.getInt("p1Found") + args.getInt("p1TimeLeft") > args
					.getInt("p2Found") + args.getInt("p2TimeLeft")) {
				result = 1;
				mOutbox.mEasy = true;
				mOutbox.mFightingStep++;
				pushAccomplishments();
			} else if (args.getInt("p1Found") + args.getInt("p1TimeLeft") == args
					.getInt("p2Found") + args.getInt("p2TimeLeft"))
				result = 0;
			else
				result = -1;
			args.putInt("result", result);
			Uri p1AvatarUri = match.getParticipant(myParticipantId)
					.getHiResImageUri();
			if (p1AvatarUri != null)
				args.putString("p1AvatarUri", p1AvatarUri.toString());
			if (oppParticipantId != null) {
				Uri p2AvatarUri = match.getParticipant(oppParticipantId)
						.getHiResImageUri();
				if (p2AvatarUri != null)
					args.putString("p2AvatarUri", p2AvatarUri.toString());
			}
			showMultiplayerResult(args);
			return;
		}

		// OK, it's active. Check on turn status.
		switch (turnStatus) {
		case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
			NumberSearchTurn data = NumberSearchTurn.unpersist(match.getData());
			Bundle args = new Bundle();
			String myParticipantId = match.getParticipantId(Games.Players
					.getCurrentPlayerId(getApiClient()));
			String oppParticipantId = getNextParticipantId(match);
			String p1Name = match.getParticipant(myParticipantId).getDisplayName();
			String p2Name = getString(R.string.default_name);
			if (oppParticipantId != null)
				p2Name = match.getParticipant(oppParticipantId).getDisplayName();
			args.putString("gameType", GameInfo.WORD_SEARCH_MULTIPLAYER);
			args.putString("matchId", match.getMatchId());
			args.putString("myId", myParticipantId);
			args.putString("oppId", oppParticipantId);
			args.putInt("level", data.mLevel);
			args.putString("p1Name", p1Name);
			args.putString("p2Name", p2Name);
			args.putInt("p1TimeLeft", data.mP1TimeLeft);
			args.putInt("p1Found", data.mP1Found);
			args.putInt("p2TimeLeft", data.mP2TimeLeft);
			args.putInt("p2Found", data.mP2Found);
			Uri p1AvatarUri = match.getParticipant(myParticipantId)
					.getHiResImageUri();
			if (p1AvatarUri != null)
				args.putString("p1AvatarUri", p1AvatarUri.toString());
			if (oppParticipantId != null) {
				Uri p2AvatarUri = match.getParticipant(oppParticipantId)
						.getHiResImageUri();
				if (p2AvatarUri != null)
					args.putString("p2AvatarUri", p2AvatarUri.toString());
			}
			showSpinner();
			startGame(args);
			return;
		case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
			// Should return results.
			showWarning(getString(R.string.their_turn_title),
					getString(R.string.their_turn_content));
			break;
		case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
			showWarning(getString(R.string.invited_title),
					getString(R.string.invited_content));
		}
	}
	
	private void showMultiplayerResult(Bundle args) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ResultMultiplayerFragment rmf = new ResultMultiplayerFragment();
		rmf.setArguments(args);
		ft.replace(R.id.root, rmf, ResultMultiplayerFragment.class.getName());
		ft.addToBackStack(ResultMultiplayerFragment.class.getName());
		ft.commit();
	}

	// This function is what gets called when you return from either the Play
	// Games built-in in box, or else the create game built-in interface.
	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		if (response != Activity.RESULT_OK)
			// user canceled
			return;
		if (request == RC_LOOK_AT_MATCHES) {
			// Returning from the 'Select Match' dialog
			TurnBasedMatch match = data
					.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);
			if (match != null)
				updateMatch(match);
		} else if (request == RC_SELECT_PLAYERS) {
			// Returned from 'Select players to Invite' dialog
			// get the invite list
			final ArrayList<String> invitees = data
					.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
			// get auto match criteria
			Bundle autoMatchCriteria = null;
			int minAutoMatchPlayers = data.getIntExtra(
					Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
			int maxAutoMatchPlayers = data.getIntExtra(
					Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
			if (minAutoMatchPlayers > 0)
				autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
						minAutoMatchPlayers, maxAutoMatchPlayers, 0);
			else
				autoMatchCriteria = null;
			TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
					.addInvitedPlayers(invitees).setAutoMatchCriteria(autoMatchCriteria)
					.build();
			// Kick the match off
			Games.TurnBasedMultiplayer.createMatch(getApiClient(), tbmc)
					.setResultCallback(this);
			showSpinner();
		}
	}
}

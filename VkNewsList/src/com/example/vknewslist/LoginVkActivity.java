package com.example.vknewslist;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;

public class LoginVkActivity extends Activity {

	private static final String[] sMyScope = new String[] { VKScope.WALL,
			VKScope.FRIENDS };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		VKUIHelper.onCreate(this);
		VKSdk.initialize(sdkListener, "4422684");
		
		if (VKSdk.wakeUpSession()) {
			startNewsActivity();
			return;
		}
	}

	private final VKSdkListener sdkListener = new VKSdkListener() {
		@Override
		public void onCaptchaError(VKError captchaError) {
			new VKCaptchaDialog(captchaError).show();
		}

		@Override
		public void onTokenExpired(VKAccessToken expiredToken) {
			VKSdk.authorize(sMyScope);
		}

		@Override
		public void onAccessDenied(final VKError authorizationError) {
			new AlertDialog.Builder(VKUIHelper.getTopActivity()).setMessage(
					authorizationError.toString()).show();
		}

		@Override
		public void onReceiveNewToken(VKAccessToken newToken) {
			startNewsActivity();
		}

		@Override
		public void onAcceptUserToken(VKAccessToken token) {
			startNewsActivity();
		}
	};


	@Override
	protected void onResume() {
		super.onResume();
		VKUIHelper.onResume(this);
		if (VKSdk.isLoggedIn()) {
			startNewsActivity();
		} else {
			VKSdk.authorize(sMyScope, true, false);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VKUIHelper.onDestroy(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
	}

	private void startNewsActivity() {
		startActivity(new Intent(this, NewsActivity.class));
	}

	

}

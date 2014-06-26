package com.example.vknewslist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ListAdapter;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiCommunityArray;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKPostArray;
import com.vk.sdk.api.model.VKUsersArray;

public class WallPostEndlessAdapter extends EndlessAdapter {
	private RotateAnimation rotate = null;
	private View pendingView = null;

	PostsListAdapter adapter;
	VKPostArray p = new VKPostArray();

	public WallPostEndlessAdapter(ListAdapter wrapped) {
		super(wrapped);
		rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(600);
		rotate.setRepeatMode(Animation.RESTART);
		rotate.setRepeatCount(Animation.INFINITE);
	}

	@Override
	protected View getPendingView(ViewGroup parent) {
		View row = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.row, null);

		pendingView = row.findViewById(android.R.id.text1);
		pendingView.setVisibility(View.GONE);
		pendingView = row.findViewById(R.id.throbber);
		pendingView.setVisibility(View.VISIBLE);
		startProgressAnimation();

		return (row);
	}

	@Override
	protected boolean cacheInBackground() throws Exception {
		return true;
	}

	@Override
	protected void appendCachedData() {
		adapter = (PostsListAdapter) getWrappedAdapter();
		VKRequest request = VKApi.news().get(
				VKParameters.from(VKApiConst.FILTERS, "post",
						VKApiConst.START_FROM,
						Integer.toString(adapter.startFrom), VKApiConst.COUNT,
						Integer.toString(adapter.count)));


		request.executeWithListener(new VKRequestListener() {
			@Override
			public void onComplete(VKResponse response) {
				Log.d("---", String.valueOf(adapter.startFrom));
				p = (VKPostArray) response.parsedModel;

				// получаем список пользователей, которые упоминаются в
				// пришедших постах
				VKUsersArray u = new VKUsersArray();
				u.fill(response.json.optJSONObject("response").optJSONArray(
						"profiles"), VKApiUser.class);

				// получаем список групп, которые упоминаются в пришедших постах
				VKApiCommunityArray g = new VKApiCommunityArray();
				g.fill(response.json.optJSONObject("response").optJSONArray(
						"groups"), VKApiCommunity.class);

				for (VKApiCommunity gr : g) {
					adapter.groups.put(gr.id, gr);
				}
				for (VKApiUser us : u) {
					adapter.users.put(us.id, us);
				}
				finish();
				adapter.startFrom += adapter.count + 1;
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onError(VKError error) {
				Log.d("Error", error.toString());
			}
		});

	}

	private void finish() {
		adapter.posts.addAll(p);
	}

	void startProgressAnimation() {
		if (pendingView != null) {
			pendingView.startAnimation(rotate);
		}
	}

}

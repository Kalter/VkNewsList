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
import com.vk.sdk.api.model.VKCommentArray;
import com.vk.sdk.api.model.VKUsersArray;

public class EndlessCommentAdapter extends EndlessAdapter {

	private RotateAnimation rotate = null;
	private View pendingView = null;
	CommentsAdapter adapter = (CommentsAdapter) getWrappedAdapter();

	public EndlessCommentAdapter(ListAdapter wrapped) {
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
		
		VKRequest request = VKApi.wall().getComments(
				VKParameters.from(VKApiConst.OWNER_ID, adapter.mPost.owner_id,
						VKApiConst.POST_ID, adapter.mPost.id,
						VKApiConst.NEED_LIKES, 1, VKApiConst.OFFSET,
						adapter.mOffset, VKApiConst.COUNT, adapter.mCount,
						VKApiConst.EXTENDED, 1));

		request.executeWithListener(new VKRequestListener() {
			@Override
			public void onComplete(VKResponse response) {
				VKCommentArray comments = ((VKCommentArray) response.parsedModel);
				// получаем список пользователей, которые упоминаются в
				// пришедших постах
				VKUsersArray users = new VKUsersArray();
				users.fill(response.json.optJSONObject("response").optJSONArray(
						"profiles"), VKApiUser.class);

				// получаем список групп, которые упоминаются в пришедших постах
				VKApiCommunityArray groups = new VKApiCommunityArray();
				groups.fill(response.json.optJSONObject("response").optJSONArray(
						"groups"), VKApiCommunity.class);

				finish(comments, users, groups);
			}

			@Override
			public void onError(VKError error) {
				Log.d("Error", error.toString());
			}
		});
		
		boolean m = adapter.k;
		if (adapter.mPost.comments_count <= adapter.mOffset + adapter.mCount) {
			adapter.k = false;
		}
		return m;
	}

	@Override
	protected void appendCachedData() {
	}

	void finish(VKCommentArray comments, VKUsersArray users, VKApiCommunityArray groups) {
		CommentsAdapter adapter = (CommentsAdapter) getWrappedAdapter();
		adapter.mComments.addAll(comments);
		for (VKApiCommunity group : groups) {
			adapter.mGroups.put(group.id, group);
		}
		for (VKApiUser user : users) {
			adapter.mUsers.put(user.id, user);
		}
		adapter.mOffset += adapter.mCount;
		adapter.notifyDataSetChanged();
	}

	void startProgressAnimation() {
		if (pendingView != null) {
			pendingView.startAnimation(rotate);
		}
	}

}

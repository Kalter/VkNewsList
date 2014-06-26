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
		boolean m = adapter.k;
		if (adapter.post.comments_count <= adapter.offset + adapter.count){
			adapter.k = false;
		}
		return m;
	}

	@Override
	protected void appendCachedData() {
		
		VKRequest request = VKApi.wall().getComments(
				VKParameters.from(VKApiConst.OWNER_ID, adapter.post.owner_id,
						VKApiConst.POST_ID, adapter.post.id,
						VKApiConst.NEED_LIKES, 1, VKApiConst.OFFSET,
						adapter.offset, VKApiConst.COUNT, adapter.count,
						VKApiConst.EXTENDED, 1));

		request.executeWithListener(new VKRequestListener() {
			@Override
			public void onComplete(VKResponse response) {
				VKCommentArray comments = ((VKCommentArray) response.parsedModel);
				// получаем список пользователей, которые упоминаются в
				// пришедших постах
				VKUsersArray u = new VKUsersArray();
				u.fill(response.json.optJSONObject("response").optJSONArray(
						"profiles"), VKApiUser.class);

				// получаем список групп, которые упоминаются в пришедших постах
				VKApiCommunityArray g = new VKApiCommunityArray();
				g.fill(response.json.optJSONObject("response").optJSONArray(
						"groups"), VKApiCommunity.class);

				finish(comments, u, g);
			}

			@Override
			public void onError(VKError error) {
				Log.d("Error", error.toString());
			}
		});
	}

	void finish(VKCommentArray comments, VKUsersArray u, VKApiCommunityArray g) {
		CommentsAdapter adapter = (CommentsAdapter) getWrappedAdapter();
		adapter.comments.addAll(comments);
		for (VKApiCommunity gr : g) {
			adapter.groups.put(gr.id, gr);
		}
		for (VKApiUser us : u) {
			adapter.users.put(us.id, us);
		}
		adapter.offset += adapter.count;
		adapter.notifyDataSetChanged();
	}

	void startProgressAnimation() {
		if (pendingView != null) {
			pendingView.startAnimation(rotate);
		}
	}

}

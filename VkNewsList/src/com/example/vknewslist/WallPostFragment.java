package com.example.vknewslist;

import java.util.HashMap;
import java.util.Map;

import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiCommunityArray;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKCommentArray;
import com.vk.sdk.api.model.VKUsersArray;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class WallPostFragment extends Fragment {

	VKApiPost post = new VKApiPost();
	VKCommentArray comments = new VKCommentArray();
	Map<Integer, VKApiUser> users = new HashMap<Integer, VKApiUser>();
	Map<Integer, VKApiCommunity> groups = new HashMap<Integer, VKApiCommunity>();
	String id;
	Bundle savedInstanceState;
	ViewGroup container;
	CommentsAdapter adapter;
	Integer count = 1;
	Integer offset = 0;
	EndlessCommentAdapter endlessAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		VKUIHelper.onCreate(getActivity());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VKUIHelper.onDestroy(getActivity());
		
	}

	@Override
	public void onResume() {
		super.onResume();
		VKUIHelper.onResume(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		addComments();
		this.savedInstanceState = savedInstanceState;
		View view = inflater.inflate(R.layout.wall_post_fragment, container,
				false);

		return view;
	}

	public void addComments() {
		VKRequest request = VKApi.wall().getComments(
				VKParameters.from(VKApiConst.OWNER_ID, post.owner_id,
						VKApiConst.POST_ID, post.id, VKApiConst.NEED_LIKES, 1,
						VKApiConst.OFFSET, offset, VKApiConst.COUNT, count,
						VKApiConst.EXTENDED, 1));

		request.executeWithListener(new VKRequestListener() {
			@Override
			public void onComplete(VKResponse response) {
				comments = (VKCommentArray) response.parsedModel;
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
					groups.put(gr.id, gr);
				}
				for (VKApiUser us : u) {
					users.put(us.id, us);
				}
				((ListView) getView().findViewById(R.id.comments))
						.addHeaderView(((new PostView(post, users, groups,
								null, getLayoutInflater(savedInstanceState),
								getActivity())).setupPostView(container)));
				setupAdapter();
				adapter.offset += count;
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onError(VKError error) {
				Log.d("Error", error.toString());
			}
		});
	}

	public void setupAdapter() {
		setRetainInstance(true);
		adapter = new CommentsAdapter(getActivity(), comments, users, groups,
				count, offset, post);
		if (endlessAdapter == null) {
			endlessAdapter = new EndlessCommentAdapter(adapter);
		} else {
			endlessAdapter.startProgressAnimation();
		}
		ListView lv = (ListView) getView().findViewById(R.id.comments);

		lv.setAdapter(endlessAdapter);
	}
	
	
}

package com.example.vknewslist;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiCommunityArray;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKPostArray;
import com.vk.sdk.api.model.VKUsersArray;

public class PostsListFragment extends SwipeRefreshListFragment implements
		OnItemClickListener {

	public interface PostIdListener {
		public void setId(String s);
	}

	PostIdListener idListener;
	VKPostArray posts = new VKPostArray();
	Map<Integer, VKApiUser> users = new HashMap<Integer, VKApiUser>();
	Map<Integer, VKApiCommunity> groups = new HashMap<Integer, VKApiCommunity>();
	PostsListAdapter adapter;
	WallPostEndlessAdapter endlessAdapter;

	/**
	 * Number of post that start downloading
	 */
	Integer startFrom = 0;

	/**
	 * Count of loaded posts
	 */
	Integer count = 30;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		VKUIHelper.onCreate(getActivity());
		getActivity().setTitle(R.string.news);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
	          idListener = (PostIdListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement PostIdListener");
	        }
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);
		getListView().setOnItemClickListener(this);
		addPosts(startFrom, count);

		setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				initiateRefresh();
			}
		});
	}

	public void addPosts(Integer s, Integer c) {
		VKRequest request = VKApi.news().get(
				VKParameters.from(VKApiConst.FILTERS, "post",
						VKApiConst.START_FROM, Integer.toString(s),
						VKApiConst.COUNT, Integer.toString(c)));

		request.useSystemLanguage = false;

		request = VKRequest.getRegisteredRequest(request.registerObject());
		if (request != null) {
			request.unregisterObject();
		}

		request.executeWithListener(new VKRequestListener() {
			@Override
			public void onComplete(VKResponse response) {
				posts = (VKPostArray) response.parsedModel;

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
				setupAdapters();
				startFrom += count;
				adapter.startFrom = startFrom;
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onError(VKError error) {
				Log.d("Error", error.toString());
			}
		});
	}

	private void setupAdapters() {
		setRetainInstance(true);
		adapter = new PostsListAdapter(getActivity(), posts, users, groups,
				startFrom, count);
		if (endlessAdapter == null) {
			endlessAdapter = new WallPostEndlessAdapter(adapter);
		} else {
			endlessAdapter.startProgressAnimation();
		}
		setListAdapter(endlessAdapter);
	}

	private void initiateRefresh() {
		startFrom = 0;
		posts = new VKPostArray();
		addPosts(startFrom, count);
		adapter = null;
		setupAdapters();
		setRefreshing(false);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		idListener.setId(adapter.posts.get(position).source_id + "_"
				+ adapter.posts.get(position).post_id);
	}

}
